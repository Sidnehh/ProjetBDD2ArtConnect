package com.project.artconnect.ui;

import com.project.artconnect.model.Artwork;

import java.sql.SQLException;

import com.project.artconnect.model.Artist;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class ArtworkController {
    private static ArtworkController instance;

    public static void refreshArtistSelectorsIfOpen() {
        if (instance != null) {
            instance.setupArtistComboBox(instance.newArtworkArtist);
            instance.setupArtistComboBox(instance.editArtworkArtist);
        }
    }

    @FXML
    private TableView<Artwork> artworkTable;
    @FXML
    private TableColumn<Artwork, String> titleColumn;
    @FXML
    private TableColumn<Artwork, String> typeColumn;
    @FXML
    private TableColumn<Artwork, Double> priceColumn;
    @FXML
    private TableColumn<Artwork, String> statusColumn;
    @FXML
    private TableColumn<Artwork, String> artistColumn;

    // Create fields
    @FXML
    private TextField newArtworkTitle;
    @FXML
    private ComboBox<String> newArtworkType;
    @FXML
    private TextField newArtworkPrice;
    @FXML
    private ComboBox<String> newArtworkStatus;
    @FXML
    private ComboBox<Artist> newArtworkArtist;

    // Edit fields
    @FXML
    private TextField editArtworkTitle;
    @FXML
    private ComboBox<String> editArtworkType;
    @FXML
    private TextField editArtworkPrice;
    @FXML
    private ComboBox<String> editArtworkStatus;
    @FXML
    private ComboBox<Artist> editArtworkArtist;

    @FXML
    private TextField searchField;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();
    private final ArtistService artistService = ServiceProvider.getArtistService();
    private Timeline autoRefreshTimeline;
    private Artwork selectedArtwork;

    @FXML
    public void initialize() {
        instance = this;
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getArtist() != null ? cellData.getValue().getArtist().getName() : "Unknown"));

        // Setup type dropdown with 8 predefined artwork types
        ObservableList<String> types = FXCollections.observableArrayList(
            "Painting", "Sculpture", "Photography", "Mixed Media",
            "Printmaking", "Digital Art", "Watercolor", "Installation"
        );
        newArtworkType.setItems(types);
        editArtworkType.setItems(types);

        // Setup status dropdown (only FOR_SALE and SOLD, no EXHIBITED)
        newArtworkStatus.setItems(FXCollections.observableArrayList("FOR_SALE", "SOLD"));
        editArtworkStatus.setItems(FXCollections.observableArrayList("FOR_SALE", "SOLD"));

        // Setup artist dropdowns
        setupArtistComboBox(newArtworkArtist);
        setupArtistComboBox(editArtworkArtist);

        refreshTable();
    }

    @FXML
    private void handleSearch() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            refreshTable();
            return;
        }
        String lower = q.toLowerCase();
        var filtered = artworkService.getAllArtworks().stream()
                .filter(a -> a.getTitle() != null && a.getTitle().toLowerCase().contains(lower)
                        || (a.getArtist() != null && a.getArtist().getName() != null && a.getArtist().getName().toLowerCase().contains(lower))
                        || (a.getType() != null && a.getType().toLowerCase().contains(lower)))
                .toList();
        artworkTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        refreshTable();
    }

    private void setupArtistComboBox(ComboBox<Artist> comboBox) {
        var artists = FXCollections.observableArrayList(artistService.getAllArtists());
        comboBox.setItems(artists);
        comboBox.setConverter(new StringConverter<Artist>() {
            @Override
            public String toString(Artist artist) {
                return artist != null ? artist.getName() : "";
            }
            @Override
            public Artist fromString(String string) {
                return artists.stream().filter(a -> a.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }

    @FXML
    private void handleAddArtwork() throws SQLException {
        String title = newArtworkTitle.getText().trim();
        String type = newArtworkType.getValue();
        String priceStr = newArtworkPrice.getText().trim();
        String status = newArtworkStatus.getValue();
        Artist artist = newArtworkArtist.getValue();

        if (title.isEmpty() || type == null || status == null || artist == null || priceStr.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields: Title, Type, Artist, Price, and Status.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            Artwork artwork = new Artwork(title, null, type, price, artist);
            artwork.setStatus(status != null ? Artwork.Status.valueOf(status) : Artwork.Status.FOR_SALE);

            artworkService.createArtwork(artwork);

            newArtworkTitle.clear();
            newArtworkType.setValue(null);
            newArtworkPrice.clear();
            newArtworkStatus.setValue(null);
            newArtworkArtist.setValue(null);

            showAlert("Success", "Artwork '" + title + "' added successfully!");
            refreshTable();

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
    private void handleUpdateArtwork() {
        if (selectedArtwork == null) {
            showAlert("Error", "Please select an artwork from the table first.");
            return;
        }

        String title = editArtworkTitle.getText().trim();
        String type = editArtworkType.getValue();
        String priceStr = editArtworkPrice.getText().trim();
        String status = editArtworkStatus.getValue();
        Artist artist = editArtworkArtist.getValue();

        if (title.isEmpty() || type == null || status == null || artist == null) {
            showAlert("Validation Error", "Please fill in all fields including selecting an artist.");
            return;
        }

        try {
            double price = priceStr.isEmpty() ? selectedArtwork.getPrice() : Double.parseDouble(priceStr);
            selectedArtwork.setTitle(title);
            selectedArtwork.setType(type);
            selectedArtwork.setPrice(price);
            selectedArtwork.setStatus(status != null ? Artwork.Status.valueOf(status) : Artwork.Status.FOR_SALE);
            selectedArtwork.setArtist(artist);

            artworkService.updateArtwork(selectedArtwork);
            showAlert("Success", "Artwork '" + selectedArtwork.getTitle() + "' updated successfully!");
            refreshTable();
            clearEditFields();
            
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
    private void handleDeleteArtwork() {
        if (selectedArtwork == null) {
            showAlert("Error", "Please select an artwork from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete '" + selectedArtwork.getTitle() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            artworkService.deleteArtwork(selectedArtwork.getTitle());
            showAlert("Success", "Artwork '" + selectedArtwork.getTitle() + "' deleted successfully!");
            refreshTable();
            clearEditFields();
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedArtwork = artworkTable.getSelectionModel().getSelectedItem();
        if (selectedArtwork != null) {
            editArtworkTitle.setText(selectedArtwork.getTitle());
            editArtworkType.setValue(selectedArtwork.getType() != null ? selectedArtwork.getType() : "");
            editArtworkPrice.setText(String.valueOf(selectedArtwork.getPrice()));
            editArtworkStatus.setValue(selectedArtwork.getStatus() != null ? selectedArtwork.getStatus().toString() : "FOR_SALE");
            editArtworkArtist.setValue(selectedArtwork.getArtist());
        }
    }

    private void clearEditFields() {
        editArtworkTitle.clear();
        editArtworkType.setValue(null);
        editArtworkPrice.clear();
        editArtworkStatus.setValue(null);
        editArtworkArtist.setValue(null);
        selectedArtwork = null;
        artworkTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        setupArtistComboBox(newArtworkArtist);
        setupArtistComboBox(editArtworkArtist);
        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
        clearEditFields();
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
