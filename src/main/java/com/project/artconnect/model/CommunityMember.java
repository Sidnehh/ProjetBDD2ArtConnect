package com.project.artconnect.model;

import java.util.ArrayList;
import java.util.List;

public class CommunityMember {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String city;
    private List<Discipline> favoriteDisciplines = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public CommunityMember() {
    }

    public CommunityMember(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public CommunityMember(String name, String email, String city) {
        this.name = name;
        this.email = email;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Discipline> getFavoriteDisciplines() {
        return favoriteDisciplines;
    }

    public void setFavoriteDisciplines(List<Discipline> favoriteDisciplines) {
        this.favoriteDisciplines = favoriteDisciplines;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
        if (booking.getMember() != this) {
            booking.setMember(this);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
