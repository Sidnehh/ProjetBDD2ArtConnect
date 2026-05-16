package com.project.artconnect.model;

import java.time.LocalDateTime;

public class WorkshopBooking {
    private Workshop workshop;
    private CommunityMember member;

    public WorkshopBooking() {
    }

    public WorkshopBooking(Workshop workshop, CommunityMember member) {
        this.workshop = workshop;
        this.member = member;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public CommunityMember getMember() {
        return member;
    }

    public void setMember(CommunityMember member) {
        this.member = member;
    }
}
