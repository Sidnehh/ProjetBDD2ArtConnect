package com.project.artconnect.dao;

import com.project.artconnect.model.Artwork;

import java.sql.SQLException;
import java.util.List;

public interface ArtworkDao {
    List<Artwork> findAll();

    void save(Artwork artwork) throws SQLException;

    void update(Artwork artwork) throws SQLException;

    void delete(String title);

    List<Artwork> findByArtistName(String artistName);
}
