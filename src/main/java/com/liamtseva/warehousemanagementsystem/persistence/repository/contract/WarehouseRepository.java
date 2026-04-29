package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Warehouse;
import java.util.List;
import java.util.UUID;

public interface WarehouseRepository {
    List<Warehouse> findAll();
    Warehouse findById(UUID id) throws EntityNotFoundException;
    java.util.Optional<Warehouse> findByName(String name);
    Warehouse create(Warehouse warehouse);
    void update(Warehouse warehouse) throws EntityNotFoundException;
    void deleteById(UUID id) throws EntityNotFoundException;
}
