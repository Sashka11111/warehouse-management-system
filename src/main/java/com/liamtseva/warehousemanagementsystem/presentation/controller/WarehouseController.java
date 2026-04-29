package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Warehouse;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.WarehouseRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.WarehouseRepositoryImpl;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WarehouseController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Warehouse> warehouseTable;
    @FXML
    private TableColumn<Warehouse, String> nameColumn;
    @FXML
    private TableColumn<Warehouse, String> addressColumn;
    @FXML
    private TableColumn<Warehouse, String> capacityColumn;
    @FXML
    private TableColumn<Warehouse, String> dateColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final WarehouseRepository warehouseRepository;
    private ObservableList<Warehouse> warehouseList;
    private Warehouse selectedWarehouse;

    public WarehouseController() {
        this.warehouseRepository = new WarehouseRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().address()));
        capacityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().capacitySqm())));
        dateColumn.setCellValueFactory(data -> {
            if (data.getValue().createdAt() != null) {
                return new SimpleStringProperty(data.getValue().createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            return new SimpleStringProperty("");
        });

        loadWarehouses();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchWarehouses(newVal));

        addButton.setOnAction(event -> addWarehouse());
        editButton.setOnAction(event -> editWarehouse());
        deleteButton.setOnAction(event -> deleteWarehouse());
        clearButton.setOnAction(event -> clearFields());

        warehouseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedWarehouse = newVal;
            if (newVal != null) {
                nameField.setText(newVal.name());
                addressField.setText(newVal.address());
                capacityField.setText(String.valueOf(newVal.capacitySqm()));
            }
        });
    }

    private void loadWarehouses() {
        warehouseList = FXCollections.observableArrayList(warehouseRepository.findAll());
        warehouseTable.setItems(warehouseList);
    }

    private void searchWarehouses(String text) {
        if (text == null || text.isEmpty()) {
            warehouseTable.setItems(warehouseList);
            return;
        }
        String lowerText = text.toLowerCase();
        List<Warehouse> filtered = warehouseList.stream()
                .filter(w -> w.name().toLowerCase().contains(lowerText) || w.address().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
        warehouseTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void addWarehouse() {
        try {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            double capacity = Double.parseDouble(capacityField.getText().trim());

            if (name.isEmpty() || address.isEmpty()) {
                AlertController.showAlert("Всі поля повинні бути заповнені");
                return;
            }

            Warehouse warehouse = new Warehouse(UUID.randomUUID(), name, address, capacity, LocalDateTime.now());
            warehouseRepository.create(warehouse);
            loadWarehouses();
            clearFields();
            AlertController.showInfo("Склад успішно додано");
        } catch (NumberFormatException e) {
            AlertController.showAlert("Місткість повинна бути числом");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при додаванні: " + e.getMessage());
        }
    }

    private void editWarehouse() {
        if (selectedWarehouse == null) {
            AlertController.showAlert("Виберіть склад для редагування");
            return;
        }
        try {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            double capacity = Double.parseDouble(capacityField.getText().trim());

            Warehouse updated = new Warehouse(selectedWarehouse.warehouseId(), name, address, capacity, selectedWarehouse.createdAt());
            warehouseRepository.update(updated);
            loadWarehouses();
            clearFields();
            AlertController.showInfo("Дані оновлено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при редагуванні: " + e.getMessage());
        }
    }

    private void deleteWarehouse() {
        if (selectedWarehouse == null) {
            AlertController.showAlert("Виберіть склад для видалення");
            return;
        }
        try {
            warehouseRepository.deleteById(selectedWarehouse.warehouseId());
            loadWarehouses();
            clearFields();
            AlertController.showInfo("Склад видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        addressField.clear();
        capacityField.clear();
        selectedWarehouse = null;
        warehouseTable.getSelectionModel().clearSelection();
    }
}
