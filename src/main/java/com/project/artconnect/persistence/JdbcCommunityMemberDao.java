package com.project.artconnect.persistence;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation for CommunityMemberDao.
 */
public class JdbcCommunityMemberDao implements CommunityMemberDao {

    @Override
    public Optional<CommunityMember> findById(Long id) {
        String sql = "SELECT IdMember, Name, Email, City FROM CommunityMember WHERE IdMember = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                CommunityMember member = mapResultSetToCommunityMember(rs);
                rs.close();
                return Optional.of(member);
            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding community member by id: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    @Override
    public List<CommunityMember> findAll() {
        List<CommunityMember> members = new ArrayList<>();
        String sql = "SELECT IdMember, Name, Email, City FROM CommunityMember";
        
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                CommunityMember member = mapResultSetToCommunityMember(rs);
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all community members: " + e.getMessage());
        }
        
        return members;
    }

    /**
     * Maps a ResultSet row to a CommunityMember object.
     */
    private CommunityMember mapResultSetToCommunityMember(ResultSet rs) throws SQLException {
        CommunityMember member = new CommunityMember();
        member.setId(rs.getInt("IdMember"));
        member.setName(rs.getString("Name"));
        member.setEmail(rs.getString("Email"));
        member.setCity(rs.getString("City"));
        return member;
    }
}
