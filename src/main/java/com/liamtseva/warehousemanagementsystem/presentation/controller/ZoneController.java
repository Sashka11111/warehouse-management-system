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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ZoneController {

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
    private TableColumn<Zone, String> nameColumn;
    @FXML
    private TableColumn<Zone, String> typeColumn;

    private final ZoneRepository repository;
    private final ObservableList<Zone> zoneData = FXCollections.observableArrayList();
    private Zone selectedZone;

    public ZoneController() {
        this.repository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        setupTable();
        setupComboBoxes();
        loadZones();
        setupListeners();
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().zoneType().toString()));
        zoneTable.setItems(zoneData);
    }

    private void setupComboBoxes() {

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
            String name = nameField.getText();
            ZoneType type = zoneTypeComboBox.getValue();
            Zone zone = new Zone(UUID.randomUUID(), name, type);
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
            String name = nameField.getText();
            ZoneType type = zoneTypeComboBox.getValue();
            Zone updated = new Zone(selectedZone.zoneId(), name, type);
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
        zoneTypeComboBox.setValue(null);
        selectedZone = null;
        zoneTable.getSelectionModel().clearSelection();
    }
}
