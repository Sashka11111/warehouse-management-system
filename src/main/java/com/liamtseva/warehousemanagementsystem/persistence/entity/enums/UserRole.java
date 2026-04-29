package com.liamtseva.warehousemanagementsystem.persistence.entity.enums;

public enum UserRole {
    ADMIN("Адміністратор"),
    MANAGER("Менеджер"),
    OPERATOR("Оператор");

    private final String ukrainianName;

    UserRole(String ukrainianName) {
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
