package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
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

public class SupplierController {

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, String> nameColumn;
    @FXML private TableColumn<Supplier, String> contactColumn;
    @FXML private TableColumn<Supplier, String> emailColumn;
    @FXML private TableColumn<Supplier, String> phoneColumn;
    @FXML private TableColumn<Supplier, String> addressColumn;

    private final SupplierRepository repository;
    private final ObservableList<Supplier> supplierData = FXCollections.observableArrayList();
    private FilteredList<Supplier> filteredData;
    private Supplier selectedSupplier;

    public SupplierController() {
        this.repository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        filteredData = new FilteredList<>(supplierData, p -> true);
        setupTable();
        loadSuppliers();
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
        contactColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().contactPerson()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().email()));
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().phone()));
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().address()));
        
        SortedList<Supplier> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(supplierTable.comparatorProperty());
        supplierTable.setItems(sortedData);
        
        Label placeholder = new Label("Нічого не знайдено за вашим запитом");
        placeholder.getStyleClass().add("table-placeholder");
        supplierTable.setPlaceholder(placeholder);
    }

    private void loadSuppliers() {
        List<Supplier> suppliers = repository.findAll();
        supplierData.setAll(suppliers);
    }

    private void setupListeners() {
        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedSupplier = newSelection;
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filteredData.setPredicate(supplier -> {
                if (newText == null || newText.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newText.toLowerCase();
                return supplier.name().toLowerCase().contains(lowerCaseFilter) ||
                       supplier.contactPerson().toLowerCase().contains(lowerCaseFilter) ||
                       supplier.email().toLowerCase().contains(lowerCaseFilter) ||
                       supplier.phone().toLowerCase().contains(lowerCaseFilter) ||
                       supplier.address().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }


    @FXML
    private void openAddDialog() {
        showFormDialog(null);
    }

    @FXML
    private void openEditDialog() {
        if (selectedSupplier == null) {
            AlertController.showAlert("Будь ласка, виберіть постачальника для редагування");
            return;
        }
        showFormDialog(selectedSupplier);
    }

    private void showFormDialog(Supplier supplier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/supplier_form.fxml"));
            VBox root = loader.load();
            SupplierFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);

            controller.setStage(stage);
            controller.setSupplierToEdit(supplier);
            controller.setOnSaveCallback(this::loadSuppliers);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження форми: " + e.toString());
        }
    }

    @FXML
    private void deleteSupplier() {
        if (selectedSupplier == null) {
            AlertController.showAlert("Будь ласка, виберіть постачальника для видалення");
            return;
        }

        try {
            repository.deleteById(selectedSupplier.supplierId());
            loadSuppliers();
            AlertController.showInfo("Постачальника видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }
}
