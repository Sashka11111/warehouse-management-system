package com.liamtseva.warehousemanagementsystem.persistence.entity.enums;

public enum ZoneType {
    COLD("Холодна зона"),
    DRY("Суха зона"),
    HAZARDOUS("Небезпечні вантажі"),
    GENERAL("Загальна зона");

    private final String ukrainianName;

    ZoneType(String ukrainianName) {
        this.ukrainianName = ukrainianName;
    }

    public String getUkrainianName() {
        return ukrainianName;
    }

    @Override
    public String toString() {
        return ukrainianName;
    }
}
