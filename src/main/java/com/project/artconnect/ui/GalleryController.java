package com.project.artconnect.ui;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;

public class GalleryController {
    @FXML
    private TableView<Gallery> galleryTable;
    @FXML
    private TableColumn<Gallery, String> nameColumn;
    @FXML
    private TableColumn<Gallery, Double> ratingColumn;
    @FXML
    private TableColumn<Gallery, String> addressColumn;
    @FXML
    private TableColumn<Gallery, String> cityColumn;

    // Create fields
    @FXML
    private TextField newGalleryName;
    @FXML
    private TextField newGalleryStreet;
    @FXML
    private TextField newGalleryCity;
    @FXML
    private TextField newGalleryRating;

    // Edit fields
    @FXML
    private TextField editGalleryName;
    @FXML
    private TextField editGalleryStreet;
    @FXML
    private TextField editGalleryCity;
    @FXML
    private TextField editGalleryRating;

    @FXML
    private TextField searchField;

    private final GalleryService galleryService = ServiceProvider.getGalleryService();
    private Timeline autoRefreshTimeline;
    private Gallery selectedGallery;

    @FXML
    public void initialize() {
        // Setup columns in order: Name, Rating, Address, City
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("streetName"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        refreshTable();
    }

    @FXML
    private void handleAddGallery() {
        String name = newGalleryName.getText().trim();
        String street = newGalleryStreet.getText().trim();
        String city = newGalleryCity.getText().trim();
        String ratingStr = newGalleryRating.getText().trim();

        if (name.isEmpty() || street.isEmpty() || city.isEmpty() || ratingStr.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields: Name, Street, City, and Rating.");
            return;
        }

        try {
            double rating = Double.parseDouble(ratingStr);
            Gallery g = new Gallery(name, street, city, rating);
            try {
                galleryService.createGallery(g);
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to add gallery: " + re.getMessage());
                return;
            }
            newGalleryName.clear();
            newGalleryStreet.clear();
            newGalleryCity.clear();
            newGalleryRating.clear();

            showAlert("Success", "Gallery '" + name + "' added successfully!");
            refreshTable();

            if (RegistrationController.getInstance() != null) {
                RegistrationController.getInstance().reloadSelectors();
            }
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("A trigger has blocked the insertion!");
            alert.setContentText(errorMsg); 
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdateGallery() {
        if (selectedGallery == null) {
            showAlert("Error", "Please select a gallery from the table first.");
            return;
        }

        String name = editGalleryName.getText().trim();
        String street = editGalleryStreet.getText().trim();
        String city = editGalleryCity.getText().trim();
        String ratingStr = editGalleryRating.getText().trim();

        try {
            double rating = ratingStr.isEmpty() ? selectedGallery.getRating() : Double.parseDouble(ratingStr);
            if (rating < 0.0 || rating > 5.0) {
                showAlert("Validation Error", "Rating must be between 0 and 5.");
                return;
            }
            if (!name.isEmpty()) selectedGallery.setName(name);
            selectedGallery.setStreetName(street);
            selectedGallery.setCity(city);
            selectedGallery.setRating(rating);

            try {
                galleryService.updateGallery(selectedGallery);
            } catch (SQLException e) {
            String errorMsg = e.getMessage();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("A trigger has blocked the insertion!");
            alert.setContentText(errorMsg); 
            alert.showAndWait();
            }

            showAlert("Success", "Gallery '" + selectedGallery.getName() + "' updated successfully!");
            refreshTable();
            clearEditFields();

            RegistrationController.refreshSelectorsIfOpen();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Rating must be a valid number.");
        }
    }

    @FXML
    private void handleDeleteGallery() {
        if (selectedGallery == null) {
            showAlert("Error", "Please select a gallery from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete '" + selectedGallery.getName() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                galleryService.deleteGallery(selectedGallery.getName());
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to delete gallery: " + re.getMessage());
                return;
            }
            showAlert("Success", "Gallery '" + selectedGallery.getName() + "' deleted successfully!");
            refreshTable();
            clearEditFields();

            RegistrationController.refreshSelectorsIfOpen();
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedGallery = galleryTable.getSelectionModel().getSelectedItem();
        if (selectedGallery != null) {
            editGalleryName.setText(selectedGallery.getName());
            editGalleryStreet.setText(selectedGallery.getStreetName() != null ? selectedGallery.getStreetName() : "");
            editGalleryCity.setText(selectedGallery.getCity() != null ? selectedGallery.getCity() : "");
            editGalleryRating.setText(String.valueOf(selectedGallery.getRating()));
        }
    }

    private void clearEditFields() {
        editGalleryName.clear();
        editGalleryStreet.clear();
        editGalleryCity.clear();
        editGalleryRating.clear();
        selectedGallery = null;
        galleryTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        galleryTable.setItems(FXCollections.observableArrayList(galleryService.getAllGalleries()));
    }

    @FXML
    private void handleSearch() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            refreshTable();
            return;
        }
        String lower = q.toLowerCase();
        var filtered = galleryService.getAllGalleries().stream()
                .filter(g -> (g.getName() != null && g.getName().toLowerCase().contains(lower))
                        || (g.getStreetName() != null && g.getStreetName().toLowerCase().contains(lower))
                        || (g.getCity() != null && g.getCity().toLowerCase().contains(lower)))
                .toList();
        galleryTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        refreshTable();
    }

    private void startAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> refreshTable()));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
