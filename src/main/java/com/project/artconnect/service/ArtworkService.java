package com.project.artconnect.service;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.Artist;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ArtworkService {
    List<Artwork> getAllArtworks();

    Optional<Artwork> getArtworkByTitle(String title);

    List<Artwork> getArtworksByArtist(Artist artist);

    void createArtwork(Artwork artwork) throws SQLException;

    void updateArtwork(Artwork artwork) throws SQLException;

    void deleteArtwork(String title);

    default void deleteArtworkById(int artworkId) {
        throw new UnsupportedOperationException("deleteArtworkById is not implemented");
    }
}
