package com.liamtseva.warehousemanagementsystem.persistence.repository.contract;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    List<User> findAll();
    User findById(UUID id) throws EntityNotFoundException;
    Optional<User> findByUsername(String username);
    User create(User user);
    void update(User user) throws EntityNotFoundException;
    void deleteById(UUID id) throws EntityNotFoundException;
}
