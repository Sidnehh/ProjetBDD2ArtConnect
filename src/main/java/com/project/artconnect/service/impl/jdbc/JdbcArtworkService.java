package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.Artist;
import com.project.artconnect.service.ArtworkService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JDBC implementation of ArtworkService.
 */
public class JdbcArtworkService implements ArtworkService {
    private final ArtworkDao artworkDao;

    public JdbcArtworkService(ArtworkDao artworkDao, ArtistDao artistDao) {
        this.artworkDao = artworkDao;
    }

    @Override
    public List<Artwork> getAllArtworks() {
        return artworkDao.findAll();
    }

    @Override
    public Optional<Artwork> getArtworkByTitle(String title) {
        List<Artwork> artworks = artworkDao.findAll();
        return artworks.stream()
                .filter(a -> a.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public List<Artwork> getArtworksByArtist(Artist artist) {
        return artworkDao.findByArtistName(artist.getName());
    }

    @Override
    public void createArtwork(Artwork artwork) throws SQLException {
        artworkDao.save(artwork);
    }

    @Override
    public void updateArtwork(Artwork artwork) throws SQLException {
        artworkDao.update(artwork);
    }

    @Override
    public void deleteArtwork(String title) {
        artworkDao.delete(title);
    }

    @Override
    public void deleteArtworkById(int artworkId) {
        artworkDao.deleteById(artworkId);
    }
}
