package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

public class SupplierRepositoryImpl implements SupplierRepository {
    private final DataSource dataSource;

    public SupplierRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Supplier findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM Suppliers WHERE supplier_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToSupplier(resultSet);
                } else {
                    throw new EntityNotFoundException("Постачальника з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку постачальника", e);
        }
    }

    @Override
    public java.util.Optional<Supplier> findByName(String name) {
        String query = "SELECT * FROM Suppliers WHERE name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return java.util.Optional.of(mapToSupplier(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку постачальника за назвою", e);
        }
        return java.util.Optional.empty();
    }

    @Override
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM Suppliers";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                suppliers.add(mapToSupplier(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всіх постачальників", e);
        }
        return suppliers;
    }

    @Override
    public Supplier create(Supplier supplier) {
        String query = "INSERT INTO Suppliers (supplier_id, name, contact_person, email, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
        UUID id = supplier.supplierId() != null ? supplier.supplierId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, supplier.name());
            preparedStatement.setString(3, supplier.contactPerson());
            preparedStatement.setString(4, supplier.email());
            preparedStatement.setString(5, supplier.phone());
            preparedStatement.setString(6, supplier.address());
            preparedStatement.executeUpdate();
            return new Supplier(id, supplier.name(), supplier.contactPerson(), supplier.email(), supplier.phone(), supplier.address());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні постачальника", e);
        }
    }

    @Override
    public void update(Supplier supplier) throws EntityNotFoundException {
        String query = "UPDATE Suppliers SET name = ?, contact_person = ?, email = ?, phone = ?, address = ? WHERE supplier_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, supplier.name());
            preparedStatement.setString(2, supplier.contactPerson());
            preparedStatement.setString(3, supplier.email());
            preparedStatement.setString(4, supplier.phone());
            preparedStatement.setString(5, supplier.address());
            preparedStatement.setString(6, supplier.supplierId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Постачальника з ID " + supplier.supplierId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні постачальника", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM Suppliers WHERE supplier_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Постачальника з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні постачальника", e);
        }
    }

    private Supplier mapToSupplier(ResultSet resultSet) throws SQLException {
        return new Supplier(
            UUID.fromString(resultSet.getString("supplier_id")),
            resultSet.getString("name"),
            resultSet.getString("contact_person"),
            resultSet.getString("email"),
            resultSet.getString("phone"),
            resultSet.getString("address")
        );
    }
}
