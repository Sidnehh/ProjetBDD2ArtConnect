package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.util.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.GalleryService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JDBC implementation of GalleryService.
 */
public class JdbcGalleryService implements GalleryService {
    private final GalleryDao galleryDao;
    private final ExhibitionDao exhibitionDao;

    public JdbcGalleryService(GalleryDao galleryDao, ExhibitionDao exhibitionDao) {
        this.galleryDao = galleryDao;
        this.exhibitionDao = exhibitionDao;
    }

    @Override
    public List<Gallery> getAllGalleries() {
        return galleryDao.findAll();
    }

    @Override
    public Optional<Gallery> getGalleryByName(String name) {
        List<Gallery> galleries = galleryDao.findAll();
        return galleries.stream()
                .filter(g -> g.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Exhibition> getExhibitionsByGallery(Gallery gallery) {
        List<Exhibition> allExhibitions = exhibitionDao.findAll();
        return allExhibitions.stream()
                .filter(e -> e.getGallery() != null && e.getGallery().getName().equals(gallery.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void createGallery(Gallery gallery) {
        String sql = "INSERT INTO Gallery (Name, Rating, StreetName, City) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gallery.getName());
            stmt.setDouble(2, gallery.getRating());
            stmt.setString(3, gallery.getStreetName());
            stmt.setString(4, gallery.getCity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating gallery: " + e.getMessage());
        }
    }

    @Override
    public void updateGallery(Gallery gallery) {
        String sql = "UPDATE Gallery SET Rating = ?, StreetName = ?, City = ? WHERE Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, gallery.getRating());
            stmt.setString(2, gallery.getStreetName());
            stmt.setString(3, gallery.getCity());
            stmt.setString(4, gallery.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating gallery: " + e.getMessage());
        }
    }

    @Override
    public void deleteGallery(String name) {
        String sql = "DELETE FROM Gallery WHERE Name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting gallery: " + e.getMessage());
        }
    }
}
