package com.project.artconnect.ui;

import com.project.artconnect.model.Artwork;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class ArtworkController {
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
    private TextField newArtworkType;
    @FXML
    private TextField newArtworkPrice;
    @FXML
    private ComboBox<String> newArtworkStatus;

    // Edit fields
    @FXML
    private TextField editArtworkTitle;
    @FXML
    private TextField editArtworkType;
    @FXML
    private TextField editArtworkPrice;
    @FXML
    private ComboBox<String> editArtworkStatus;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();
    private Timeline autoRefreshTimeline;
    private Artwork selectedArtwork;

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getArtist() != null ? cellData.getValue().getArtist().getName() : "Unknown"));

        // Populate status ComboBox items
        newArtworkStatus.setItems(FXCollections.observableArrayList("FOR_SALE", "SOLD", "EXHIBITED"));
        editArtworkStatus.setItems(FXCollections.observableArrayList("FOR_SALE", "SOLD", "EXHIBITED"));

        refreshTable();
        startAutoRefresh();
    }

    @FXML
    private void handleAddArtwork() {
        String title = newArtworkTitle.getText().trim();
        String type = newArtworkType.getText().trim();
        String priceStr = newArtworkPrice.getText().trim();
        String status = newArtworkStatus.getValue();

        if (title.isEmpty() || type.isEmpty()) {
            showAlert("Validation Error", "Please fill in Title and Type.");
            return;
        }

        try {
            double price = priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);
            Artwork artwork = new Artwork(title, null, type, price, null);
            artwork.setStatus(status != null ? Artwork.Status.valueOf(status) : Artwork.Status.FOR_SALE);

            artworkService.createArtwork(artwork);

            newArtworkTitle.clear();
            newArtworkType.clear();
            newArtworkPrice.clear();
            newArtworkStatus.setValue(null);

            showAlert("Success", "Artwork '" + title + "' added successfully!");
            refreshTable();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price must be a valid number.");
        }
    }

    @FXML
    private void handleUpdateArtwork() {
        if (selectedArtwork == null) {
            showAlert("Error", "Please select an artwork from the table first.");
            return;
        }

        String type = editArtworkType.getText().trim();
        String priceStr = editArtworkPrice.getText().trim();
        String status = editArtworkStatus.getValue();

        try {
            double price = priceStr.isEmpty() ? selectedArtwork.getPrice() : Double.parseDouble(priceStr);
            selectedArtwork.setType(type);
            selectedArtwork.setPrice(price);
            selectedArtwork.setStatus(status != null ? Artwork.Status.valueOf(status) : Artwork.Status.FOR_SALE);

            artworkService.updateArtwork(selectedArtwork);
            showAlert("Success", "Artwork '" + selectedArtwork.getTitle() + "' updated successfully!");
            refreshTable();
            clearEditFields();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price must be a valid number.");
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
            editArtworkType.setText(selectedArtwork.getType() != null ? selectedArtwork.getType() : "");
            editArtworkPrice.setText(String.valueOf(selectedArtwork.getPrice()));
            editArtworkStatus.setValue(selectedArtwork.getStatus() != null ? selectedArtwork.getStatus().toString() : "FOR_SALE");
        }
    }

    private void clearEditFields() {
        editArtworkTitle.clear();
        editArtworkType.clear();
        editArtworkPrice.clear();
        editArtworkStatus.setValue(null);
        selectedArtwork = null;
        artworkTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
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
