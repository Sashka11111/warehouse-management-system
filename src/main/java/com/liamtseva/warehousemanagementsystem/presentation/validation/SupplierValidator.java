package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.SupplierRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class SupplierValidator {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^\\+?[0-9]{10,15}$";

    public static ValidationResult isNameValid(String name) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Назва постачальника не може бути порожньою");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isNameUnique(String name, UUID supplierId, SupplierRepository repository) {
        ValidationResult basic = isNameValid(name);
        if (!basic.isValid()) return basic;

        List<String> errors = new ArrayList<>();
        repository.findByName(name).ifPresent(existing -> {
            if (supplierId == null || !existing.supplierId().equals(supplierId)) {
                errors.add("Постачальник з назвою \"" + name + "\" вже існує");
            }
        });
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isSupplierValid(Supplier supplier, SupplierRepository repository) {
        List<String> errors = new ArrayList<>();
        
        ValidationResult nameRes = isNameUnique(supplier.name(), supplier.supplierId(), repository);
        if (!nameRes.isValid()) errors.addAll(nameRes.getErrors());

        if (supplier.email() != null && !supplier.email().isEmpty()) {
            if (!Pattern.matches(EMAIL_PATTERN, supplier.email())) {
                errors.add("Некоректний формат email");
            }
        }

        if (supplier.phone() != null && !supplier.phone().isEmpty()) {
            if (!Pattern.matches(PHONE_PATTERN, supplier.phone())) {
                errors.add("Некоректний формат телефону");
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
