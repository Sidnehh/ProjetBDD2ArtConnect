package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiscoverController {
    @FXML
    private FlowPane discoverPane;

    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();
    private final GalleryService galleryService = ServiceProvider.getGalleryService();
    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();

    @FXML
    public void initialize() {
        discoverPane.getChildren().clear();

        List<Exhibition> upcomingExhibitions = exhibitionService.getAllExhibitions().stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getStartDate() == null || !e.getStartDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Exhibition::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        List<Workshop> upcomingWorkshops = workshopService.getAllWorkshops().stream()
                .filter(Objects::nonNull)
                .filter(w -> w.getDate() == null || !w.getDate().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Workshop::getDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        if (upcomingExhibitions.isEmpty() && upcomingWorkshops.isEmpty()) {
            discoverPane.getChildren().add(new Label("No upcoming events available."));
            return;
        }

        upcomingExhibitions.forEach(this::addExhibitionCard);
        upcomingWorkshops.forEach(this::addWorkshopCard);
    }

    private void addExhibitionCard(Exhibition e) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: #e3f2fd; -fx-border-color: #2196f3; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(250);
        card.getChildren().addAll(
                new Label("FEATURED EXHIBITION"),
                new Label(e.getTitle()) {
                    {
                        setStyle("-fx-font-weight: bold;");
                    }
                },
                new Label("Date: " + e.getStartDate()),
                new Label("Theme: " + e.getTheme()),
                new Label("Gallery: " + (e.getGallery() != null ? e.getGallery().getName() : "Unknown")));
        discoverPane.getChildren().add(card);
    }

    private void addWorkshopCard(Workshop w) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: #f1f8e9; -fx-border-color: #4caf50; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(250);
        card.getChildren().addAll(
                new Label("UPCOMING WORKSHOP"),
                new Label(w.getTitle()) {
                    {
                        setStyle("-fx-font-weight: bold;");
                    }
                },
                new Label("Date: " + w.getDate()),
                new Label("Instructor: " + (w.getInstructor() != null ? w.getInstructor().getName() : "Unknown")),
                new Label("Price: $" + w.getPrice()));
        discoverPane.getChildren().add(card);
    }
}
