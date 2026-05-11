package com.liamtseva.warehousemanagementsystem.persistence.repository.impl;

import com.liamtseva.warehousemanagementsystem.domain.exception.EntityNotFoundException;
import com.liamtseva.warehousemanagementsystem.persistence.entity.Zone;
import com.liamtseva.warehousemanagementsystem.persistence.entity.enums.ZoneType;
import com.liamtseva.warehousemanagementsystem.persistence.repository.contract.ZoneRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

public class ZoneRepositoryImpl implements ZoneRepository {
    private final DataSource dataSource;

    public ZoneRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Zone findById(UUID id) throws EntityNotFoundException {
        String query = "SELECT * FROM Zones WHERE zone_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToZone(resultSet);
                } else {
                    throw new EntityNotFoundException("Зону з ID " + id + " не знайдено");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка під час пошуку зони", e);
        }
    }


    @Override
    public List<Zone> findAll() {
        List<Zone> zones = new ArrayList<>();
        String query = "SELECT * FROM Zones";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                zones.add(mapToZone(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при отриманні всіх зон", e);
        }
        return zones;
    }

    @Override
    public Zone create(Zone zone) {
        String query = "INSERT INTO Zones (zone_id, name, zone_type) VALUES (?, ?, ?)";
        UUID id = zone.zoneId() != null ? zone.zoneId() : UUID.randomUUID();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, zone.name());
            preparedStatement.setString(3, zone.zoneType() != null ? zone.zoneType().name() : null);
            preparedStatement.executeUpdate();
            return new Zone(id, zone.name(), zone.zoneType());
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при створенні зони", e);
        }
    }

    @Override
    public void update(Zone zone) throws EntityNotFoundException {
        String query = "UPDATE Zones SET name = ?, zone_type = ? WHERE zone_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, zone.name());
            preparedStatement.setString(2, zone.zoneType() != null ? zone.zoneType().name() : null);
            preparedStatement.setString(3, zone.zoneId().toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Зону з ID " + zone.zoneId() + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при оновленні зони", e);
        }
    }

    @Override
    public void deleteById(UUID id) throws EntityNotFoundException {
        String query = "DELETE FROM Zones WHERE zone_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id.toString());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new EntityNotFoundException("Зону з ID " + id + " не знайдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при видаленні зони", e);
        }
    }

    private Zone mapToZone(ResultSet resultSet) throws SQLException {
        String zoneTypeStr = resultSet.getString("zone_type");
        ZoneType zoneType = zoneTypeStr != null ? ZoneType.valueOf(zoneTypeStr) : null;
        return new Zone(
            UUID.fromString(resultSet.getString("zone_id")),
            resultSet.getString("name"),
            zoneType
        );
    }
}
