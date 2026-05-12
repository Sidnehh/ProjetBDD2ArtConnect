package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of WorkshopService.
 */
public class JdbcWorkshopService implements WorkshopService {
    private final WorkshopDao workshopDao;

    public JdbcWorkshopService(WorkshopDao workshopDao) {
        this.workshopDao = workshopDao;
    }

    @Override
    public List<Workshop> getAllWorkshops() {
        return workshopDao.findAll();
    }

    @Override
    public Optional<Workshop> getWorkshopByTitle(String title) {
        List<Workshop> workshops = workshopDao.findAll();
        return workshops.stream()
                .filter(w -> w.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public void bookWorkshop(Workshop workshop, CommunityMember member) {
        // TODO: Implement workshop booking logic with database transaction
        // This would involve inserting into RegisterWorkshop table
    }

    @Override
    public List<Booking> getBookingsByMember(CommunityMember member) {
        // TODO: Implement booking retrieval from database
        return List.of();
    }

    @Override
    public void save(Workshop workshop) {
        String sql = "INSERT INTO Workshop (Title, Date_, Price, Level, IdArtist) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, workshop.getTitle());
            stmt.setTimestamp(2, Timestamp.valueOf(workshop.getDate()));
            stmt.setDouble(3, workshop.getPrice());
            stmt.setString(4, workshop.getLevel());
            stmt.setInt(5, workshop.getInstructor() != null ? workshop.getInstructor().getIdArtist() : 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving workshop: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Workshop workshop) {
        String sql = "UPDATE Workshop SET Title = ?, Date_ = ?, Price = ?, Level = ? WHERE Title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, workshop.getTitle());
            stmt.setTimestamp(2, Timestamp.valueOf(workshop.getDate()));
            stmt.setDouble(3, workshop.getPrice());
            stmt.setString(4, workshop.getLevel());
            stmt.setString(5, workshop.getTitle());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating workshop: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Workshop WHERE Title = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting workshop: " + e.getMessage(), e);
        }
    }
}
