package com.liamtseva.warehousemanagementsystem.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record Warehouse(
    UUID warehouseId,
    String name,
    String address,
    Double capacitySqm,
    LocalDateTime createdAt
) implements Entity, Comparable<Warehouse> {

    @Override
    public UUID id() {
        return warehouseId;
    }

    @Override
    public int compareTo(Warehouse o) {
        return this.name.compareTo(o.name);
    }
}
