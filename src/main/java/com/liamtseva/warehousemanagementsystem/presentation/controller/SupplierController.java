package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SupplierController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Supplier> supplierTable;
    @FXML
    private TableColumn<Supplier, String> nameColumn;
    @FXML
    private TableColumn<Supplier, String> contactColumn;
    @FXML
    private TableColumn<Supplier, String> emailColumn;
    @FXML
    private TableColumn<Supplier, String> phoneColumn;
    @FXML
    private TableColumn<Supplier, String> addressColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final SupplierRepository supplierRepository;
    private ObservableList<Supplier> supplierList;
    private Supplier selectedSupplier;

    public SupplierController() {
        this.supplierRepository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        contactColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().contactPerson()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().phone()));
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().address()));

        loadSuppliers();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchSuppliers(newVal));

        addButton.setOnAction(event -> addSupplier());
        editButton.setOnAction(event -> editSupplier());
        deleteButton.setOnAction(event -> deleteSupplier());
        clearButton.setOnAction(event -> clearFields());

        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedSupplier = newVal;
            if (newVal != null) {
                nameField.setText(newVal.name());
                contactField.setText(newVal.contactPerson());
                emailField.setText(newVal.email());
                phoneField.setText(newVal.phone());
                addressField.setText(newVal.address());
            }
        });
    }

    private void loadSuppliers() {
        supplierList = FXCollections.observableArrayList(supplierRepository.findAll());
        supplierTable.setItems(supplierList);
    }

    private void searchSuppliers(String text) {
        if (text == null || text.isEmpty()) {
            supplierTable.setItems(supplierList);
            return;
        }
        String lowerText = text.toLowerCase();
        List<Supplier> filtered = supplierList.stream()
                .filter(s -> s.name().toLowerCase().contains(lowerText) || s.contactPerson().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
        supplierTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void addSupplier() {
        try {
            Supplier supplier = getSupplierFromFields(UUID.randomUUID());
            supplierRepository.create(supplier);
            loadSuppliers();
            clearFields();
            AlertController.showInfo("Постачальника додано");
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    private void editSupplier() {
        if (selectedSupplier == null) {
            AlertController.showAlert("Виберіть постачальника");
            return;
        }
        try {
            Supplier updated = getSupplierFromFields(selectedSupplier.supplierId());
            supplierRepository.update(updated);
            loadSuppliers();
            clearFields();
            AlertController.showInfo("Дані оновлено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    private void deleteSupplier() {
        if (selectedSupplier == null) {
            AlertController.showAlert("Виберіть постачальника");
            return;
        }
        try {
            supplierRepository.deleteById(selectedSupplier.supplierId());
            loadSuppliers();
            clearFields();
            AlertController.showInfo("Постачальника видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    private Supplier getSupplierFromFields(UUID id) {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {
            throw new RuntimeException("Назва компанії обов'язкова");
        }

        return new Supplier(id, name, contact, email, phone, address);
    }

    private void clearFields() {
        nameField.clear();
        contactField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        selectedSupplier = null;
        supplierTable.getSelectionModel().clearSelection();
    }
}
