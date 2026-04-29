package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Warehouse;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.WarehouseRepository;
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

public class WarehouseRepositoryImpl implements WarehouseRepository {
    private final DataSource dataSource;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public WarehouseRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Warehouse findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM Warehouses WHERE warehouse_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToWarehouse(resultSet);
                } else {
                    throw new EntityNotFoundException("Склад з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку складу", e);
        }
    }

    @Override
    public java.util.Optional<Warehouse> findByName(String name) {
        String query = "SELECT * FROM Warehouses WHERE name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return java.util.Optional.of(mapToWarehouse(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку складу за назвою", e);
        }
        return java.util.Optional.empty();
    }

    @Override
    public List<Warehouse> findAll() {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM Warehouses";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                warehouses.add(mapToWarehouse(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всіх складів", e);
        }
        return warehouses;
    }

    @Override
    public Warehouse create(Warehouse warehouse) {
        String query = "INSERT INTO Warehouses (warehouse_id, name, address, capacity_sqm, created_at) VALUES (?, ?, ?, ?, ?)";
        UUID id = warehouse.warehouseId() != null ? warehouse.warehouseId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, warehouse.name());
            preparedStatement.setString(3, warehouse.address());
            preparedStatement.setObject(4, warehouse.capacitySqm());
            preparedStatement.setString(5, warehouse.createdAt().format(DATE_TIME_FORMATTER));
            preparedStatement.executeUpdate();
            return new Warehouse(id, warehouse.name(), warehouse.address(), warehouse.capacitySqm(), warehouse.createdAt());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні складу", e);
        }
    }

    @Override
    public void update(Warehouse warehouse) throws EntityNotFoundException {
        String query = "UPDATE Warehouses SET name = ?, address = ?, capacity_sqm = ? WHERE warehouse_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, warehouse.name());
            preparedStatement.setString(2, warehouse.address());
            preparedStatement.setObject(3, warehouse.capacitySqm());
            preparedStatement.setString(4, warehouse.warehouseId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Склад з ID " + warehouse.warehouseId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні складу", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM Warehouses WHERE warehouse_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Склад з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні складу", e);
        }
    }

    private Warehouse mapToWarehouse(ResultSet resultSet) throws SQLException {
        String createdAtStr = resultSet.getString("created_at");
        LocalDateTime createdAt;
        try {
            createdAt = LocalDateTime.parse(createdAtStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                createdAt = LocalDateTime.parse(createdAtStr);
            } catch (DateTimeParseException e2) {
                createdAt = LocalDateTime.now();
            }
        }
        return new Warehouse(
            UUID.fromString(resultSet.getString("warehouse_id")),
            resultSet.getString("name"),
            resultSet.getString("address"),
            resultSet.getDouble("capacity_sqm"),
            createdAt
        );
    }
}
