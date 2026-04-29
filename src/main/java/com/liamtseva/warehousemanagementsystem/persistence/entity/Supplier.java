package com.liamtseva.warehousemanagementsystem.persistence.entity;

import java.util.UUID;

public record Supplier(
    UUID supplierId,
    String name,
    String contactPerson,
    String email,
    String phone,
    String address
) implements Entity, Comparable<Supplier> {

    @Override
    public UUID id() {
        return supplierId;
    }

    @Override
    public int compareTo(Supplier o) {
        return this.name.compareTo(o.name);
    }
}
