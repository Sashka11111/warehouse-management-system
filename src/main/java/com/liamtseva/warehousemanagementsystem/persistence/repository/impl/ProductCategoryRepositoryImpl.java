package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

public class ProductCategoryRepositoryImpl implements ProductCategoryRepository {
    private final DataSource dataSource;

    public ProductCategoryRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ProductCategory findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM ProductCategories WHERE category_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToCategory(resultSet);
                } else {
                    throw new EntityNotFoundException("Категорію з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку категорії", e);
        }
    }

    @Override
    public java.util.Optional<ProductCategory> findByName(String name) {
        String query = "SELECT * FROM ProductCategories WHERE category_name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return java.util.Optional.of(mapToCategory(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку категорії за назвою", e);
        }
        return java.util.Optional.empty();
    }

    @Override
    public List<ProductCategory> findAll() {
        List<ProductCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM ProductCategories";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(mapToCategory(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всіх категорій", e);
        }
        return categories;
    }

    @Override
    public ProductCategory create(ProductCategory category) {
        String query = "INSERT INTO ProductCategories (category_id, category_name, description) VALUES (?, ?, ?)";
        UUID id = category.categoryId() != null ? category.categoryId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, category.categoryName());
            preparedStatement.setString(3, category.description());
            preparedStatement.executeUpdate();
            return new ProductCategory(id, category.categoryName(), category.description());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні категорії", e);
        }
    }

    @Override
    public void update(ProductCategory category) throws EntityNotFoundException {
        String query = "UPDATE ProductCategories SET category_name = ?, description = ? WHERE category_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, category.categoryName());
            preparedStatement.setString(2, category.description());
            preparedStatement.setString(3, category.categoryId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Категорію з ID " + category.categoryId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні категорії", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM ProductCategories WHERE category_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Категорію з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні категорії", e);
        }
    }

    private ProductCategory mapToCategory(ResultSet resultSet) throws SQLException {
        return new ProductCategory(
            UUID.fromString(resultSet.getString("category_id")),
            resultSet.getString("category_name"),
            resultSet.getString("description")
        );
    }
}
