package com.project.artconnect.persistence;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation for ExhibitionDao.
 */
public class JdbcExhibitionDao implements ExhibitionDao {

    private GalleryDao galleryDao;

    public JdbcExhibitionDao() {
        this.galleryDao = new JdbcGalleryDao();
    }

    public JdbcExhibitionDao(GalleryDao galleryDao) {
        this.galleryDao = galleryDao;
    }

    @Override
    public List<Exhibition> findAll() {
        List<Exhibition> exhibitions = new ArrayList<>();
        String sql = "SELECT IdExhibition, Title, StartDate, Theme, IdGallery FROM Exhibition";
        
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Exhibition exhibition = mapResultSetToExhibition(rs);
                exhibitions.add(exhibition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all exhibitions: " + e.getMessage());
        }
        
        return exhibitions;
    }

    @Override
    public void save(Exhibition exhibition) {
        String sql = "INSERT INTO Exhibition (IdExhibition, Title, StartDate, Theme, IdGallery) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int nextId = getNextExhibitionId(conn);
            
            stmt.setInt(1, nextId);
            stmt.setString(2, exhibition.getTitle());
            stmt.setDate(3, java.sql.Date.valueOf(exhibition.getStartDate()));
            stmt.setString(4, exhibition.getTheme() != null ? exhibition.getTheme() : "");
            stmt.setInt(5, 1); // Default gallery ID
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving exhibition: " + e.getMessage());
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        String sql = "UPDATE Exhibition SET StartDate = ?, Theme = ? WHERE Title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(exhibition.getStartDate()));
            stmt.setString(2, exhibition.getTheme() != null ? exhibition.getTheme() : "");
            stmt.setString(3, exhibition.getTitle());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating exhibition: " + e.getMessage());
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Exhibition WHERE Title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting exhibition: " + e.getMessage());
        }
    }

    /**
     * Maps a ResultSet row to an Exhibition object.
     */
    private Exhibition mapResultSetToExhibition(ResultSet rs) throws SQLException {
        Exhibition exhibition = new Exhibition();
        exhibition.setTitle(rs.getString("Title"));
        exhibition.setStartDate(LocalDate.parse(rs.getString("StartDate")));
        exhibition.setTheme(rs.getString("Theme"));
        
        // Load gallery
        int idGallery = rs.getInt("IdGallery");
        exhibition.setGallery(loadGalleryById(idGallery));
        
        return exhibition;
    }

    /**
     * Helper method to load a Gallery by ID.
     */
    private Gallery loadGalleryById(int idGallery) {
        String sql = "SELECT IdGallery, Name, Rating, StreetName, City FROM Gallery WHERE IdGallery = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idGallery);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Gallery gallery = new Gallery();
                gallery.setName(rs.getString("Name"));
                gallery.setRating(rs.getDouble("Rating"));
                gallery.setAddress(rs.getString("StreetName") + ", " + rs.getString("City"));
                rs.close();
                return gallery;
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Helper method to get the next available exhibition ID.
     */
    private int getNextExhibitionId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(IdExhibition) as maxId FROM Exhibition";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                return maxId + 1;
            }
        }
        return 1;
    }
}
