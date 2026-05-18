package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation for ArtistDao.
 */
public class JdbcArtistDao implements ArtistDao {

    @Override
    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT IdArtist, Email, Name, City, BirthYear FROM Artist";
        
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Artist artist = mapResultSetToArtist(rs);
                artists.add(artist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all artists: " + e.getMessage());
        }
        
        return artists;
    }

    @Override
    public void save(Artist artist) {
        String sql = "INSERT INTO Artist (IdArtist, Email, Name, City, BirthYear) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Generate a new IdArtist (in production, use AUTO_INCREMENT)
            int nextId = getNextArtistId(conn);
            
            stmt.setInt(1, nextId);
            stmt.setString(2, artist.getContactEmail() != null ? artist.getContactEmail() : "");
            stmt.setString(3, artist.getName());
            stmt.setString(4, artist.getCity() != null ? artist.getCity() : "");
            stmt.setInt(5, artist.getBirthYear() != null ? artist.getBirthYear() : 0);
            
            stmt.executeUpdate();
            artist.setName(artist.getName()); // Ensure name is set
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving artist: " + e.getMessage());
        }
    }

    @Override
    public void update(Artist artist) {
        String sql = "UPDATE Artist SET Name = ?, Email = ?, City = ?, BirthYear = ? WHERE IdArtist = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, artist.getName());
            stmt.setString(2, artist.getContactEmail() != null ? artist.getContactEmail() : "");
            stmt.setString(3, artist.getCity() != null ? artist.getCity() : "");
            stmt.setInt(4, artist.getBirthYear() != null ? artist.getBirthYear() : 0);
            stmt.setInt(5, artist.getIdArtist());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating artist: " + e.getMessage());
        }
    }

    @Override
    public void delete(String artistName) throws SQLException{
        String sql = "DELETE FROM Artist WHERE Name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, artistName);
                stmt.executeUpdate();
            }
    }

    @Override
    public List<Artist> findByCity(String city) {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT IdArtist, Email, Name, City, BirthYear FROM Artist WHERE City = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, city);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Artist artist = mapResultSetToArtist(rs);
                artists.add(artist);
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding artists by city: " + e.getMessage());
        }
        
        return artists;
    }

    /**
     * Maps a ResultSet row to an Artist object.
     */
    private Artist mapResultSetToArtist(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setIdArtist(rs.getInt("IdArtist"));
        artist.setName(rs.getString("Name"));
        artist.setBio(""); // Not stored in DB
        artist.setBirthYear(rs.getInt("BirthYear"));
        artist.setContactEmail(rs.getString("Email"));
        artist.setCity(rs.getString("City"));
        return artist;
    }

    /**
     * Helper method to get the next available artist ID.
     */
    private int getNextArtistId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(IdArtist) as maxId FROM Artist";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                return maxId + 1;
            }
        }
        return 1; // If no artists exist
    }
}
