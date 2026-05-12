package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductCategoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ProductCategoryValidator;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoryFormController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label formTitleLabel;
    @FXML
    private Label formSubtitleLabel;
    @FXML
    private Button saveButton;

    private Stage stage;
    private final ProductCategoryRepository repository;
    private ProductCategory categoryToEdit;
    private Runnable onSaveCallback;

    public CategoryFormController() {
        this.repository = new ProductCategoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCategoryToEdit(ProductCategory category) {
        this.categoryToEdit = category;
        if (category != null) {
            formTitleLabel.setText("Редагувати категорію");
            formSubtitleLabel.setText("Оновіть інформацію про категорію");
            nameField.setText(category.categoryName());
            descriptionField.setText(category.description());
            saveButton.setText("Оновити дані");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();

            UUID categoryId = (categoryToEdit != null) ? categoryToEdit.categoryId() : null;
            ValidationResult validation = ProductCategoryValidator.isNameUnique(name, categoryId, repository);
            
            if (!validation.isValid()) {
                AlertController.showAlert(validation.getErrors().get(0));
                return;
            }

            if (categoryToEdit == null) {
                ProductCategory newCategory = new ProductCategory(UUID.randomUUID(), name, description);
                repository.create(newCategory);
                AlertController.showInfo("Категорію успішно додано");
            } else {
                ProductCategory updated = new ProductCategory(categoryToEdit.categoryId(), name, description);
                repository.update(updated);
                AlertController.showInfo("Категорію оновлено");
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            stage.close();
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        stage.close();
    }
}
