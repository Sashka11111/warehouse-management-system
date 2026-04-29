package com.liamtseva.warehousemanagementsystem.persistence.entity;

import java.util.UUID;

public record Product(
    UUID productId,
    UUID categoryId,
    UUID supplierId,
    String sku,
    String name,
    String description,
    String unit,
    Double price,
    Integer minStockLevel
) implements Entity, Comparable<Product> {

    @Override
    public UUID id() {
        return productId;
    }

    @Override
    public int compareTo(Product o) {
        return this.sku.compareTo(o.sku);
    }
}
