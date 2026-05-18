package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CommunityService.
 */
public class JdbcCommunityService implements CommunityService {
    private final CommunityMemberDao communityMemberDao;

    public JdbcCommunityService(CommunityMemberDao communityMemberDao) {
        this.communityMemberDao = communityMemberDao;
    }

    @Override
    public List<CommunityMember> getAllMembers() {
        return communityMemberDao.findAll();
    }

    @Override
    public Optional<CommunityMember> getMemberByName(String name) {
        List<CommunityMember> members = communityMemberDao.findAll();
        return members.stream()
                .filter(m -> m.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Review> getReviewsByMember(CommunityMember member) {
        return List.of();
    }

    @Override
    public void createMember(CommunityMember member) {
        String sql = "INSERT INTO CommunityMember (IdMember, Name, Email, City) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int nextId = getNextMemberId(conn);
            stmt.setInt(1, nextId);
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getCity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating community member: " + e.getMessage(), e);
        }
    }

    private int getNextMemberId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(IdMember) as maxId FROM CommunityMember";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("maxId") + 1;
            }
        }
        return 1;
    }

    @Override
    public void updateMember(CommunityMember member) {
        String sql = "UPDATE CommunityMember SET Name = ?, Email = ?, City = ? WHERE IdMember = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getCity());
            stmt.setInt(4, member.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating community member: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMember(int id) {
        String sql = "DELETE FROM CommunityMember WHERE IdMember = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting community member: " + e.getMessage(), e);
        }
    }
}
