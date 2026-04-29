package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductCategoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ProductCategoryValidator;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductCategoryController {

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private TableView<ProductCategory> categoryTable;
    @FXML
    private TableColumn<ProductCategory, String> nameColumn;
    @FXML
    private TableColumn<ProductCategory, String> descriptionColumn;

    private final ProductCategoryRepository repository;
    private final ObservableList<ProductCategory> categoryData = FXCollections.observableArrayList();
    private ProductCategory selectedCategory;

    public ProductCategoryController() {
        this.repository = new ProductCategoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        setupTable();
        loadCategories();
        setupListeners();
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().categoryName()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().description()));
        categoryTable.setItems(categoryData);
    }

    private void loadCategories() {
        List<ProductCategory> categories = repository.findAll();
        categoryData.setAll(categories);
    }

    private void setupListeners() {
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCategory = newSelection;
                nameField.setText(newSelection.categoryName());
                descriptionField.setText(newSelection.description());
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterCategories(newText);
        });

        addButton.setOnAction(e -> handleAdd());
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> clearFields());
    }

    private void filterCategories(String query) {
        if (query == null || query.isEmpty()) {
            loadCategories();
        } else {
            String lowerCaseQuery = query.toLowerCase();
            List<ProductCategory> filtered = repository.findAll().stream()
                .filter(c -> c.categoryName().toLowerCase().contains(lowerCaseQuery) ||
                            (c.description() != null && c.description().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
            categoryData.setAll(filtered);
        }
    }

    private void handleAdd() {
        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();

            ValidationResult validation = ProductCategoryValidator.isNameUnique(name, null, repository);
            if (!validation.isValid()) {
                AlertController.showAlert(validation.getErrors().get(0));
                return;
            }

            ProductCategory category = new ProductCategory(UUID.randomUUID(), name, description);
            repository.create(category);
            loadCategories();
            clearFields();
            AlertController.showInfo("Категорію успішно додано");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при додаванні: " + e.getMessage());
        }
    }

    private void handleEdit() {
        if (selectedCategory == null) {
            AlertController.showAlert("Будь ласка, виберіть категорію для редагування");
            return;
        }

        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();

            ValidationResult validation = ProductCategoryValidator.isNameUnique(name, selectedCategory.categoryId(), repository);
            if (!validation.isValid()) {
                AlertController.showAlert(validation.getErrors().get(0));
                return;
            }

            ProductCategory updated = new ProductCategory(selectedCategory.categoryId(), name, description);
            repository.update(updated);
            loadCategories();
            clearFields();
            AlertController.showInfo("Категорію оновлено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при редагуванні: " + e.getMessage());
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
            clearFields();
            AlertController.showInfo("Категорію видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        selectedCategory = null;
        categoryTable.getSelectionModel().clearSelection();
    }
}
