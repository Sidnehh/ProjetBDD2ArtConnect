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
    public void save(Gallery gallery) {
        String sql = "INSERT INTO Gallery (IdGallery, Name, Rating, StreetName, City) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int nextId = getNextGalleryId(conn);
            stmt.setInt(1, nextId);
            stmt.setString(2, gallery.getName());
            stmt.setDouble(3, gallery.getRating());
            stmt.setString(4, gallery.getStreetName() != null ? gallery.getStreetName() : "");
            stmt.setString(5, gallery.getCity() != null ? gallery.getCity() : "");

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving gallery: " + e.getMessage());
        }
    }

    @Override
    public void update(Gallery gallery) {
        String sql = "UPDATE Gallery SET Name = ?, Rating = ?, StreetName = ?, City = ? WHERE IdGallery = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gallery.getName());
            stmt.setDouble(2, gallery.getRating());
            stmt.setString(3, gallery.getStreetName() != null ? gallery.getStreetName() : "");
            stmt.setString(4, gallery.getCity() != null ? gallery.getCity() : "");
            stmt.setInt(5, gallery.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating gallery: " + e.getMessage());
        }
    }

    @Override
    public void delete(String name) {
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
        gallery.setId(rs.getInt("IdGallery"));
        gallery.setName(rs.getString("Name"));
        gallery.setRating(rs.getDouble("Rating"));
        gallery.setStreetName(rs.getString("StreetName"));
        gallery.setCity(rs.getString("City"));
        return gallery;
    }

    private int getNextGalleryId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(IdGallery) as maxId FROM Gallery";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("maxId") + 1;
            }
        }
        return 1;
    }
}
