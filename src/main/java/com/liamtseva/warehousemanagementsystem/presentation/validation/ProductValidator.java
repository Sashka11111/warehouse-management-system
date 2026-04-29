package com.liamtseva.warehousemanagementsystem.presentation.validation;

import com.liamtseva.warehousemanagementsystem.persistence.entity.Product;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductValidator {
    public static ValidationResult isSkuValid(String sku) {
        List<String> errors = new ArrayList<>();
        if (sku == null || sku.trim().isEmpty()) {
            errors.add("Артикул (SKU) не може бути порожнім");
        } else if (sku.length() > 50) {
            errors.add("Артикул не може перевищувати 50 символів");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isSkuUnique(String sku, UUID productId, ProductRepository repository) {
        ValidationResult basic = isSkuValid(sku);
        if (!basic.isValid()) return basic;

        List<String> errors = new ArrayList<>();
        repository.findBySku(sku).ifPresent(existing -> {
            if (productId == null || !existing.productId().equals(productId)) {
                errors.add("Товар з артикулом \"" + sku + "\" вже існує");
            }
        });
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult isProductValid(Product product, ProductRepository repository) {
        List<String> errors = new ArrayList<>();
        
        ValidationResult skuRes = isSkuUnique(product.sku(), product.productId(), repository);
        if (!skuRes.isValid()) errors.addAll(skuRes.getErrors());

        if (product.name() == null || product.name().trim().isEmpty()) {
            errors.add("Назва товару не може бути порожньою");
        }

        if (product.price() == null || product.price() < 0) {
            errors.add("Ціна не може бути від'ємною");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
