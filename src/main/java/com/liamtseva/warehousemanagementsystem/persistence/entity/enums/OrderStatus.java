package com.liamtseva.warehousemanagementsystem.persistence.entity.enums;

public enum OrderStatus {
    PENDING("Очікує"),
    PROCESSING("В обробці"),
    SHIPPED("Відправлено"),
    DELIVERED("Доставлено"),
    CANCELLED("Скасовано");

    private final String ukrainianName;

    OrderStatus(String ukrainianName) {
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
