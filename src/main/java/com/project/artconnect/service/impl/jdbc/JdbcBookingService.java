package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.model.*;
import com.project.artconnect.service.BookingService;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcBookingService implements BookingService {

    @Override
    public void registerToWorkshop(int memberId, int workshopId) {
        String sql = "{CALL sp_Register_Member_To_Workshop(?, ?, ?)}";
        try (Connection conn = ConnectionManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, workshopId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();
            String message = stmt.getString(3);
            if (message != null && message.toLowerCase().contains("error")) {
                throw new RuntimeException(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error registering to workshop: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregisterFromWorkshop(int memberId, int workshopId) {
        String sql = "{CALL sp_Unregister_Member_To_Workshop(?, ?, ?)}";
        try (Connection conn = ConnectionManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, workshopId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();
            String message = stmt.getString(3);
            if (message != null && message.toLowerCase().contains("error")) {
                throw new RuntimeException(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error unregistering from workshop: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Booking> getMemberWorkshopBookings(int memberId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT w.IdWorkshop, w.Title, w.Date_, w.Price, w.Level, " +
                     "a.IdArtist, a.Email, a.Name, a.City, a.BirthYear " +
                     "FROM RegisterWorkshop rw " +
                     "JOIN Workshop w ON rw.IdWorkshop = w.IdWorkshop " +
                     "JOIN Artist a ON w.IdArtist = a.IdArtist " +
                     "WHERE rw.IdMember = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Workshop workshop = mapResultSetToWorkshop(rs);
                Booking booking = new Booking();
                booking.setWorkshop(workshop);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving workshop bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    @Override
    public List<Workshop> getAvailableWorkshopsForMember(int memberId) {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT w.IdWorkshop, w.Title, w.Date_, w.Price, w.Level, " +
                     "a.IdArtist, a.Email, a.Name, a.City, a.BirthYear " +
                     "FROM Workshop w " +
                     "JOIN Artist a ON w.IdArtist = a.IdArtist " +
                     "WHERE w.IdWorkshop NOT IN (SELECT IdWorkshop FROM RegisterWorkshop WHERE IdMember = ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                workshops.add(mapResultSetToWorkshop(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving available workshops: " + e.getMessage(), e);
        }
        return workshops;
    }

    @Override
    public void registerToExhibition(int memberId, int exhibitionId) {
        String sql = "{CALL sp_Register_Member_To_Exhibition(?, ?, ?)}";
        try (Connection conn = ConnectionManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, exhibitionId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();
            String message = stmt.getString(3);
            if (message != null && message.toLowerCase().contains("error")) {
                throw new RuntimeException(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error registering to exhibition: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregisterFromExhibition(int memberId, int exhibitionId) {
        String sql = "{CALL sp_Unregister_Member_To_Exhibition(?, ?, ?)}";
        try (Connection conn = ConnectionManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, exhibitionId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();
            String message = stmt.getString(3);
            if (message != null && message.toLowerCase().contains("error")) {
                throw new RuntimeException(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error unregistering from exhibition: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ExhibitionBooking> getMemberExhibitionBookings(int memberId) {
        List<ExhibitionBooking> bookings = new ArrayList<>();
        String sql = "SELECT e.IdExhibition, e.Title, e.StartDate, e.Theme, " +
                     "g.IdGallery, g.Name, g.StreetName, g.City, g.Rating " +
                     "FROM RegisterExhibition re " +
                     "JOIN Exhibition e ON re.IdExhibition = e.IdExhibition " +
                     "JOIN Gallery g ON e.IdGallery = g.IdGallery " +
                     "WHERE re.IdMember = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Exhibition exhibition = mapResultSetToExhibition(rs);
                ExhibitionBooking booking = new ExhibitionBooking();
                booking.setExhibition(exhibition);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving exhibition bookings: " + e.getMessage(), e);
        }
        return bookings;
    }

    @Override
    public List<Exhibition> getAvailableExhibitionsForMember(int memberId) {
        List<Exhibition> exhibitions = new ArrayList<>();
        String sql = "SELECT e.IdExhibition, e.Title, e.StartDate, e.Theme, " +
                     "g.IdGallery, g.Name, g.StreetName, g.City, g.Rating " +
                     "FROM Exhibition e " +
                     "JOIN Gallery g ON e.IdGallery = g.IdGallery " +
                     "WHERE e.IdExhibition NOT IN (SELECT IdExhibition FROM RegisterExhibition WHERE IdMember = ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                exhibitions.add(mapResultSetToExhibition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving available exhibitions: " + e.getMessage(), e);
        }
        return exhibitions;
    }

    private Workshop mapResultSetToWorkshop(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();
        workshop.setId(rs.getInt("IdWorkshop"));
        workshop.setTitle(rs.getString("Title"));
        workshop.setDate(rs.getTimestamp("Date_") != null ? rs.getTimestamp("Date_").toLocalDateTime() : null);
        workshop.setPrice(rs.getDouble("Price"));
        workshop.setLevel(rs.getString("Level"));

        // Map artist
        int artistId = rs.getInt("IdArtist");
        if (artistId > 0) {
            Artist artist = new Artist();
            artist.setId(artistId);
            artist.setName(rs.getString("Name"));
            workshop.setInstructor(artist);
        }

        return workshop;
    }

    private Exhibition mapResultSetToExhibition(ResultSet rs) throws SQLException {
        Exhibition exhibition = new Exhibition();
        exhibition.setId(rs.getInt("IdExhibition"));
        exhibition.setTitle(rs.getString("Title"));
        exhibition.setStartDate(rs.getDate("StartDate") != null ? rs.getDate("StartDate").toLocalDate() : null);
        exhibition.setTheme(rs.getString("Theme"));

        // Map gallery
        int galleryId = rs.getInt("IdGallery");
        if (galleryId > 0) {
            Gallery gallery = new Gallery();
            gallery.setId(galleryId);
            gallery.setName(rs.getString("Name"));
            gallery.setStreetName(rs.getString("StreetName"));
            gallery.setCity(rs.getString("City"));
            gallery.setRating(rs.getDouble("Rating"));
            exhibition.setGallery(gallery);
        }

        return exhibition;
    }
}
