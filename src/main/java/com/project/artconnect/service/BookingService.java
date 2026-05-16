package com.project.artconnect.service;

import com.project.artconnect.model.Booking;
import com.project.artconnect.model.ExhibitionBooking;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Exhibition;

import java.util.List;

public interface BookingService {
    void registerToWorkshop(int memberId, int workshopId);
    void unregisterFromWorkshop(int memberId, int workshopId);
    List<Booking> getMemberWorkshopBookings(int memberId);
    List<Workshop> getAvailableWorkshopsForMember(int memberId);
    
    void registerToExhibition(int memberId, int exhibitionId);
    void unregisterFromExhibition(int memberId, int exhibitionId);
    List<ExhibitionBooking> getMemberExhibitionBookings(int memberId);
    List<Exhibition> getAvailableExhibitionsForMember(int memberId);
}
