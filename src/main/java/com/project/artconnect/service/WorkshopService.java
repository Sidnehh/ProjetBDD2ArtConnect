package com.project.artconnect.service;

import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface WorkshopService {
    List<Workshop> getAllWorkshops();

    Optional<Workshop> getWorkshopByTitle(String title);

    void bookWorkshop(Workshop workshop, CommunityMember member);

    List<Booking> getBookingsByMember(CommunityMember member);

    void save(Workshop workshop) throws SQLException;

    void update(Workshop workshop) throws SQLException;

    void delete(String title);

    default void deleteWorkshopById(int workshopId) {
        throw new UnsupportedOperationException("deleteWorkshopById is not implemented");
    }
}
