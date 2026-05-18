package com.project.artconnect.dao;

import com.project.artconnect.model.Artist;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object for Artist entity.
 */
public interface ArtistDao {
    List<Artist> findAll();

    void save(Artist artist);

    void update(Artist artist);

    void delete(String artistName) throws SQLException;

    List<Artist> findByCity(String city);
}
