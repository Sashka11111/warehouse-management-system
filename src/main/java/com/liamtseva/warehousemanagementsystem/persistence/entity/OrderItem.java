package com.liamtseva.warehousemanagementsystem.persistence.entity;

import java.util.UUID;

public record OrderItem(
    UUID orderItemId,
    UUID orderId,
    UUID productId,
    Integer quantity,
    Double unitPrice
) implements Entity, Comparable<OrderItem> {

    @Override
    public UUID id() {
        return orderItemId;
    }

    @Override
    public int compareTo(OrderItem o) {
        int orderComparison = this.orderId.compareTo(o.orderId);
        if (orderComparison != 0) return orderComparison;
        return this.productId.compareTo(o.productId);
    }
}
