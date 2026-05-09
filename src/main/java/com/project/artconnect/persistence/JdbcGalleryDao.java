package com.project.artconnect.persistence;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation for GalleryDao.
 */
public class JdbcGalleryDao implements GalleryDao {

    @Override
    public Optional<Gallery> findById(Long id) {
        String sql = "SELECT IdGallery, Name, Rating, StreetName, City FROM Gallery WHERE IdGallery = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Gallery gallery = mapResultSetToGallery(rs);
                rs.close();
                return Optional.of(gallery);
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding gallery by id: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    @Override
    public List<Gallery> findAll() {
        List<Gallery> galleries = new ArrayList<>();
        String sql = "SELECT IdGallery, Name, Rating, StreetName, City FROM Gallery";
        
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Gallery gallery = mapResultSetToGallery(rs);
                galleries.add(gallery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all galleries: " + e.getMessage());
        }
        
        return galleries;
    }

    /**
     * Maps a ResultSet row to a Gallery object.
     */
    private Gallery mapResultSetToGallery(ResultSet rs) throws SQLException {
        Gallery gallery = new Gallery();
        gallery.setName(rs.getString("Name"));
        gallery.setRating(rs.getDouble("Rating"));
        gallery.setAddress(rs.getString("StreetName") + ", " + rs.getString("City"));
        return gallery;
    }
}
