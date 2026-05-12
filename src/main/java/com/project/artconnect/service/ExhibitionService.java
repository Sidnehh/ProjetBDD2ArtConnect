package com.project.artconnect.service;

import com.project.artconnect.model.Exhibition;
import java.util.List;

/**
 * Service interface for Exhibition operations.
 */
public interface ExhibitionService {
    List<Exhibition> getAllExhibitions();

    void save(Exhibition exhibition);

    void update(Exhibition exhibition);

    void delete(String title);

    // Legacy method names for backward compatibility
    void createExhibition(Exhibition exhibition);

    void updateExhibition(Exhibition exhibition);

    void deleteExhibition(String title);
}
