package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.InventoryItem;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.InventoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.InventoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ZoneRepositoryImpl;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

public class InventoryController {

    @FXML
    private ComboBox<Product> productComboBox;
    @FXML
    private ComboBox<Zone> zoneComboBox;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<InventoryItem> inventoryTable;
    @FXML
    private TableColumn<InventoryItem, String> productColumn;
    @FXML
    private TableColumn<InventoryItem, String> zoneColumn;
    @FXML
    private TableColumn<InventoryItem, String> quantityColumn;
    @FXML
    private TableColumn<InventoryItem, String> dateColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;

    private ObservableList<InventoryItem> inventoryList;
    private Map<UUID, Product> products;
    private Map<UUID, Zone> zones;
    private InventoryItem selectedItem;

    public InventoryController() {
        this.inventoryRepository = new InventoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.productRepository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.zoneRepository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        loadData();

        productColumn.setCellValueFactory(data -> {
            Product p = products.get(data.getValue().productId());
            return new SimpleStringProperty(p != null ? p.name() : "Unknown");
        });
        zoneColumn.setCellValueFactory(data -> {
            Zone z = zones.get(data.getValue().zoneId());
            return new SimpleStringProperty(z != null ? z.name() : "Unknown");
        });
        quantityColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().quantity())));
        dateColumn.setCellValueFactory(data -> {
            if (data.getValue().lastUpdated() != null) {
                return new SimpleStringProperty(data.getValue().lastUpdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            return new SimpleStringProperty("");
        });

        setupComboBoxes();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchInventory(newVal));

        addButton.setOnAction(event -> addOrUpdateInventory());
        deleteButton.setOnAction(event -> deleteInventory());
        clearButton.setOnAction(event -> clearFields());

        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedItem = newVal;
            if (newVal != null) {
                productComboBox.setValue(products.get(newVal.productId()));
                zoneComboBox.setValue(zones.get(newVal.zoneId()));
                quantityField.setText(String.valueOf(newVal.quantity()));
            }
        });
    }

    private void loadData() {
        inventoryList = FXCollections.observableArrayList(inventoryRepository.findAll());
        inventoryTable.setItems(inventoryList);

        products = productRepository.findAll().stream()
                .collect(Collectors.toMap(Product::productId, p -> p));
        zones = zoneRepository.findAll().stream()
                .collect(Collectors.toMap(Zone::zoneId, z -> z));
    }

    private void setupComboBoxes() {
        productComboBox.setItems(FXCollections.observableArrayList(products.values()));
        productComboBox.setConverter(new StringConverter<Product>() {
            @Override public String toString(Product object) { return object == null ? "" : object.name(); }
            @Override public Product fromString(String string) { return null; }
        });

        zoneComboBox.setItems(FXCollections.observableArrayList(zones.values()));
        zoneComboBox.setConverter(new StringConverter<Zone>() {
            @Override public String toString(Zone object) { return object == null ? "" : object.name(); }
            @Override public Zone fromString(String string) { return null; }
        });
    }

    private void searchInventory(String text) {
        if (text == null || text.isEmpty()) {
            inventoryTable.setItems(inventoryList);
            return;
        }
        String lowerText = text.toLowerCase();
        List<InventoryItem> filtered = inventoryList.stream()
                .filter(item -> {
                    Product p = products.get(item.productId());
                    return p != null && p.name().toLowerCase().contains(lowerText);
                })
                .collect(Collectors.toList());
        inventoryTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void addOrUpdateInventory() {
        try {
            Product p = productComboBox.getValue();
            Zone z = zoneComboBox.getValue();
            int qty = Integer.parseInt(quantityField.getText().trim());

            if (p == null || z == null) {
                AlertController.showAlert("Виберіть товар та зону");
                return;
            }

            if (selectedItem != null && selectedItem.productId().equals(p.productId()) && selectedItem.zoneId().equals(z.zoneId())) {
                // Оновлення
                InventoryItem updated = new InventoryItem(selectedItem.inventoryId(), p.productId(), z.zoneId(), qty, LocalDateTime.now());
                inventoryRepository.update(updated);
                AlertController.showInfo("Інвентар оновлено");
            } else {
                // Новий запис
                InventoryItem newItem = new InventoryItem(UUID.randomUUID(), p.productId(), z.zoneId(), qty, LocalDateTime.now());
                inventoryRepository.create(newItem);
                AlertController.showInfo("Товар додано до інвентарю");
            }
            loadData();
            clearFields();
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    private void deleteInventory() {
        if (selectedItem == null) {
            AlertController.showAlert("Виберіть запис для видалення");
            return;
        }
        try {
            inventoryRepository.deleteById(selectedItem.inventoryId());
            loadData();
            clearFields();
            AlertController.showInfo("Запис видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }

    private void clearFields() {
        productComboBox.setValue(null);
        zoneComboBox.setValue(null);
        quantityField.clear();
        selectedItem = null;
        inventoryTable.getSelectionModel().clearSelection();
    }
}
