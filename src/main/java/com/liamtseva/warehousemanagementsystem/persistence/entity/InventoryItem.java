package com.liamtseva.warehousemanagementsystem.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryItem(
    UUID inventoryId,
    UUID productId,
    UUID zoneId,
    Integer quantity,
    LocalDateTime lastUpdated
) implements Entity, Comparable<InventoryItem> {

    @Override
    public UUID id() {
        return inventoryId;
    }

    @Override
    public int compareTo(InventoryItem o) {
        return this.inventoryId.compareTo(o.inventoryId);
    }
}
