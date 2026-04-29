package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Warehouse;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.ZoneType;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.WarehouseRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.WarehouseRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ZoneRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ZoneValidator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ZoneController {

    @FXML
    private ComboBox<Warehouse> warehouseComboBox;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<ZoneType> zoneTypeComboBox;
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
    private TableView<Zone> zoneTable;
    @FXML
    private TableColumn<Zone, String> warehouseColumn;
    @FXML
    private TableColumn<Zone, String> nameColumn;
    @FXML
    private TableColumn<Zone, String> typeColumn;

    private final ZoneRepository repository;
    private final WarehouseRepository warehouseRepository;
    private final ObservableList<Zone> zoneData = FXCollections.observableArrayList();
    private Map<UUID, Warehouse> warehouses;
    private Zone selectedZone;

    public ZoneController() {
        this.repository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.warehouseRepository = new WarehouseRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        setupTable();
        setupComboBoxes();
        loadZones();
        setupListeners();
    }

    private void setupTable() {
        warehouseColumn.setCellValueFactory(data -> {
            Warehouse w = warehouses != null ? warehouses.get(data.getValue().warehouseId()) : null;
            return new SimpleStringProperty(w != null ? w.name() : "Невідомо");
        });
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().zoneType().toString()));
        zoneTable.setItems(zoneData);
    }

    private void setupComboBoxes() {
        refreshWarehouses();
        warehouseComboBox.setConverter(new StringConverter<Warehouse>() {
            @Override
            public String toString(Warehouse warehouse) {
                return warehouse == null ? "" : warehouse.name();
            }
            @Override
            public Warehouse fromString(String string) { return null; }
        });

        zoneTypeComboBox.setItems(FXCollections.observableArrayList(ZoneType.values()));
        zoneTypeComboBox.setConverter(new StringConverter<ZoneType>() {
            @Override
            public String toString(ZoneType type) {
                return type == null ? "" : type.getUkrainianName();
            }
            @Override
            public ZoneType fromString(String string) { return null; }
        });
    }

    private void refreshWarehouses() {
        List<Warehouse> allWarehouses = warehouseRepository.findAll();
        warehouses = allWarehouses.stream().collect(Collectors.toMap(Warehouse::warehouseId, w -> w));
        warehouseComboBox.setItems(FXCollections.observableArrayList(allWarehouses));
    }

    private void loadZones() {
        List<Zone> zones = repository.findAll();
        zoneData.setAll(zones);
    }

    private void setupListeners() {
        zoneTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedZone = newSelection;
                nameField.setText(newSelection.name());
                zoneTypeComboBox.setValue(newSelection.zoneType());
                warehouseComboBox.setValue(warehouses.get(newSelection.warehouseId()));
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterZones(newText);
        });

        addButton.setOnAction(e -> handleAdd());
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> clearFields());
    }

    private void filterZones(String query) {
        if (query == null || query.isEmpty()) {
            loadZones();
        } else {
            String lowerCaseQuery = query.toLowerCase();
            List<Zone> filtered = repository.findAll().stream()
                .filter(z -> z.name().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
            zoneData.setAll(filtered);
        }
    }

    private void handleAdd() {
        try {
            Warehouse warehouse = warehouseComboBox.getValue();
            ZoneType type = zoneTypeComboBox.getValue();
            String name = nameField.getText().trim();

            if (warehouse == null || type == null || name.isEmpty()) {
                AlertController.showAlert("Всі поля повинні бути заповнені");
                return;
            }

            Zone zone = new Zone(UUID.randomUUID(), warehouse.warehouseId(), name, type);
            ValidationResult validation = ZoneValidator.isZoneValid(zone);
            if (!validation.isValid()) {
                AlertController.showAlert(validation.getErrors().get(0));
                return;
            }

            repository.create(zone);
            loadZones();
            clearFields();
            AlertController.showInfo("Зону успішно додано");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при додаванні: " + e.getMessage());
        }
    }

    private void handleEdit() {
        if (selectedZone == null) {
            AlertController.showAlert("Будь ласка, виберіть зону для редагування");
            return;
        }

        try {
            Warehouse warehouse = warehouseComboBox.getValue();
            ZoneType type = zoneTypeComboBox.getValue();
            String name = nameField.getText().trim();

            if (warehouse == null || type == null || name.isEmpty()) {
                AlertController.showAlert("Всі поля повинні бути заповнені");
                return;
            }

            Zone updated = new Zone(selectedZone.zoneId(), warehouse.warehouseId(), name, type);
            ValidationResult validation = ZoneValidator.isZoneValid(updated);
            if (!validation.isValid()) {
                AlertController.showAlert(validation.getErrors().get(0));
                return;
            }

            repository.update(updated);
            loadZones();
            clearFields();
            AlertController.showInfo("Зону оновлено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при редагуванні: " + e.getMessage());
        }
    }

    private void handleDelete() {
        if (selectedZone == null) {
            AlertController.showAlert("Будь ласка, виберіть зону для видалення");
            return;
        }

        try {
            repository.deleteById(selectedZone.zoneId());
            loadZones();
            clearFields();
            AlertController.showInfo("Зону видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        warehouseComboBox.setValue(null);
        zoneTypeComboBox.setValue(null);
        selectedZone = null;
        zoneTable.getSelectionModel().clearSelection();
    }
}
