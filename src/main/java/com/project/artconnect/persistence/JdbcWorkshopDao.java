package com.project.artconnect.persistence;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Artist;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation for WorkshopDao.
 */
public class JdbcWorkshopDao implements WorkshopDao {

    private ArtistDao artistDao;

    public JdbcWorkshopDao() {
        this.artistDao = new JdbcArtistDao();
    }

    public JdbcWorkshopDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    @Override
    public Optional<Workshop> findById(Long id) {
        String sql = "SELECT IdWorkshop, Title, Date_, Price, Level, IdArtist FROM Workshop WHERE IdWorkshop = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Workshop workshop = mapResultSetToWorkshop(rs);
                rs.close();
                return Optional.of(workshop);
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding workshop by id: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    @Override
    public List<Workshop> findAll() {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT IdWorkshop, Title, Date_, Price, Level, IdArtist FROM Workshop";
        
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Workshop workshop = mapResultSetToWorkshop(rs);
                workshops.add(workshop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all workshops: " + e.getMessage());
        }
        
        return workshops;
    }

    /**
     * Maps a ResultSet row to a Workshop object.
     */
    private Workshop mapResultSetToWorkshop(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();
        workshop.setTitle(rs.getString("Title"));
        workshop.setDate(rs.getTimestamp("Date_").toLocalDateTime());
        workshop.setPrice(rs.getDouble("Price"));
        workshop.setLevel(rs.getString("Level"));
        
        // Load instructor (Artist)
        int idArtist = rs.getInt("IdArtist");
        workshop.setInstructor(loadArtistById(idArtist));
        
        return workshop;
    }

    /**
     * Helper method to load an Artist by ID.
     */
    private Artist loadArtistById(int idArtist) {
        String sql = "SELECT IdArtist, Email, Name, City, BirthYear FROM Artist WHERE IdArtist = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idArtist);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Artist artist = new Artist();
                artist.setName(rs.getString("Name"));
                artist.setBirthYear(rs.getInt("BirthYear"));
                artist.setContactEmail(rs.getString("Email"));
                artist.setCity(rs.getString("City"));
                rs.close();
                return artist;
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
