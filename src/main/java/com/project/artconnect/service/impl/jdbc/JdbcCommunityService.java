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
        // TODO: Implement review retrieval from database
        return List.of();
    }

    @Override
    public void createMember(CommunityMember member) {
        String sql = "INSERT INTO CommunityMember (Name, Email, BirthYear, City, MembershipType) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setInt(3, member.getBirthYear());
            stmt.setString(4, member.getCity());
            stmt.setString(5, member.getMembershipType() != null ? member.getMembershipType() : "REGULAR");
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating community member: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateMember(CommunityMember member) {
        String sql = "UPDATE CommunityMember SET Email = ?, BirthYear = ?, City = ?, MembershipType = ? WHERE IdMember = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getEmail());
            stmt.setInt(2, member.getBirthYear());
            stmt.setString(3, member.getCity());
            stmt.setString(4, member.getMembershipType() != null ? member.getMembershipType() : "REGULAR");
            stmt.setInt(5, member.getId());
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
