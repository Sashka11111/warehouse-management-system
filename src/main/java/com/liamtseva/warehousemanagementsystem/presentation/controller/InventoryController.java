package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import com.liamtseva.warehousemanagementsystem.persistence.entity.InventoryItem;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.InventoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.InventoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ZoneRepositoryImpl;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InventoryController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> zoneFilter;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, String> productColumn;
    @FXML private TableColumn<InventoryItem, String> zoneColumn;
    @FXML private TableColumn<InventoryItem, Integer> quantityColumn;
    @FXML private TableColumn<InventoryItem, LocalDateTime> lastUpdatedColumn;

    private final InventoryRepository repository;
    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;
    
    private final ObservableList<InventoryItem> inventoryData = FXCollections.observableArrayList();
    private FilteredList<InventoryItem> filteredData;
    private Map<UUID, Product> products;
    private Map<UUID, Zone> zones;
    private InventoryItem selectedInventory;

    public InventoryController() {
        this.repository = new InventoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.productRepository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.zoneRepository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        filteredData = new FilteredList<>(inventoryData, p -> true);
        setupTable();
        loadInventory();
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
        productColumn.setCellValueFactory(data -> {
            Product p = products.get(data.getValue().productId());
            return new SimpleStringProperty(p != null ? p.name() : "Невідомо");
        });
        zoneColumn.setCellValueFactory(data -> {
            Zone z = zones.get(data.getValue().zoneId());
            return new SimpleStringProperty(z != null ? z.name() : "Невідомо");
        });
        quantityColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().quantity()));
        lastUpdatedColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().lastUpdated()));
        
        SortedList<InventoryItem> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(inventoryTable.comparatorProperty());
        inventoryTable.setItems(sortedData);
        
        Label placeholder = new Label("Нічого не знайдено за вашим запитом");
        placeholder.getStyleClass().add("table-placeholder");
        inventoryTable.setPlaceholder(placeholder);
    }

    private void loadInventory() {
        products = productRepository.findAll().stream()
            .collect(Collectors.toMap(Product::productId, p -> p));
        zones = zoneRepository.findAll().stream()
            .collect(Collectors.toMap(Zone::zoneId, z -> z));
        
        List<InventoryItem> inventoryList = repository.findAll();
        inventoryData.setAll(inventoryList);
        
        ObservableList<String> zoneNames = FXCollections.observableArrayList("Всі зони");
        zoneNames.addAll(zones.values().stream()
            .map(Zone::name)
            .sorted()
            .collect(Collectors.toList()));
        zoneFilter.setItems(zoneNames);
        zoneFilter.getSelectionModel().selectFirst();
    }

    private void setupListeners() {
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedInventory = newSelection;
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        zoneFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String selectedZone = zoneFilter.getValue();
        
        filteredData.setPredicate(item -> {
            Product p = products.get(item.productId());
            String productName = p != null ? p.name().toLowerCase() : "";
            
            boolean matchesText = searchText.isEmpty() || productName.contains(searchText);

            Zone z = zones.get(item.zoneId());
            String zoneName = z != null ? z.name() : "";
            
            boolean matchesZone = selectedZone == null || 
                                 selectedZone.equals("Всі зони") ||
                                 zoneName.equals(selectedZone);

            return matchesText && matchesZone;
        });
    }


    @FXML
    private void openAddDialog() {
        showFormDialog(null);
    }

    @FXML
    private void openEditDialog() {
        if (selectedInventory == null) {
            AlertController.showAlert("Будь ласка, виберіть позицію для редагування");
            return;
        }
        showFormDialog(selectedInventory);
    }

    private void showFormDialog(InventoryItem inventory) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inventory_form.fxml"));
            VBox root = loader.load();
            InventoryFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);

            controller.setStage(stage);
            controller.setInventoryToEdit(inventory);
            controller.setOnSaveCallback(this::loadInventory);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            AlertController.showAlert("Помилка завантаження форми: " + e.toString());
        }
    }

    @FXML
    private void deleteInventory() {
        if (selectedInventory == null) {
            AlertController.showAlert("Будь ласка, виберіть позицію для видалення");
            return;
        }

        try {
            repository.deleteById(selectedInventory.inventoryId());
            loadInventory();
            AlertController.showInfo("Позицію видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }
}
