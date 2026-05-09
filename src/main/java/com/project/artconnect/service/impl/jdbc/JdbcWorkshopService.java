package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.service.WorkshopService;
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
}
