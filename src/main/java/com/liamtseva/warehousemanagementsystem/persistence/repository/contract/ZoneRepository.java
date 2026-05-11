package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import java.util.List;
import java.util.UUID;

public interface ZoneRepository {
    List<Zone> findAll();
    Zone findById(UUID id) throws EntityNotFoundException;
    Zone create(Zone zone);

    void update(Zone zone) throws EntityNotFoundException;

    void deleteById(UUID id) throws EntityNotFoundException;
}
