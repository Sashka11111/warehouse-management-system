package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.ProductCategory;
import java.util.List;
import java.util.UUID;

public interface ProductCategoryRepository {
    List<ProductCategory> findAll();
    ProductCategory findById(UUID id) throws EntityNotFoundException;
    java.util.Optional<ProductCategory> findByName(String name);
    ProductCategory create(ProductCategory category);

    void update(ProductCategory category) throws EntityNotFoundException;

    void deleteById(UUID id) throws EntityNotFoundException;
}
