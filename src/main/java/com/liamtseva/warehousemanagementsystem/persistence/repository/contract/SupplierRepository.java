package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Supplier;
import java.util.List;
import java.util.UUID;

public interface SupplierRepository {
    List<Supplier> findAll();
    Supplier findById(UUID id) throws EntityNotFoundException;
    java.util.Optional<Supplier> findByName(String name);
    Supplier create(Supplier supplier);

    void update(Supplier supplier) throws EntityNotFoundException;

    void deleteById(UUID id) throws EntityNotFoundException;
}
