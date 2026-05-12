package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductCategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductCategoryValidator {
    public static ValidationResult isNameValid(String name) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Назва категорії не може бути порожньою");
        } else if (name.length() > 50) {
            errors.add("Назва категорії не може перевищувати 50 символів");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isNameUnique(String name, UUID categoryId, ProductCategoryRepository repository) {
        ValidationResult basic = isNameValid(name);
        if (!basic.isValid()) return basic;

        List<String> errors = new ArrayList<>();
        repository.findByName(name).ifPresent(existing -> {
            if (categoryId == null || !existing.categoryId().equals(categoryId)) {
                errors.add("Категорія з назвою \"" + name + "\" вже існує");
            }
        });
        return new ValidationResult(errors.isEmpty(), errors);
    }
}
