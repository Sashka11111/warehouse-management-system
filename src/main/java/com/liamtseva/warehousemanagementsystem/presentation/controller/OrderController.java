package com.liamtseva.warehousemanagementsystem.presentation.controller;

import com.liamtseva.warehousemanagementsystem.persistence.connection.DatabaseConnection;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Order;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.OrderStatus;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.OrderRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.UserRepository;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.OrderRepositoryImpl;
import com.liamtseva.warehousemanagementsystem.persistence.repository.impl.UserRepositoryImpl;
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

public class OrderController {

    @FXML
    private ComboBox<OrderStatus> statusComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, String> idColumn;
    @FXML
    private TableColumn<Order, String> userColumn;
    @FXML
    private TableColumn<Order, String> dateColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, String> amountColumn;
    @FXML
    private Button updateStatusButton;
    @FXML
    private Button deleteButton;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    private ObservableList<Order> orderList;
    private Map<UUID, User> users;
    private Order selectedOrder;

    public OrderController() {
        this.orderRepository = new OrderRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
        this.userRepository = new UserRepositoryImpl(DatabaseConnection.getInstance().getDataSource());
    }

    @FXML
    public void initialize() {
        loadData();

        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().orderId().toString()));
        userColumn.setCellValueFactory(data -> {
            User u = users.get(data.getValue().userId());
            return new SimpleStringProperty(u != null ? u.username() : "Unknown");
        });
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status().toString()));
        amountColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f", data.getValue().totalAmount())));

        statusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));

        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchOrders(newVal));

        updateStatusButton.setOnAction(event -> updateOrderStatus());
        deleteButton.setOnAction(event -> deleteOrder());

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedOrder = newVal;
            if (newVal != null) {
                statusComboBox.setValue(newVal.status());
            }
        });
    }

    private void loadData() {
        orderList = FXCollections.observableArrayList(orderRepository.findAll());
        orderTable.setItems(orderList);

        users = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::userId, u -> u));
    }

    private void searchOrders(String text) {
        if (text == null || text.isEmpty()) {
            orderTable.setItems(orderList);
            return;
        }
        String lowerText = text.toLowerCase();
        List<Order> filtered = orderList.stream()
                .filter(o -> o.orderId().toString().toLowerCase().contains(lowerText) || 
                            (users.get(o.userId()) != null && users.get(o.userId()).username().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
        orderTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void updateOrderStatus() {
        if (selectedOrder == null || statusComboBox.getValue() == null) {
            AlertController.showAlert("Виберіть замовлення та статус");
            return;
        }
        try {
            Order updated = new Order(selectedOrder.orderId(), selectedOrder.userId(), selectedOrder.orderNumber(), selectedOrder.type(), statusComboBox.getValue(), selectedOrder.totalAmount(), selectedOrder.createdAt(), selectedOrder.requiredDate(), selectedOrder.completedAt(), selectedOrder.notes());
            orderRepository.update(updated);
            loadData();
            AlertController.showInfo("Статус замовлення оновлено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка: " + e.getMessage());
        }
    }

    private void deleteOrder() {
        if (selectedOrder == null) {
            AlertController.showAlert("Виберіть замовлення для видалення");
            return;
        }
        try {
            orderRepository.deleteById(selectedOrder.orderId());
            loadData();
            AlertController.showInfo("Замовлення видалено");
        } catch (Exception e) {
            AlertController.showAlert("Помилка при видаленні: " + e.getMessage());
        }
    }
}
