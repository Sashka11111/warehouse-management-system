package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.OrderItem;
import java.util.ArrayList;
import java.util.List;

public class OrderItemValidator {
    public static ValidationResult isQuantityValid(Integer quantity) {
        List<String> errors = new ArrayList<>();
        if (quantity == null || quantity <= 0) {
            errors.add("Кількість має бути більшою за 0");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isPriceValid(Double price) {
        List<String> errors = new ArrayList<>();
        if (price == null || price < 0) {
            errors.add("Ціна не може бути від'ємною");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isOrderItemValid(OrderItem item) {
        List<String> errors = new ArrayList<>();
        ValidationResult qRes = isQuantityValid(item.quantity());
        if (!qRes.isValid()) errors.addAll(qRes.getErrors());

        ValidationResult pRes = isPriceValid(item.unitPrice());
        if (!pRes.isValid()) errors.addAll(pRes.getErrors());

        if (item.productId() == null) errors.add("Необхідно вибрати товар");
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
}
