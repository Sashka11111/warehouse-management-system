package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.Order;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderValidator {
    public static ValidationResult isOrderNumberValid(String orderNumber) {
        List<String> errors = new ArrayList<>();
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            errors.add("Номер замовлення не може бути порожнім");
        } else if (orderNumber.length() > 50) {
            errors.add("Номер замовлення не може перевищувати 50 символів");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isOrderNumberUnique(String orderNumber, UUID orderId, OrderRepository repository) {
        ValidationResult basic = isOrderNumberValid(orderNumber);
        if (!basic.isValid()) return basic;

        List<String> errors = new ArrayList<>();
        try {
            Order existing = repository.findByOrderNumber(orderNumber);
            if (orderId == null || !existing.orderId().equals(orderId)) {
                errors.add("Замовлення з номером \"" + orderNumber + "\" вже існує");
            }
        } catch (Exception e) {
            // Якщо не знайдено - все ок
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isOrderValid(Order order, OrderRepository repository) {
        List<String> errors = new ArrayList<>();
        ValidationResult numRes = isOrderNumberUnique(order.orderNumber(), order.orderId(), repository);
        if (!numRes.isValid()) errors.addAll(numRes.getErrors());

        if (order.type() == null) errors.add("Тип замовлення має бути вказаний");
        if (order.status() == null) errors.add("Статус замовлення має бути вказаний");

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
