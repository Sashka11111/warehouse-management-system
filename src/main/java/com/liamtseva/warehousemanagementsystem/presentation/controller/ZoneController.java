package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ZoneRepositoryImpl;
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

public class ZoneController {

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TableView<Zone> zoneTable;
    @FXML private TableColumn<Zone, String> nameColumn;
    @FXML private TableColumn<Zone, String> typeColumn;

    private final ZoneRepository repository;
    private final ObservableList<Zone> zoneData = FXCollections.observableArrayList();
    private FilteredList<Zone> filteredData;
    private Zone selectedZone;

    public ZoneController() {
        this.repository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        filteredData = new FilteredList<>(zoneData, p -> true);
        setupTable();
        loadZones();
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
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().zoneType().toString()));
        
        SortedList<Zone> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(zoneTable.comparatorProperty());
        zoneTable.setItems(sortedData);
        
        Label placeholder = new Label("Нічого не знайдено за вашим запитом");
        placeholder.getStyleClass().add("table-placeholder");
        zoneTable.setPlaceholder(placeholder);
    }

    private void loadZones() {
        List<Zone> zones = repository.findAll();
        zoneData.setAll(zones);
    }

    private void setupListeners() {
        zoneTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedZone = newSelection;
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filteredData.setPredicate(zone -> {
                if (newText == null || newText.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newText.toLowerCase();
                return zone.name().toLowerCase().contains(lowerCaseFilter) ||
                       zone.zoneType().toString().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }


    @FXML
    private void openAddDialog() {
        showFormDialog(null);
    }

    @FXML
    private void openEditDialog() {
        if (selectedZone == null) {
            AlertController.showAlert("Будь ласка, виберіть зону для редагування");
            return;
        }
        showFormDialog(selectedZone);
    }

    private void showFormDialog(Zone zone) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/zone_form.fxml"));
            VBox root = loader.load();
            ZoneFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);

            controller.setStage(stage);
            controller.setZoneToEdit(zone);
            controller.setOnSaveCallback(this::loadZones);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження форми: " + e.toString());
        }
    }

    @FXML
    private void deleteZone() {
        if (selectedZone == null) {
            AlertController.showAlert("Будь ласка, виберіть зону для видалення");
            return;
        }

        try {
            repository.deleteById(selectedZone.zoneId());
            loadZones();
            AlertController.showInfo("Зону видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }
}
