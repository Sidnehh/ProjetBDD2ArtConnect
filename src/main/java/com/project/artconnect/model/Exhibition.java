package com.project.artconnect.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Exhibition {
    private String title;
    private LocalDate startDate;
    private String description;
    private Gallery gallery;
    private String curatorName;
    private String theme;
    private List<Artwork> artworks = new ArrayList<>();

    public Exhibition() {
    }

    public Exhibition(String title, LocalDate startDate, Gallery gallery) {
        this.title = title;
        this.startDate = startDate;
        this.gallery = gallery;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Gallery getGallery() {
        return gallery;
    }

    public void setGallery(Gallery gallery) {
        this.gallery = gallery;
    }

    public String getCuratorName() {
        return curatorName;
    }

    public void setCuratorName(String curatorName) {
        this.curatorName = curatorName;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }

    public void setArtworks(List<Artwork> artworks) {
        this.artworks = artworks;
    }

    @Override
    public String toString() {
        return title;
    }
}
