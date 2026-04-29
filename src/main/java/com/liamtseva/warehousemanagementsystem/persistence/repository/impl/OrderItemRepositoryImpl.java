package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.OrderItem;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.OrderItemRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

public class OrderItemRepositoryImpl implements OrderItemRepository {
    private final DataSource dataSource;

    public OrderItemRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public OrderItem findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM OrderItems WHERE order_item_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToOrderItem(resultSet);
                } else {
                    throw new EntityNotFoundException("Елемент замовлення з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку елемента замовлення", e);
        }
    }

    @Override
    public List<OrderItem> findByOrderId(UUID orderId) {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT * FROM OrderItems WHERE order_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, orderId.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapToOrderItem(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні елементів замовлення", e);
        }
        return items;
    }

    @Override
    public List<OrderItem> findAll() {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT * FROM OrderItems";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                items.add(mapToOrderItem(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всіх елементів замовлень", e);
        }
        return items;
    }

    @Override
    public OrderItem create(OrderItem orderItem) {
        String query = "INSERT INTO OrderItems (order_item_id, order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        UUID id = orderItem.orderItemId() != null ? orderItem.orderItemId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, orderItem.orderId().toString());
            preparedStatement.setString(3, orderItem.productId().toString());
            preparedStatement.setInt(4, orderItem.quantity());
            preparedStatement.setDouble(5, orderItem.unitPrice());
            preparedStatement.executeUpdate();
            return new OrderItem(id, orderItem.orderId(), orderItem.productId(), orderItem.quantity(), orderItem.unitPrice());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні елемента замовлення", e);
        }
    }

    @Override
    public void update(OrderItem orderItem) throws EntityNotFoundException {
        String query = "UPDATE OrderItems SET order_id = ?, product_id = ?, quantity = ?, unit_price = ? WHERE order_item_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, orderItem.orderId().toString());
            preparedStatement.setString(2, orderItem.productId().toString());
            preparedStatement.setInt(3, orderItem.quantity());
            preparedStatement.setDouble(4, orderItem.unitPrice());
            preparedStatement.setString(5, orderItem.orderItemId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Елемент замовлення з ID " + orderItem.orderItemId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні елемента замовлення", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM OrderItems WHERE order_item_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Елемент замовлення з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні елемента замовлення", e);
        }
    }

    private OrderItem mapToOrderItem(ResultSet resultSet) throws SQLException {
        return new OrderItem(
            UUID.fromString(resultSet.getString("order_item_id")),
            UUID.fromString(resultSet.getString("order_id")),
            UUID.fromString(resultSet.getString("product_id")),
            resultSet.getInt("quantity"),
            resultSet.getDouble("unit_price")
        );
    }
}
