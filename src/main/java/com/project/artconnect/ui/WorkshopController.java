package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class WorkshopController {
    @FXML
    private TableView<Workshop> workshopTable;
    @FXML
    private TableColumn<Workshop, String> titleColumn;
    @FXML
    private TableColumn<Workshop, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Workshop, String> instructorColumn;
    @FXML
    private TableColumn<Workshop, Double> priceColumn;
    @FXML
    private TableColumn<Workshop, String> levelColumn;

    // Create fields
    @FXML
    private TextField newWorkshopTitle;
    @FXML
    private DatePicker newWorkshopDate;
    @FXML
    private ComboBox<Artist> newWorkshopInstructor;
    @FXML
    private TextField newWorkshopPrice;
    @FXML
    private ComboBox<String> newWorkshopLevel;

    // Edit fields
    @FXML
    private TextField editWorkshopTitle;
    @FXML
    private DatePicker editWorkshopDate;
    @FXML
    private TextField editWorkshopPrice;
    @FXML
    private ComboBox<String> editWorkshopLevel;

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final ArtistService artistService = ServiceProvider.getArtistService();
    private Timeline autoRefreshTimeline;
    private Workshop selectedWorkshop;

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));

        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getInstructor() != null ? cellData.getValue().getInstructor().getName()
                        : "Unknown"));

        loadInstructors();
        // Populate level ComboBox items with proper capitalization
        newWorkshopLevel.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        editWorkshopLevel.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        
        refreshTable();
        startAutoRefresh();
    }

    private void loadInstructors() {
        newWorkshopInstructor.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
    }

    @FXML
    private void handleAddWorkshop() {
        String title = newWorkshopTitle.getText().trim();
        LocalDate date = newWorkshopDate.getValue();
        Artist instructor = newWorkshopInstructor.getValue();
        String priceStr = newWorkshopPrice.getText().trim();
        String level = newWorkshopLevel.getValue();

        if (title.isEmpty() || date == null || instructor == null) {
            showAlert("Validation Error", "Please fill in Title, Date, and Instructor.");
            return;
        }

        try {
            double price = priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);
            LocalDateTime dateTime = date.atStartOfDay();
            Workshop workshop = new Workshop(title, dateTime, instructor, price);
            workshop.setLevel(level);

            workshopService.save(workshop);

            newWorkshopTitle.clear();
            newWorkshopDate.setValue(null);
            newWorkshopInstructor.setValue(null);
            newWorkshopPrice.clear();
            newWorkshopLevel.setValue(null);

            showAlert("Success", "Workshop '" + title + "' added successfully!");
            refreshTable();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price must be a valid number.");
        }
    }

    @FXML
    private void handleUpdateWorkshop() {
        if (selectedWorkshop == null) {
            showAlert("Error", "Please select a workshop from the table first.");
            return;
        }

        LocalDate date = editWorkshopDate.getValue();
        String priceStr = editWorkshopPrice.getText().trim();
        String level = editWorkshopLevel.getValue();

        if (date == null) {
            showAlert("Validation Error", "Please fill in Date.");
            return;
        }

        try {
            double price = priceStr.isEmpty() ? selectedWorkshop.getPrice() : Double.parseDouble(priceStr);
            LocalDateTime dateTime = date.atStartOfDay();
            selectedWorkshop.setDate(dateTime);
            selectedWorkshop.setPrice(price);
            selectedWorkshop.setLevel(level);

            workshopService.update(selectedWorkshop);
            showAlert("Success", "Workshop '" + selectedWorkshop.getTitle() + "' updated successfully!");
            refreshTable();
            clearEditFields();
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price must be a valid number.");
        }
    }

    @FXML
    private void handleDeleteWorkshop() {
        if (selectedWorkshop == null) {
            showAlert("Error", "Please select a workshop from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete '" + selectedWorkshop.getTitle() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            workshopService.delete(selectedWorkshop.getTitle());
            showAlert("Success", "Workshop '" + selectedWorkshop.getTitle() + "' deleted successfully!");
            refreshTable();
            clearEditFields();
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedWorkshop = workshopTable.getSelectionModel().getSelectedItem();
        if (selectedWorkshop != null) {
            editWorkshopTitle.setText(selectedWorkshop.getTitle());
            editWorkshopDate.setValue(selectedWorkshop.getDate().toLocalDate());
            editWorkshopPrice.setText(String.valueOf(selectedWorkshop.getPrice()));
            editWorkshopLevel.setValue(selectedWorkshop.getLevel() != null ? selectedWorkshop.getLevel() : "BEGINNER");
        }
    }

    private void clearEditFields() {
        editWorkshopTitle.clear();
        editWorkshopDate.setValue(null);
        editWorkshopPrice.clear();
        editWorkshopLevel.setValue(null);
        selectedWorkshop = null;
        workshopTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));
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
