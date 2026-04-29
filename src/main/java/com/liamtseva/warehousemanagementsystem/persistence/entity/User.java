package com.liamtseva.warehousemanagementsystem.persistence.entity;

import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record User(
    UUID userId,
    String username,
    String password,
    String fullName,
    UserRole role,
    String email,
    LocalDateTime createdAt
) implements Entity, Comparable<User> {

    @Override
    public UUID id() {
        return userId;
    }

    @Override
    public int compareTo(User o) {
        return this.username.compareTo(o.username);
    }
}
