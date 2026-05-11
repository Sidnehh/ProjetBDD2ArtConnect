package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.service.CommunityService;
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
}
