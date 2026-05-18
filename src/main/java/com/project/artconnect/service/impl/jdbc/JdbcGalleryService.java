package com.project.artconnect.service.impl.jdbc;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.GalleryService;

import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JDBC implementation of GalleryService.
 */
public class JdbcGalleryService implements GalleryService {
    private final GalleryDao galleryDao;
    private final ExhibitionDao exhibitionDao;

    public JdbcGalleryService(GalleryDao galleryDao, ExhibitionDao exhibitionDao) {
        this.galleryDao = galleryDao;
        this.exhibitionDao = exhibitionDao;
    }

    @Override
    public List<Gallery> getAllGalleries() {
        return galleryDao.findAll();
    }

    @Override
    public Optional<Gallery> getGalleryByName(String name) {
        List<Gallery> galleries = galleryDao.findAll();
        return galleries.stream()
                .filter(g -> g.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Exhibition> getExhibitionsByGallery(Gallery gallery) {
        List<Exhibition> allExhibitions = exhibitionDao.findAll();
        return allExhibitions.stream()
                .filter(e -> e.getGallery() != null && e.getGallery().getName().equals(gallery.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void createGallery(Gallery gallery) throws SQLException{
        galleryDao.save(gallery);
    }

    @Override
    public void updateGallery(Gallery gallery) throws SQLException{
        galleryDao.update(gallery);
    }

    @Override
    public void deleteGallery(String name) {
        galleryDao.delete(name);
    }
}
