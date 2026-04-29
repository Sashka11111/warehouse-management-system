package com.liamtseva.warehousemanagementsystem.persistence.entity;

import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.OrderStatus;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.OrderType;
import java.time.LocalDateTime;
import java.util.UUID;

public record Order(
    UUID orderId,
    UUID userId,
    String orderNumber,
    OrderType type,
    OrderStatus status,
    Double totalAmount,
    LocalDateTime createdAt,
    LocalDateTime requiredDate,
    LocalDateTime completedAt,
    String notes
) implements Entity, Comparable<Order> {

    @Override
    public UUID id() {
        return orderId;
    }

    @Override
    public int compareTo(Order o) {
        return this.orderNumber.compareTo(o.orderNumber);
    }
}
