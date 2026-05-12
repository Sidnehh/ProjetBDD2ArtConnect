package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import java.time.LocalDate;
import java.util.List;

public class ExhibitionController {
    @FXML
    private TableView<Exhibition> exhibitionTable;
    @FXML
    private TableColumn<Exhibition, String> titleColumn;
    @FXML
    private TableColumn<Exhibition, LocalDate> dateColumn;
    @FXML
    private TableColumn<Exhibition, String> themeColumn;
    @FXML
    private TableColumn<Exhibition, String> galleryColumn;

    // Create fields
    @FXML
    private TextField newExhibitionTitle;
    @FXML
    private DatePicker newExhibitionStartDate;
    @FXML
    private DatePicker newExhibitionEndDate;
    @FXML
    private TextField newExhibitionTheme;
    @FXML
    private ComboBox<Gallery> newExhibitionGallery;

    // Edit fields
    @FXML
    private TextField editExhibitionTitle;
    @FXML
    private DatePicker editExhibitionStartDate;
    @FXML
    private DatePicker editExhibitionEndDate;
    @FXML
    private TextField editExhibitionTheme;

    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();
    private final GalleryService galleryService = ServiceProvider.getGalleryService();
    private Timeline autoRefreshTimeline;
    private Exhibition selectedExhibition;

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        galleryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getGallery() != null ? cellData.getValue().getGallery().getName() : "Unknown"));

        loadGalleries();
        refreshData();
        startAutoRefresh();
    }

    private void loadGalleries() {
        List<Gallery> galleries = galleryService.getAllGalleries();
        newExhibitionGallery.setItems(FXCollections.observableArrayList(galleries));
    }

    @FXML
    private void handleAddExhibition() {
        String title = newExhibitionTitle.getText().trim();
        LocalDate startDate = newExhibitionStartDate.getValue();
        LocalDate endDate = newExhibitionEndDate.getValue();
        String theme = newExhibitionTheme.getText().trim();
        Gallery gallery = newExhibitionGallery.getValue();

        if (title.isEmpty() || startDate == null || endDate == null) {
            showAlert("Validation Error", "Please fill in Title, Start Date, and End Date.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert("Validation Error", "Start Date must be before End Date.");
            return;
        }

        Exhibition exhibition = new Exhibition(title, startDate, endDate, gallery);
        exhibition.setTheme(theme);
        exhibitionService.save(exhibition);

        newExhibitionTitle.clear();
        newExhibitionStartDate.setValue(null);
        newExhibitionEndDate.setValue(null);
        newExhibitionTheme.clear();
        newExhibitionGallery.setValue(null);

        showAlert("Success", "Exhibition '" + title + "' added successfully!");
        refreshData();
    }

    @FXML
    private void handleUpdateExhibition() {
        if (selectedExhibition == null) {
            showAlert("Error", "Please select an exhibition from the table first.");
            return;
        }

        LocalDate startDate = editExhibitionStartDate.getValue();
        LocalDate endDate = editExhibitionEndDate.getValue();
        String theme = editExhibitionTheme.getText().trim();

        if (startDate == null || endDate == null) {
            showAlert("Validation Error", "Please fill in Start Date and End Date.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert("Validation Error", "Start Date must be before End Date.");
            return;
        }

        selectedExhibition.setStartDate(startDate);
        selectedExhibition.setEndDate(endDate);
        selectedExhibition.setTheme(theme);

        exhibitionService.update(selectedExhibition);
        showAlert("Success", "Exhibition '" + selectedExhibition.getTitle() + "' updated successfully!");
        refreshData();
        clearEditFields();
    }

    @FXML
    private void handleDeleteExhibition() {
        if (selectedExhibition == null) {
            showAlert("Error", "Please select an exhibition from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete '" + selectedExhibition.getTitle() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            exhibitionService.delete(selectedExhibition.getTitle());
            showAlert("Success", "Exhibition '" + selectedExhibition.getTitle() + "' deleted successfully!");
            refreshData();
            clearEditFields();
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedExhibition = exhibitionTable.getSelectionModel().getSelectedItem();
        if (selectedExhibition != null) {
            editExhibitionTitle.setText(selectedExhibition.getTitle());
            editExhibitionStartDate.setValue(selectedExhibition.getStartDate());
            editExhibitionEndDate.setValue(selectedExhibition.getEndDate());
            editExhibitionTheme.setText(selectedExhibition.getTheme() != null ? selectedExhibition.getTheme() : "");
        }
    }

    private void clearEditFields() {
        editExhibitionTitle.clear();
        editExhibitionStartDate.setValue(null);
        editExhibitionEndDate.setValue(null);
        editExhibitionTheme.clear();
        selectedExhibition = null;
        exhibitionTable.getSelectionModel().clearSelection();
    }

    private void refreshData() {
        List<Exhibition> all = exhibitionService.getAllExhibitions();
        exhibitionTable.setItems(FXCollections.observableArrayList(all));
        clearEditFields();
    }

    private void startAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> refreshData()));
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
