package com.liamtseva.warehousemanagementsystem.persistence.entity;

import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.ZoneType;
import java.util.UUID;

public record Zone(
    UUID zoneId,
    UUID warehouseId,
    String name,
    ZoneType zoneType
) implements Entity, Comparable<Zone> {

    @Override
    public UUID id() {
        return zoneId;
    }

    @Override
    public int compareTo(Zone o) {
        int warehouseComparison = this.warehouseId.compareTo(o.warehouseId);
        if (warehouseComparison != 0) return warehouseComparison;
        return this.name.compareTo(o.name);
    }
}
