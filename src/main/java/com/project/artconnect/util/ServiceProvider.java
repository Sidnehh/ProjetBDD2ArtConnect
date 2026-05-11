package com.project.artconnect.util;

import com.project.artconnect.dao.*;
import com.project.artconnect.persistence.*;
import com.project.artconnect.service.*;
import com.project.artconnect.service.impl.jdbc.*;
import com.project.artconnect.service.impl.*;

/**
 * Service Provider to manage singleton instances of services and handle their
 * initialization.
 * 
 * Currently using JDBC-based implementations for persistent storage.
 * InMemory implementations are commented out below for future testing use.
 */
public class ServiceProvider {

    // ============================================================
    // JDBC DAOs (persistent storage)
    // ============================================================
    private static final ArtistDao artistDao = new JdbcArtistDao();
    private static final ArtworkDao artworkDao = new JdbcArtworkDao(artistDao);
    private static final ExhibitionDao exhibitionDao = new JdbcExhibitionDao();
    private static final GalleryDao galleryDao = new JdbcGalleryDao();
    private static final WorkshopDao workshopDao = new JdbcWorkshopDao(artistDao);
    private static final CommunityMemberDao communityMemberDao = new JdbcCommunityMemberDao();

    // ============================================================
    // JDBC Services (using JDBC DAOs)
    // ============================================================
    private static final ArtistService artistService = new JdbcArtistService(artistDao);
    private static final ArtworkService artworkService = new JdbcArtworkService(artworkDao, artistDao);
    private static final GalleryService galleryService = new JdbcGalleryService(galleryDao, exhibitionDao);
    private static final WorkshopService workshopService = new JdbcWorkshopService(workshopDao);
    private static final CommunityService communityService = new JdbcCommunityService(communityMemberDao);
    private static final ExhibitionService exhibitionService = new JdbcExhibitionService(exhibitionDao);

    // ============================================================
    // InMemory Services (commented out - available for testing)
    // ============================================================
    /*
    private static final InMemoryArtistService inMemoryArtistService = new InMemoryArtistService();
    private static final InMemoryArtworkService inMemoryArtworkService = new InMemoryArtworkService();
    private static final InMemoryGalleryService inMemoryGalleryService = new InMemoryGalleryService();
    private static final InMemoryWorkshopService inMemoryWorkshopService = new InMemoryWorkshopService();
    private static final InMemoryCommunityService inMemoryCommunityService = new InMemoryCommunityService();

    static {
        // Initialize InMemory services with their dependencies
        inMemoryArtworkService.initData(inMemoryArtistService);
        inMemoryGalleryService.initData(inMemoryArtworkService);
        inMemoryWorkshopService.initData(inMemoryArtistService);
        inMemoryCommunityService.initData(inMemoryArtworkService);
    }
    */

    // ============================================================
    // Service Getters (returns JDBC implementations)
    // ============================================================
    public static ArtistService getArtistService() {
        return artistService;
    }

    public static ArtworkService getArtworkService() {
        return artworkService;
    }

    public static GalleryService getGalleryService() {
        return galleryService;
    }

    public static WorkshopService getWorkshopService() {
        return workshopService;
    }

    public static CommunityService getCommunityService() {
        return communityService;
    }

    public static ExhibitionService getExhibitionService() {
        return exhibitionService;
    }

    // ============================================================
    // InMemory Service Getters (commented out)
    // ============================================================
    /*
    public static ArtistService getInMemoryArtistService() {
        return inMemoryArtistService;
    }

    public static ArtworkService getInMemoryArtworkService() {
        return inMemoryArtworkService;
    }

    public static GalleryService getInMemoryGalleryService() {
        return inMemoryGalleryService;
    }

    public static WorkshopService getInMemoryWorkshopService() {
        return inMemoryWorkshopService;
    }

    public static CommunityService getInMemoryCommunityService() {
        return inMemoryCommunityService;
    }
    */
}
