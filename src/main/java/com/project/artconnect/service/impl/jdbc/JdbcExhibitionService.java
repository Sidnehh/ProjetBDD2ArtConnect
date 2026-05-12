package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.List;

/**
 * JDBC implementation of ExhibitionService.
 * Note: ExhibitionService interface may need to be created if it doesn't exist.
 */
public class JdbcExhibitionService implements ExhibitionService {
    private final ExhibitionDao exhibitionDao;

    public JdbcExhibitionService(ExhibitionDao exhibitionDao) {
        this.exhibitionDao = exhibitionDao;
    }

    @Override
    public List<Exhibition> getAllExhibitions() {
        return exhibitionDao.findAll();
    }

    @Override
    public void createExhibition(Exhibition exhibition) {
        exhibitionDao.save(exhibition);
    }

    @Override
    public void updateExhibition(Exhibition exhibition) {
        exhibitionDao.update(exhibition);
    }

    @Override
    public void deleteExhibition(String title) {
        exhibitionDao.delete(title);
    }

    @Override
    public void save(Exhibition exhibition) {
        String sql = "INSERT INTO Exhibition (Title, StartDate, EndDate, Theme, IdGallery) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, exhibition.getTitle());
            stmt.setDate(2, java.sql.Date.valueOf(exhibition.getStartDate()));
            stmt.setDate(3, java.sql.Date.valueOf(exhibition.getEndDate()));
            stmt.setString(4, exhibition.getTheme());
            stmt.setInt(5, exhibition.getGallery() != null ? exhibition.getGallery().getId() : 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving exhibition: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        String sql = "UPDATE Exhibition SET StartDate = ?, EndDate = ?, Theme = ? WHERE Title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(exhibition.getStartDate()));
            stmt.setDate(2, java.sql.Date.valueOf(exhibition.getEndDate()));
            stmt.setString(3, exhibition.getTheme());
            stmt.setString(4, exhibition.getTitle());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating exhibition: " + e.getMessage(), e);
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
            throw new RuntimeException("Error deleting exhibition: " + e.getMessage(), e);
        }
    }
}
