package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Order;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.OrderRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.WarehouseRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.OrderRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.ProductRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.SupplierRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.WarehouseRepositoryImpl;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController {

    @FXML
    private Label totalWarehousesLabel;
    @FXML
    private Label totalProductsLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label totalSuppliersLabel;

    @FXML
    private TableView<Order> recentOrdersTable;
    @FXML
    private TableColumn<Order, String> orderNumColumn;
    @FXML
    private TableColumn<Order, String> orderDateColumn;
    @FXML
    private TableColumn<Order, String> orderTypeColumn;
    @FXML
    private TableColumn<Order, String> orderStatusColumn;
    @FXML
    private TableColumn<Order, String> orderAmountColumn;

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SupplierRepository supplierRepository;

    public DashboardController() {
        this.warehouseRepository = new WarehouseRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.productRepository = new ProductRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.orderRepository = new OrderRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.supplierRepository = new SupplierRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        setupTable();
        loadStatistics();
    }

    private void setupTable() {
        orderNumColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().orderNumber()));
        orderDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));
        orderTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().type().toString()));
        orderStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status().toString()));
        orderAmountColumn.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("%.2f", data.getValue().totalAmount())
        ));
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                List<Order> allOrders = orderRepository.findAll();
                int warehousesCount = warehouseRepository.findAll().size();
                int productsCount = productRepository.findAll().size();
                int suppliersCount = supplierRepository.findAll().size();

                // Отримуємо 10 останніх замовлень
                List<Order> recentOrders = allOrders.stream()
                        .sorted(Comparator.comparing(Order::createdAt).reversed())
                        .limit(10)
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    totalWarehousesLabel.setText(String.valueOf(warehousesCount));
                    totalProductsLabel.setText(String.valueOf(productsCount));
                    totalOrdersLabel.setText(String.valueOf(allOrders.size()));
                    totalSuppliersLabel.setText(String.valueOf(suppliersCount));
                    recentOrdersTable.setItems(FXCollections.observableArrayList(recentOrders));
                });
            } catch (Exception e) {
                // Якщо колонка total_amount ще не додана, тут може виникнути помилка
                // У такому разі просто виведемо її в консоль
                e.printStackTrace();
            }
        }).start();
    }
}
