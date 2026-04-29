package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.OrderItem;
import java.util.List;
import java.util.UUID;

public interface OrderItemRepository {
    List<OrderItem> findAll();
    OrderItem findById(UUID id) throws EntityNotFoundException;
    List<OrderItem> findByOrderId(UUID orderId);
    OrderItem create(OrderItem orderItem);

    void update(OrderItem orderItem) throws EntityNotFoundException;

    void deleteById(UUID id) throws EntityNotFoundException;
}
