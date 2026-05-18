package com.project.artconnect.service;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Exhibition;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GalleryService {
    List<Gallery> getAllGalleries();

    Optional<Gallery> getGalleryByName(String name);

    List<Exhibition> getExhibitionsByGallery(Gallery gallery);
    
    void createGallery(Gallery gallery) throws SQLException;

    void updateGallery(Gallery gallery) throws SQLException;

    void deleteGallery(String name);
}
