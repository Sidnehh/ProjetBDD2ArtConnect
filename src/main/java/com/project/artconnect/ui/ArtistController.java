package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class ArtistController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Artist> artistTable;
    @FXML
    private TableColumn<Artist, String> nameColumn;
    @FXML
    private TableColumn<Artist, String> cityColumn;
    @FXML
    private TableColumn<Artist, String> emailColumn;
    @FXML
    private TableColumn<Artist, Integer> yearColumn;

    // Create fields
    @FXML
    private TextField newArtistName;
    @FXML
    private TextField newArtistEmail;
    @FXML
    private TextField newArtistCity;
    @FXML
    private TextField newArtistYear;

    // Edit fields
    @FXML
    private TextField editArtistName;
    @FXML
    private TextField editArtistEmail;
    @FXML
    private TextField editArtistCity;
    @FXML
    private TextField editArtistYear;

    private final ArtistService artistService = ServiceProvider.getArtistService();
    private Timeline autoRefreshTimeline;
    private Artist selectedArtist;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("birthYear"));

        refreshTable();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        artistTable.setItems(FXCollections.observableArrayList(artistService.searchArtists(query, null, null)));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        refreshTable();
    }

    @FXML
    private void handleAddArtist() {
        String name = newArtistName.getText().trim();
        String email = newArtistEmail.getText().trim();
        String city = newArtistCity.getText().trim();
        String yearStr = newArtistYear.getText().trim();

        if (name.isEmpty() || email.isEmpty() || city.isEmpty() || yearStr.isEmpty()) {
            showAlert("Validation Error", "All fields are required: Name, Email, City, and Birth Year.");
            return;
        }

        try {
            Integer year = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);
            Artist artist = new Artist(name, "", year, email, city);
            try {
                artistService.createArtist(artist);
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to add artist: " + re.getMessage());
                return;
            }

            // Clear form
            newArtistName.clear();
            newArtistEmail.clear();
            newArtistCity.clear();
            newArtistYear.clear();

            showAlert("Success", "Artist '" + name + "' added successfully!");
            refreshTable();
            ArtworkController.refreshArtistSelectorsIfOpen();
            WorkshopController.refreshArtistSelectorsIfOpen();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Birth Year must be a valid number.");
        }
    }

    @FXML
    private void handleUpdateArtist() {
        if (selectedArtist == null) {
            showAlert("Error", "Please select an artist from the table first.");
            return;
        }

        String name = editArtistName.getText().trim();
        String email = editArtistEmail.getText().trim();
        String city = editArtistCity.getText().trim();
        String yearStr = editArtistYear.getText().trim();

        try {
            Integer year = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);
            if (!name.isEmpty()) {
                selectedArtist.setName(name);
            }
            selectedArtist.setContactEmail(email);
            selectedArtist.setCity(city);
            selectedArtist.setBirthYear(year);

            try {
                artistService.updateArtist(selectedArtist);
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to update artist: " + re.getMessage());
                return;
            }

            showAlert("Success", "Artist '" + selectedArtist.getName() + "' updated successfully!");
            refreshTable();
            clearEditFields();
            ArtworkController.refreshArtistSelectorsIfOpen();
            WorkshopController.refreshArtistSelectorsIfOpen();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Birth Year must be a valid number.");
        }
    }

    @FXML
    private void handleDeleteArtist() {
        if (selectedArtist == null) {
            showAlert("Error", "Please select an artist from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete '" + selectedArtist.getName() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                artistService.deleteArtist(selectedArtist.getName());
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to delete artist: " + re.getMessage());
                return;
            }
            showAlert("Success", "Artist '" + selectedArtist.getName() + "' deleted successfully!");
            refreshTable();
            clearEditFields();
            ArtworkController.refreshArtistSelectorsIfOpen();
            WorkshopController.refreshArtistSelectorsIfOpen();
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedArtist = artistTable.getSelectionModel().getSelectedItem();
        if (selectedArtist != null) {
            editArtistName.setText(selectedArtist.getName());
            editArtistEmail.setText(selectedArtist.getContactEmail() != null ? selectedArtist.getContactEmail() : "");
            editArtistCity.setText(selectedArtist.getCity() != null ? selectedArtist.getCity() : "");
            editArtistYear.setText(selectedArtist.getBirthYear() != null ? selectedArtist.getBirthYear().toString() : "");
        }
    }

    private void clearEditFields() {
        editArtistName.clear();
        editArtistEmail.clear();
        editArtistCity.clear();
        editArtistYear.clear();
        selectedArtist = null;
        artistTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        artistTable.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
        clearEditFields();
    }

    private void startAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            String query = searchField.getText();

            if (query != null && !query.isBlank()) {
                handleSearch();
            } else {
                refreshTable();
            }
        }));
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
