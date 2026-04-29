package com.liamtseva.warehousemanagementsystem.persistence.entity.enums;

public enum OrderType {
    INBOUND("Прихід"),
    OUTBOUND("Вихід");

    private final String ukrainianName;

    OrderType(String ukrainianName) {
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
