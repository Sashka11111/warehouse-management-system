package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.ZoneType;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ZoneRepositoryImpl;
import java.util.UUID;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ZoneValidator;
import com.liamtseva.warehousemanagementsystem.presentation.validation.ValidationResult;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ZoneFormController {

    @FXML private TextField nameField;
    @FXML private ComboBox<ZoneType> zoneTypeComboBox;
    @FXML private Label formTitleLabel;
    @FXML private Label formSubtitleLabel;
    @FXML private Button saveButton;

    private Stage stage;
    private final ZoneRepository repository;
    private Zone zoneToEdit;
    private Runnable onSaveCallback;

    public ZoneFormController() {
        this.repository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        zoneTypeComboBox.setItems(FXCollections.observableArrayList(ZoneType.values()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setZoneToEdit(Zone zone) {
        this.zoneToEdit = zone;
        if (zone != null) {
            formTitleLabel.setText("Редагувати зону");
            formSubtitleLabel.setText("Оновіть назву або тип зони");
            nameField.setText(zone.name());
            zoneTypeComboBox.setValue(zone.zoneType());
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
            ZoneType type = zoneTypeComboBox.getValue();

            Zone zoneData = new Zone(
                zoneToEdit == null ? null : zoneToEdit.zoneId(),
                name, type
            );

            ValidationResult result = ZoneValidator.isZoneValid(zoneData, repository);
            if (!result.isValid()) {
                AlertController.showAlert(String.join("\n", result.getErrors()));
                return;
            }

            if (zoneToEdit == null) {
                Zone newZone = new Zone(UUID.randomUUID(), name, type);
                repository.create(newZone);
                AlertController.showInfo("Зону додано");
            } else {
                Zone updated = new Zone(zoneToEdit.zoneId(), name, type);
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
