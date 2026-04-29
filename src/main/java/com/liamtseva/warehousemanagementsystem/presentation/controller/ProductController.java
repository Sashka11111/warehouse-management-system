package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductCategoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class ProductController {

    @FXML
    private TextField skuField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<ProductCategory> categoryComboBox;
    @FXML
    private ComboBox<Supplier> supplierComboBox;
    @FXML
    private TextField unitField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField minStockField;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> skuColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, String> supplierColumn;
    @FXML
    private TableColumn<Product, String> priceColumn;
    @FXML
    private TableColumn<Product, String> unitColumn;
    @FXML
    private TableColumn<Product, String> minStockColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    private ObservableList<Product> productList;
    private Map<UUID, ProductCategory> categories;
    private Map<UUID, Supplier> suppliers;
    private Product selectedProduct;

    public ProductController() {
        this.productRepository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.categoryRepository = new ProductCategoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.supplierRepository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        loadData();

        skuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sku()));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        categoryColumn.setCellValueFactory(data -> {
            ProductCategory cat = categories.get(data.getValue().categoryId());
            return new SimpleStringProperty(cat != null ? cat.categoryName() : "");
        });
        supplierColumn.setCellValueFactory(data -> {
            Supplier sup = suppliers.get(data.getValue().supplierId());
            return new SimpleStringProperty(sup != null ? sup.name() : "");
        });
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().price())));
        unitColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().unit()));
        minStockColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().minStockLevel())));

        setupComboBoxes();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchProducts(newVal));

        addButton.setOnAction(event -> addProduct());
        editButton.setOnAction(event -> editProduct());
        deleteButton.setOnAction(event -> deleteProduct());
        clearButton.setOnAction(event -> clearFields());

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedProduct = newVal;
            populateFields(newVal);
        });
    }

    private void loadData() {
        productList = FXCollections.observableArrayList(productRepository.findAll());
        productTable.setItems(productList);

        categories = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(ProductCategory::categoryId, c -> c));
        suppliers = supplierRepository.findAll().stream()
                .collect(Collectors.toMap(Supplier::supplierId, s -> s));
    }

    private void setupComboBoxes() {
        categoryComboBox.setItems(FXCollections.observableArrayList(categories.values()));
        categoryComboBox.setConverter(new StringConverter<ProductCategory>() {
            @Override
            public String toString(ProductCategory object) {
                return object == null ? "" : object.categoryName();
            }
            @Override
            public ProductCategory fromString(String string) {
                return null;
            }
        });

        supplierComboBox.setItems(FXCollections.observableArrayList(suppliers.values()));
        supplierComboBox.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier object) {
                return object == null ? "" : object.name();
            }
            @Override
            public Supplier fromString(String string) {
                return null;
            }
        });
    }

    private void searchProducts(String text) {
        if (text == null || text.isEmpty()) {
            productTable.setItems(productList);
            return;
        }
        String lowerText = text.toLowerCase();
        List<Product> filtered = productList.stream()
                .filter(p -> p.name().toLowerCase().contains(lowerText) || p.sku().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
        productTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void addProduct() {
        try {
            Product product = getProductFromFields(UUID.randomUUID());
            productRepository.create(product);
            loadData();
            clearFields();
            AlertController.showInfo("Товар успішно додано");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при додаванні: " + e.getMessage());
        }
    }

    private void editProduct() {
        if (selectedProduct == null) {
            AlertController.showAlert("Виберіть товар для редагування");
            return;
        }
        try {
            Product updated = getProductFromFields(selectedProduct.productId());
            productRepository.update(updated);
            loadData();
            clearFields();
            AlertController.showInfo("Товар оновлено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при редагуванні: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        if (selectedProduct == null) {
            AlertController.showAlert("Виберіть товар для видалення");
            return;
        }
        try {
            productRepository.deleteById(selectedProduct.productId());
            loadData();
            clearFields();
            AlertController.showInfo("Товар видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }

    private Product getProductFromFields(UUID id) {
        String sku = skuField.getText().trim();
        String name = nameField.getText().trim();
        ProductCategory cat = categoryComboBox.getValue();
        Supplier sup = supplierComboBox.getValue();
        String unit = unitField.getText().trim();
        double price = Double.parseDouble(priceField.getText().trim());
        int minStock = Integer.parseInt(minStockField.getText().trim());

        if (sku.isEmpty() || name.isEmpty() || cat == null || sup == null) {
            throw new RuntimeException("Всі обов'язкові поля повинні бути заповнені");
        }

        return new Product(id, cat.categoryId(), sup.supplierId(), sku, name, "", unit, price, minStock);
    }

    private void populateFields(Product p) {
        if (p == null) {
            clearFields();
            return;
        }
        skuField.setText(p.sku());
        nameField.setText(p.name());
        categoryComboBox.setValue(categories.get(p.categoryId()));
        supplierComboBox.setValue(suppliers.get(p.supplierId()));
        unitField.setText(p.unit());
        priceField.setText(String.valueOf(p.price()));
        minStockField.setText(String.valueOf(p.minStockLevel()));
    }

    private void clearFields() {
        skuField.clear();
        nameField.clear();
        categoryComboBox.setValue(null);
        supplierComboBox.setValue(null);
        unitField.clear();
        priceField.clear();
        minStockField.clear();
        selectedProduct = null;
        productTable.getSelectionModel().clearSelection();
    }
}
