package com.liamtseva.warehousemanagementsystem.persistence.entity;

import java.util.UUID;

public record ProductCategory(
    UUID categoryId,
    String categoryName,
    String description
) implements Entity, Comparable<ProductCategory> {

    @Override
    public UUID id() {
        return categoryId;
    }

    @Override
    public int compareTo(ProductCategory o) {
        return this.categoryName.compareTo(o.categoryName);
    }
}
