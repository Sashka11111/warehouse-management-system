package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import java.util.ArrayList;
import java.util.List;

public class ZoneValidator {
    public static ValidationResult isNameValid(String name) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Назва зони не може бути порожньою");
        } else if (name.length() > 50) {
            errors.add("Назва зони не може перевищувати 50 символів");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isZoneValid(Zone zone) {
        List<String> errors = new ArrayList<>();
        ValidationResult nameRes = isNameValid(zone.name());
        if (!nameRes.isValid()) errors.addAll(nameRes.getErrors());

        if (zone.warehouseId() == null) {
            errors.add("Необхідно вибрати склад для зони");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }
}
