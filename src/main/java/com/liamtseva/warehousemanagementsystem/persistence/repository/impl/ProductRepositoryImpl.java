package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

public class ProductRepositoryImpl implements ProductRepository {
    private final DataSource dataSource;

    public ProductRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Product findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM Products WHERE product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToProduct(resultSet);
                } else {
                    throw new EntityNotFoundException("Товар з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку товару", e);
        }
    }

    @Override
    public java.util.Optional<Product> findBySku(String sku) {
        String query = "SELECT * FROM Products WHERE sku = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, sku);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return java.util.Optional.of(mapToProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку товару за артикулом", e);
        }
        return java.util.Optional.empty();
    }

    @Override
    public List<Product> findByCategoryId(UUID categoryId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products WHERE category_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categoryId.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapToProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні товарів за категорією", e);
        }
        return products;
    }

    @Override
    public List<Product> findBySupplierId(UUID supplierId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products WHERE supplier_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, supplierId.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapToProduct(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні товарів за постачальником", e);
        }
        return products;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                products.add(mapToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всіх товарів", e);
        }
        return products;
    }

    @Override
    public Product create(Product product) {
        String query = "INSERT INTO Products (product_id, category_id, supplier_id, sku, name, description, unit, price, min_stock_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        UUID id = product.productId() != null ? product.productId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, product.categoryId() != null ? product.categoryId().toString() : null);
            preparedStatement.setString(3, product.supplierId() != null ? product.supplierId().toString() : null);
            preparedStatement.setString(4, product.sku());
            preparedStatement.setString(5, product.name());
            preparedStatement.setString(6, product.description());
            preparedStatement.setString(7, product.unit());
            preparedStatement.setDouble(8, product.price());
            preparedStatement.setInt(9, product.minStockLevel());
            preparedStatement.executeUpdate();
            return new Product(id, product.categoryId(), product.supplierId(), product.sku(), product.name(), product.description(), product.unit(), product.price(), product.minStockLevel());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні товару", e);
        }
    }

    @Override
    public void update(Product product) throws EntityNotFoundException {
        String query = "UPDATE Products SET category_id = ?, supplier_id = ?, sku = ?, name = ?, description = ?, unit = ?, price = ?, min_stock_level = ? WHERE product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, product.categoryId() != null ? product.categoryId().toString() : null);
            preparedStatement.setString(2, product.supplierId() != null ? product.supplierId().toString() : null);
            preparedStatement.setString(3, product.sku());
            preparedStatement.setString(4, product.name());
            preparedStatement.setString(5, product.description());
            preparedStatement.setString(6, product.unit());
            preparedStatement.setDouble(7, product.price());
            preparedStatement.setInt(8, product.minStockLevel());
            preparedStatement.setString(9, product.productId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Товар з ID " + product.productId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні товару", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM Products WHERE product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Товар з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні товару", e);
        }
    }

    private Product mapToProduct(ResultSet resultSet) throws SQLException {
        String catIdStr = resultSet.getString("category_id");
        String supIdStr = resultSet.getString("supplier_id");
        return new Product(
            UUID.fromString(resultSet.getString("product_id")),
            catIdStr != null ? UUID.fromString(catIdStr) : null,
            supIdStr != null ? UUID.fromString(supIdStr) : null,
            resultSet.getString("sku"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getString("unit"),
            resultSet.getDouble("price"),
            resultSet.getInt("min_stock_level")
        );
    }
}
