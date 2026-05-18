package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.Artist;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation for ArtworkDao.
 */
public class JdbcArtworkDao implements ArtworkDao {

    public JdbcArtworkDao() {
    }

    public JdbcArtworkDao(ArtistDao artistDao) {
    }

    @Override
    public List<Artwork> findAll() {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT IdArtwork, Title, Price, Status, Type, IdArtist FROM Artwork";
        
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Artwork artwork = mapResultSetToArtwork(rs);
                artworks.add(artwork);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all artworks: " + e.getMessage());
        }
        
        return artworks;
    }

    @Override
    public void save(Artwork artwork) throws SQLException{
        String sql = "INSERT INTO Artwork (IdArtwork, Title, Price, Status, Type, IdArtist) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int nextId = getNextArtworkId(conn);
            
            stmt.setInt(1, nextId);
            stmt.setString(2, artwork.getTitle());
            stmt.setDouble(3, artwork.getPrice());
            stmt.setString(4, artwork.getStatus() != null ? artwork.getStatus().toString() : "FOR_SALE");
            stmt.setString(5, artwork.getType());
            stmt.setInt(6, artwork.getArtist() != null ? artwork.getArtist().getIdArtist() : 0);
            
            stmt.executeUpdate();
            artwork.setId(nextId);
            
        }
    }

    @Override
    public void update(Artwork artwork) throws SQLException{
        String sql = "UPDATE Artwork SET Title = ?, Price = ?, Status = ?, Type = ?, IdArtist = ? WHERE IdArtwork = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, artwork.getTitle());
            stmt.setDouble(2, artwork.getPrice());
            stmt.setString(3, artwork.getStatus() != null ? artwork.getStatus().toString() : "FOR_SALE");
            stmt.setString(4, artwork.getType());
            stmt.setInt(5, artwork.getArtist() != null ? artwork.getArtist().getIdArtist() : 0);
            stmt.setInt(6, artwork.getId());
            
            stmt.executeUpdate(); 
        } 
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Artwork WHERE Title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting artwork: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(int artworkId) {
        String sql = "DELETE FROM Artwork WHERE IdArtwork = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, artworkId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting artwork: " + e.getMessage());
        }
    }

    @Override
    public List<Artwork> findByArtistName(String artistName) {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT a.IdArtwork, a.Title, a.Price, a.Status, a.Type, a.IdArtist " +
                     "FROM Artwork a JOIN Artist ar ON a.IdArtist = ar.IdArtist WHERE ar.Name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, artistName);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Artwork artwork = mapResultSetToArtwork(rs);
                artworks.add(artwork);
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding artworks by artist: " + e.getMessage());
        }
        
        return artworks;
    }

    /**
     * Maps a ResultSet row to an Artwork object.
     */
    private Artwork mapResultSetToArtwork(ResultSet rs) throws SQLException {
        Artwork artwork = new Artwork();
        artwork.setId(rs.getInt("IdArtwork"));
        artwork.setTitle(rs.getString("Title"));
        artwork.setCreationYear(null); // Not stored in DB
        artwork.setType(rs.getString("Type"));
        artwork.setPrice(rs.getDouble("Price"));
        artwork.setStatus(Artwork.Status.valueOf(rs.getString("Status")));
        
        // Load artist
        int idArtist = rs.getInt("IdArtist");
        artwork.setArtist(loadArtistById(idArtist));
        
        return artwork;
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

    /**
     * Helper method to get the next available artwork ID.
     */
    private int getNextArtworkId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(IdArtwork) as maxId FROM Artwork";
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
