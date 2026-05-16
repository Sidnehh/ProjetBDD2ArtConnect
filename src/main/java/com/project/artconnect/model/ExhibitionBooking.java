package com.project.artconnect.model;

import java.time.LocalDateTime;

public class ExhibitionBooking {
    private Exhibition exhibition;
    private CommunityMember member;
    private LocalDateTime registrationDate;

    public ExhibitionBooking() {
    }

    public ExhibitionBooking(Exhibition exhibition, CommunityMember member) {
        this.exhibition = exhibition;
        this.member = member;
        this.registrationDate = LocalDateTime.now();
    }

    public Exhibition getExhibition() {
        return exhibition;
    }

    public void setExhibition(Exhibition exhibition) {
        this.exhibition = exhibition;
    }

    public CommunityMember getMember() {
        return member;
    }

    public void setMember(CommunityMember member) {
        this.member = member;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
