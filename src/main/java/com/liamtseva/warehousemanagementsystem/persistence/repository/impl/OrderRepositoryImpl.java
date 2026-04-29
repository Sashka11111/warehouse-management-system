package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Order;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.OrderStatus;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.OrderType;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.OrderRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

public class OrderRepositoryImpl implements OrderRepository {
    private final DataSource dataSource;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Order findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM Orders WHERE order_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToOrder(resultSet);
                } else {
                    throw new EntityNotFoundException("Замовлення з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку замовлення", e);
        }
    }

    @Override
    public Order findByOrderNumber(String orderNumber) throws EntityNotFoundException {
        String query = "SELECT * FROM Orders WHERE order_number = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, orderNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToOrder(resultSet);
                } else {
                    throw new EntityNotFoundException("Замовлення з номером " + orderNumber + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку замовлення за номером", e);
        }
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(mapToOrder(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всіх замовлень", e);
        }
        return orders;
    }

    @Override
    public Order create(Order order) {
        String query = "INSERT INTO Orders (order_id, user_id, order_number, type, status, total_amount, created_at, required_date, completed_at, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        UUID id = order.orderId() != null ? order.orderId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, order.userId() != null ? order.userId().toString() : null);
            preparedStatement.setString(3, order.orderNumber());
            preparedStatement.setString(4, order.type().name());
            preparedStatement.setString(5, order.status().name());
            preparedStatement.setDouble(6, order.totalAmount() != null ? order.totalAmount() : 0.0);
            preparedStatement.setString(7, order.createdAt().format(DATE_TIME_FORMATTER));
            preparedStatement.setString(8, order.requiredDate() != null ? order.requiredDate().format(DATE_TIME_FORMATTER) : null);
            preparedStatement.setString(9, order.completedAt() != null ? order.completedAt().format(DATE_TIME_FORMATTER) : null);
            preparedStatement.setString(10, order.notes());
            preparedStatement.executeUpdate();
            return new Order(id, order.userId(), order.orderNumber(), order.type(), order.status(), order.totalAmount(), order.createdAt(), order.requiredDate(), order.completedAt(), order.notes());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні замовлення", e);
        }
    }

    @Override
    public void update(Order order) throws EntityNotFoundException {
        String query = "UPDATE Orders SET user_id = ?, order_number = ?, type = ?, status = ?, total_amount = ?, required_date = ?, completed_at = ?, notes = ? WHERE order_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, order.userId() != null ? order.userId().toString() : null);
            preparedStatement.setString(2, order.orderNumber());
            preparedStatement.setString(3, order.type().name());
            preparedStatement.setString(4, order.status().name());
            preparedStatement.setDouble(5, order.totalAmount() != null ? order.totalAmount() : 0.0);
            preparedStatement.setString(6, order.requiredDate() != null ? order.requiredDate().format(DATE_TIME_FORMATTER) : null);
            preparedStatement.setString(7, order.completedAt() != null ? order.completedAt().format(DATE_TIME_FORMATTER) : null);
            preparedStatement.setString(8, order.notes());
            preparedStatement.setString(9, order.orderId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Замовлення з ID " + order.orderId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні замовлення", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM Orders WHERE order_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Замовлення з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні замовлення", e);
        }
    }

    private Order mapToOrder(ResultSet resultSet) throws SQLException {
        return new Order(
            UUID.fromString(resultSet.getString("order_id")),
            resultSet.getString("user_id") != null ? UUID.fromString(resultSet.getString("user_id")) : null,
            resultSet.getString("order_number"),
            OrderType.valueOf(resultSet.getString("type")),
            OrderStatus.valueOf(resultSet.getString("status")),
            resultSet.getDouble("total_amount"),
            parseDateTime(resultSet.getString("created_at")),
            parseDateTime(resultSet.getString("required_date")),
            parseDateTime(resultSet.getString("completed_at")),
            resultSet.getString("notes")
        );
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateTimeStr);
            } catch (DateTimeParseException e2) {
                return LocalDateTime.now();
            }
        }
    }
}
