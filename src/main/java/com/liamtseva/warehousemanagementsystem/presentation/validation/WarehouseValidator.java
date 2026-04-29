package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.Warehouse;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.WarehouseRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarehouseValidator {
    private static final int MAX_NAME_LENGTH = 100;

    public static ValidationResult isNameValid(String name) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Назва складу не може бути порожньою");
            return new ValidationResult(false, errors);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            errors.add("Назва складу не може перевищувати " + MAX_NAME_LENGTH + " символів");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isNameUnique(String name, UUID warehouseId, WarehouseRepository repository) {
        ValidationResult basic = isNameValid(name);
        if (!basic.isValid()) return basic;

        List<String> errors = new ArrayList<>();
        repository.findByName(name).ifPresent(existing -> {
            if (warehouseId == null || !existing.warehouseId().equals(warehouseId)) {
                errors.add("Склад з назвою \"" + name + "\" вже існує");
            }
        });
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isWarehouseValid(Warehouse warehouse, boolean isExisting, WarehouseRepository repository) {
        List<String> errors = new ArrayList<>();
        
        ValidationResult nameResult = isNameUnique(warehouse.name(), warehouse.warehouseId(), repository);
        if (!nameResult.isValid()) errors.addAll(nameResult.getErrors());

        if (warehouse.capacitySqm() != null && warehouse.capacitySqm() <= 0) {
            errors.add("Площа складу має бути більшою за 0");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
