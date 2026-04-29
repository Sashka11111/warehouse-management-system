package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.InventoryItem;
import java.util.List;
import java.util.UUID;

public interface InventoryRepository {
    List<InventoryItem> findAll();
    InventoryItem findById(UUID id) throws EntityNotFoundException;
    List<InventoryItem> findByProductId(UUID productId);
    List<InventoryItem> findByLocationId(UUID locationId);
    InventoryItem create(InventoryItem inventoryItem);
    void update(InventoryItem item) throws EntityNotFoundException;
    void deleteById(UUID id) throws EntityNotFoundException;
}
