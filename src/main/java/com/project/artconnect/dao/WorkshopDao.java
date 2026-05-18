package com.project.artconnect.dao;

import com.project.artconnect.model.Workshop;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WorkshopDao {
    Optional<Workshop> findById(Long id);

    List<Workshop> findAll();

    void save(Workshop workshop) throws SQLException;

    void update(Workshop workshop) throws SQLException;

    void delete(String title);
}
