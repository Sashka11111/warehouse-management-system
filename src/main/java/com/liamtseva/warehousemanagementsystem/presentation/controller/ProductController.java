package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductCategoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProductController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> skuColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, String> supplierColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, String> unitColumn;
    @FXML private TableColumn<Product, Integer> minStockColumn;

    private final ProductRepository repository;
    private final ProductCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    
    private final ObservableList<Product> productData = FXCollections.observableArrayList();
    private FilteredList<Product> filteredData;
    private Map<UUID, ProductCategory> categories;
    private Map<UUID, Supplier> suppliers;
    private Product selectedProduct;

    public ProductController() {
        this.repository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.categoryRepository = new ProductCategoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.supplierRepository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        filteredData = new FilteredList<>(productData, p -> true);
        setupTable();
        loadProducts();
        setupListeners();
        checkPermissions();
    }

    private void checkPermissions() {
        com.liamtseva.warehousemanagementsystem.persistence.entity.User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.role() == UserRole.OPERATOR) {
            addButton.setVisible(false);
            addButton.setManaged(false);
            editButton.setVisible(false);
            editButton.setManaged(false);
            deleteButton.setVisible(false);
            deleteButton.setManaged(false);
        }
    }

    private void setupTable() {
        skuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sku()));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        categoryColumn.setCellValueFactory(data -> {
            ProductCategory cat = categories.get(data.getValue().categoryId());
            return new SimpleStringProperty(cat != null ? cat.categoryName() : "Невідомо");
        });
        supplierColumn.setCellValueFactory(data -> {
            Supplier sup = suppliers.get(data.getValue().supplierId());
            return new SimpleStringProperty(sup != null ? sup.name() : "Невідомо");
        });
        priceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().price()));
        unitColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().unit()));
        minStockColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().minStockLevel()));
        
        SortedList<Product> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(productTable.comparatorProperty());
        productTable.setItems(sortedData);
        
        Label placeholder = new Label("Нічого не знайдено за вашим запитом");
        placeholder.getStyleClass().add("table-placeholder");
        productTable.setPlaceholder(placeholder);
    }

    private void loadProducts() {
        categories = categoryRepository.findAll().stream()
            .collect(Collectors.toMap(ProductCategory::categoryId, c -> c));
        suppliers = supplierRepository.findAll().stream()
            .collect(Collectors.toMap(Supplier::supplierId, s -> s));
        
        List<Product> products = repository.findAll();
        productData.setAll(products);
        
        ObservableList<String> categoryNames = FXCollections.observableArrayList("Всі категорії");
        categoryNames.addAll(categories.values().stream()
            .map(ProductCategory::categoryName)
            .sorted()
            .collect(Collectors.toList()));
        categoryFilter.setItems(categoryNames);
        categoryFilter.getSelectionModel().selectFirst();
    }

    private void setupListeners() {
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedProduct = newSelection;
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getValue();
        
        filteredData.setPredicate(product -> {
            boolean matchesText = searchText.isEmpty() ||
                product.sku().toLowerCase().contains(searchText) ||
                product.name().toLowerCase().contains(searchText);
            
            if (!matchesText) {
                ProductCategory cat = categories.get(product.categoryId());
                if (cat != null && cat.categoryName().toLowerCase().contains(searchText)) {
                    matchesText = true;
                }
                Supplier sup = suppliers.get(product.supplierId());
                if (sup != null && sup.name().toLowerCase().contains(searchText)) {
                    matchesText = true;
                }
            }

            boolean matchesCategory = selectedCategory == null ||
                                     selectedCategory.equals("Всі категорії") ||
                                     categories.get(product.categoryId()).categoryName().equals(selectedCategory);

            return matchesText && matchesCategory;
        });
    }


    @FXML
    private void openAddDialog() {
        showFormDialog(null);
    }

    @FXML
    private void openEditDialog() {
        if (selectedProduct == null) {
            AlertController.showAlert("Будь ласка, виберіть товар для редагування");
            return;
        }
        showFormDialog(selectedProduct);
    }

    private void showFormDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/product_form.fxml"));
            VBox root = loader.load();
            ProductFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);

            controller.setStage(stage);
            controller.setProductToEdit(product);
            controller.setOnSaveCallback(this::loadProducts);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження форми: " + e.toString());
        }
    }

    @FXML
    private void deleteProduct() {
        if (selectedProduct == null) {
            AlertController.showAlert("Будь ласка, виберіть товар для видалення");
            return;
        }

        try {
            repository.deleteById(selectedProduct.productId());
            loadProducts();
            AlertController.showInfo("Товар видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }
}
