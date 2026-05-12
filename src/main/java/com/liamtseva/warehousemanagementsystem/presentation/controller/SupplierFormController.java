package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.presentation.validation.SupplierValidator;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SupplierFormController {

    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private Label formTitleLabel;
    @FXML private Label formSubtitleLabel;
    @FXML private Button saveButton;

    private Stage stage;
    private final SupplierRepository repository;
    private Supplier supplierToEdit;
    private Runnable onSaveCallback;

    public SupplierFormController() {
        this.repository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSupplierToEdit(Supplier supplier) {
        this.supplierToEdit = supplier;
        if (supplier != null) {
            formTitleLabel.setText("Редагувати постачальника");
            formSubtitleLabel.setText("Оновіть контактну інформацію");
            nameField.setText(supplier.name());
            contactField.setText(supplier.contactPerson());
            emailField.setText(supplier.email());
            phoneField.setText(supplier.phone());
            addressField.setText(supplier.address());
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
            String contact = contactField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            Supplier supplierData = new Supplier(
                supplierToEdit == null ? null : supplierToEdit.supplierId(),
                name, contact, email, phone, address
            );
            
            ValidationResult result = SupplierValidator.isSupplierValid(supplierData, repository);
            if (!result.isValid()) {
                AlertController.showAlert(String.join("\n", result.getErrors()));
                return;
            }
            
            if (supplierToEdit == null) {
                Supplier newSupplier = new Supplier(UUID.randomUUID(), name, contact, email, phone, address);
                repository.create(newSupplier);
                AlertController.showInfo("Постачальника додано");
            } else {
                Supplier updated = new Supplier(supplierToEdit.supplierId(), name, contact, email, phone, address);
                repository.update(updated);
                AlertController.showInfo("Дані оновлено");
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
