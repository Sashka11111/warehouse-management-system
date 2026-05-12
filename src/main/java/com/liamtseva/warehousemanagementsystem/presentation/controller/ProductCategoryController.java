package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductCategoryRepositoryImpl;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProductCategoryController {

    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<ProductCategory> categoryTable;
    @FXML
    private TableColumn<ProductCategory, String> nameColumn;
    @FXML
    private TableColumn<ProductCategory, String> descriptionColumn;

    private final ProductCategoryRepository repository;
    private final ObservableList<ProductCategory> categoryData = FXCollections.observableArrayList();
    private FilteredList<ProductCategory> filteredData;
    private ProductCategory selectedCategory;

    public ProductCategoryController() {
        this.repository = new ProductCategoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        filteredData = new FilteredList<>(categoryData, p -> true);
        setupTable();
        loadCategories();
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
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().categoryName()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().description()));
        
        SortedList<ProductCategory> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(categoryTable.comparatorProperty());
        categoryTable.setItems(sortedData);
        
        Label placeholder = new Label("Нічого не знайдено за вашим запитом");
        placeholder.getStyleClass().add("table-placeholder");
        categoryTable.setPlaceholder(placeholder);
    }

    private void loadCategories() {
        List<ProductCategory> categories = repository.findAll();
        categoryData.setAll(categories);
    }

    private void setupListeners() {
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCategory = newSelection;
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filteredData.setPredicate(category -> {
                if (newText == null || newText.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newText.toLowerCase();
                return category.categoryName().toLowerCase().contains(lowerCaseFilter) ||
                       (category.description() != null && category.description().toLowerCase().contains(lowerCaseFilter));
            });
        });

        deleteButton.setOnAction(e -> handleDelete());
    }


    @FXML
    private void openAddDialog() {
        showFormDialog(null);
    }

    @FXML
    private void openEditDialog() {
        if (selectedCategory == null) {
            AlertController.showAlert("Будь ласка, виберіть категорію для редагування");
            return;
        }
        showFormDialog(selectedCategory);
    }

    private void showFormDialog(ProductCategory category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/category_form.fxml"));
            VBox root = loader.load();
            CategoryFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);

            controller.setStage(stage);
            controller.setCategoryToEdit(category);
            controller.setOnSaveCallback(this::loadCategories);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження форми: " + e.toString());
        }
    }

    private void handleDelete() {
        if (selectedCategory == null) {
            AlertController.showAlert("Будь ласка, виберіть категорію для видалення");
            return;
        }

        try {
            repository.deleteById(selectedCategory.categoryId());
            loadCategories();
            AlertController.showInfo("Категорію видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }
}
