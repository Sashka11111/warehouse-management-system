package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findAll();
    Product findById(UUID id) throws EntityNotFoundException;
    java.util.Optional<Product> findBySku(String sku);
    Product create(Product product);

    void update(Product product) throws EntityNotFoundException;

    void deleteById(UUID id) throws EntityNotFoundException;
}
