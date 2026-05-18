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
        String sql = "SELECT IdWorkshop, Title, Date_, Price, Level, IdArtist FROM Workshop WHERE IdWorkshop = ? ORDER BY Date ASC";
        
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

    @Override
    public void save(Workshop workshop) throws SQLException{
        String sql = "INSERT INTO Workshop (IdWorkshop, Title, Date_, Price, Level, IdArtist) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int nextId = getNextWorkshopId(conn);
            stmt.setInt(1, nextId);
            stmt.setString(2, workshop.getTitle());
            stmt.setTimestamp(3, Timestamp.valueOf(workshop.getDate()));
            stmt.setDouble(4, workshop.getPrice());
            stmt.setString(5, workshop.getLevel() != null ? workshop.getLevel() : "Beginner");
            stmt.setInt(6, workshop.getInstructor() != null ? workshop.getInstructor().getIdArtist() : 0);

            stmt.executeUpdate();
            }
    }

    @Override
    public void update(Workshop workshop) throws SQLException{
        String sql = "UPDATE Workshop SET Title = ?, Date_ = ?, Price = ?, Level = ?, IdArtist = ? WHERE IdWorkshop = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, workshop.getTitle());
            stmt.setTimestamp(2, Timestamp.valueOf(workshop.getDate()));
            stmt.setDouble(3, workshop.getPrice());
            stmt.setString(4, workshop.getLevel() != null ? workshop.getLevel() : "Beginner");
            stmt.setInt(5, workshop.getInstructor() != null ? workshop.getInstructor().getIdArtist() : 0);
            stmt.setInt(6, workshop.getId());

            stmt.executeUpdate();
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
            e.printStackTrace();
            throw new RuntimeException("Error deleting workshop: " + e.getMessage());
        }
    }

    /**
     * Maps a ResultSet row to a Workshop object.
     */
    private Workshop mapResultSetToWorkshop(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();
        workshop.setId(rs.getInt("IdWorkshop"));
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
                artist.setIdArtist(rs.getInt("IdArtist"));
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

    private int getNextWorkshopId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(IdWorkshop) as maxId FROM Workshop";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("maxId") + 1;
            }
        }
        return 1;
    }
}
