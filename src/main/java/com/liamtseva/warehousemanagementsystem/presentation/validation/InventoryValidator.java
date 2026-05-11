package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.InventoryItem;
import java.util.ArrayList;
import java.util.List;

public class InventoryValidator {
    public static ValidationResult isQuantityValid(Integer quantity) {
        List<String> errors = new ArrayList<>();
        if (quantity == null) {
            errors.add("Кількість не може бути порожньою");
        } else if (quantity < 0) {
            errors.add("Кількість не може бути від'ємною");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isInventoryValid(InventoryItem item) {
        List<String> errors = new ArrayList<>();
        ValidationResult qRes = isQuantityValid(item.quantity());
        if (!qRes.isValid()) errors.addAll(qRes.getErrors());

        if (item.productId() == null) errors.add("Необхідно вибрати товар");
        if (item.zoneId() == null) errors.add("Необхідно вибрати зону");
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
}
