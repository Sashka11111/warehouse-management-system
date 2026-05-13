package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.domain.security.AuthenticatedUser;
import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.InventoryItem;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.InventoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.InventoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductCategoryRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ZoneRepositoryImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;

public class DashboardController {

    @FXML private Label totalProductsLabel;
    @FXML private Label totalCategoriesLabel;
    @FXML private Label totalSuppliersLabel;
    @FXML private Label totalValueLabel;
    @FXML private Label dateLabel;

    @FXML private TableView<LowStockItem> lowStockTable;
    @FXML private TableColumn<LowStockItem, String> productNameColumn;
    @FXML private TableColumn<LowStockItem, String> skuColumn;
    @FXML private TableColumn<LowStockItem, Integer> currentQtyColumn;
    @FXML private TableColumn<LowStockItem, Integer> minQtyColumn;
    @FXML private TableColumn<LowStockItem, String> statusColumn;

    @FXML private VBox mainContainer;
    @FXML private GridPane statsGrid;

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ZoneRepository zoneRepository;

    public DashboardController() {
        this.productRepository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.inventoryRepository = new InventoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.supplierRepository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.categoryRepository = new ProductCategoryRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.zoneRepository = new ZoneRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        setupTable();
        loadStatistics();
        setupResponsiveLayout();
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd MMMM, yyyy",
                new Locale("uk", "UA")
        );

        dateLabel.setText(currentDate.format(formatter));
    }

    private void setupResponsiveLayout() {
        Platform.runLater(() -> {
            if (mainContainer.getScene() != null) {
                mainContainer.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
                    updateLayout(newVal.doubleValue());
                });
                updateLayout(mainContainer.getScene().getWidth());
            }
        });
    }

    private void updateLayout(double width) {
        if (width < 900) {
            statsGrid.getColumnConstraints().forEach(c -> c.setPercentWidth(50));
        } else {
            statsGrid.getColumnConstraints().forEach(c -> c.setPercentWidth(25));
        }
    }


    private void setupTable() {
        productNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        skuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sku()));
        currentQtyColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().currentQty()).asObject());
        minQtyColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().minQty()).asObject());
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                List<Product> products = productRepository.findAll();
                List<InventoryItem> inventory = inventoryRepository.findAll();
                int categoriesCount = categoryRepository.findAll().size();
                int suppliersCount = supplierRepository.findAll().size();
                List<Zone> zones = zoneRepository.findAll();

                Map<java.util.UUID, Integer> productQuantities = inventory.stream()
                        .collect(Collectors.groupingBy(InventoryItem::productId,
                                Collectors.summingInt(InventoryItem::quantity)));

                double totalValue = products.stream()
                        .mapToDouble(p -> p.price() * productQuantities.getOrDefault(p.productId(), 0))
                        .sum();

                List<LowStockItem> lowStockItems = new ArrayList<>();
                for (Product p : products) {
                    int currentQty = productQuantities.getOrDefault(p.productId(), 0);
                    if (currentQty <= p.minStockLevel()) {
                        String status = currentQty == 0 ? "Відсутній" : "Закінчується";
                        lowStockItems.add(new LowStockItem(p.name(), p.sku(), currentQty, p.minStockLevel(), status));
                    }
                }

                Platform.runLater(() -> {
                    totalProductsLabel.setText(String.valueOf(products.size()));
                    totalCategoriesLabel.setText(String.valueOf(categoriesCount));
                    totalSuppliersLabel.setText(String.valueOf(suppliersCount));
                    totalValueLabel.setText(String.format("%.2f ₴", totalValue));
                    lowStockTable.setItems(FXCollections.observableArrayList(lowStockItems));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static record LowStockItem(String name, String sku, int currentQty, int minQty, String status) {}
}
