package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Order;
import java.util.List;
import java.util.UUID;

public interface OrderRepository {
    List<Order> findAll();
    Order findById(UUID id) throws EntityNotFoundException;
    Order findByOrderNumber(String orderNumber) throws EntityNotFoundException;
    Order create(Order order);

    void update(Order order) throws EntityNotFoundException;

    void deleteById(UUID id) throws EntityNotFoundException;
}
