package com.project.artconnect.dao;

import com.project.artconnect.model.Gallery;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GalleryDao {
    Optional<Gallery> findById(Long id);

    List<Gallery> findAll();

    void save(Gallery gallery) throws SQLException;;

    void update(Gallery gallery) throws SQLException;

    void delete(String name);
}
