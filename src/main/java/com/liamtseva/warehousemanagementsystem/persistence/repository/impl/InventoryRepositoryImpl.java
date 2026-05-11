package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.InventoryItem;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.InventoryRepository;
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

public class InventoryRepositoryImpl implements InventoryRepository {
    private final DataSource dataSource;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public InventoryRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public InventoryItem findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM Inventory WHERE inventory_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToInventory(resultSet);
                } else {
                    throw new EntityNotFoundException("Запис інвентарю з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку запису інвентарю", e);
        }
    }

    @Override
    public List<InventoryItem> findByProductId(UUID productId) {
        List<InventoryItem> items = new ArrayList<>();
        String query = "SELECT * FROM Inventory WHERE product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productId.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapToInventory(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні інвентарю за товаром", e);
        }
        return items;
    }

    @Override
    public List<InventoryItem> findByZoneId(UUID zoneId) {
        List<InventoryItem> items = new ArrayList<>();
        String query = "SELECT * FROM Inventory WHERE zone_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, zoneId.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapToInventory(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні інвентарю за зоною", e);
        }
        return items;
    }

    @Override
    public List<InventoryItem> findAll() {
        List<InventoryItem> items = new ArrayList<>();
        String query = "SELECT * FROM Inventory";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                items.add(mapToInventory(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всього інвентарю", e);
        }
        return items;
    }

    @Override
    public InventoryItem create(InventoryItem item) {
        String query = "INSERT INTO Inventory (inventory_id, product_id, zone_id, quantity, last_updated) VALUES (?, ?, ?, ?, ?)";
        UUID id = item.inventoryId() != null ? item.inventoryId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, item.productId().toString());
            preparedStatement.setString(3, item.zoneId().toString());
            preparedStatement.setInt(4, item.quantity());
            preparedStatement.setString(5, item.lastUpdated().format(DATE_TIME_FORMATTER));
            preparedStatement.executeUpdate();
            return new InventoryItem(id, item.productId(), item.zoneId(), item.quantity(), item.lastUpdated());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні запису інвентарю", e);
        }
    }

    @Override
    public void update(InventoryItem item) throws EntityNotFoundException {
        String query = "UPDATE Inventory SET product_id = ?, zone_id = ?, quantity = ?, last_updated = ? WHERE inventory_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, item.productId().toString());
            preparedStatement.setString(2, item.zoneId().toString());
            preparedStatement.setInt(3, item.quantity());
            preparedStatement.setString(4, item.lastUpdated().format(DATE_TIME_FORMATTER));
            preparedStatement.setString(5, item.inventoryId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Запис інвентарю з ID " + item.inventoryId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні запису інвентарю", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM Inventory WHERE inventory_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Запис інвентарю з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні запису інвентарю", e);
        }
    }

    private InventoryItem mapToInventory(ResultSet resultSet) throws SQLException {
        String updatedStr = resultSet.getString("last_updated");
        LocalDateTime updated;
        try {
            updated = LocalDateTime.parse(updatedStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                updated = LocalDateTime.parse(updatedStr);
            } catch (DateTimeParseException e2) {
                updated = LocalDateTime.now();
            }
        }
        return new InventoryItem(
            UUID.fromString(resultSet.getString("inventory_id")),
            UUID.fromString(resultSet.getString("product_id")),
            UUID.fromString(resultSet.getString("zone_id")),
            resultSet.getInt("quantity"),
            updated
        );
    }
}
