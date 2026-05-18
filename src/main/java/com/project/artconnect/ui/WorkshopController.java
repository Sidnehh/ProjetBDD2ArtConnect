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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class WorkshopController {
    private static WorkshopController instance;

    public static void refreshArtistSelectorsIfOpen() {
        if (instance != null) {
            instance.loadInstructors();
        }
    }

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
    private TextField newWorkshopTime;
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
    private TextField editWorkshopTime;
    @FXML
    private TextField editWorkshopPrice;
    @FXML
    private ComboBox<String> editWorkshopLevel;
    @FXML
    private ComboBox<Artist> editWorkshopInstructor;
    @FXML
    private TextField searchField;

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final ArtistService artistService = ServiceProvider.getArtistService();
    private Timeline autoRefreshTimeline;
    private Workshop selectedWorkshop;

    @FXML
    public void initialize() {
        instance = this;
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
    }

    private void loadInstructors() {
        newWorkshopInstructor.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
        editWorkshopInstructor.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
    }

    @FXML
    private void handleAddWorkshop() {
        String title = newWorkshopTitle.getText().trim();
        LocalDate date = newWorkshopDate.getValue();
        Artist instructor = newWorkshopInstructor.getValue();
        String timeStr = newWorkshopTime.getText().trim();
        String priceStr = newWorkshopPrice.getText().trim();
        String level = newWorkshopLevel.getValue();

        if (title.isEmpty() || date == null || instructor == null || timeStr.isEmpty() || priceStr.isEmpty() || level == null) {
            showAlert("Validation Error", "Please fill in all fields: Title, Date, Time, Instructor, Price, and Level.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm"));
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            Workshop workshop = new Workshop(title, dateTime, instructor, price);
            workshop.setLevel(level);

            try {
                workshopService.save(workshop);
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to add workshop: " + re.getMessage());
                return;
            }

            newWorkshopTitle.clear();
            newWorkshopDate.setValue(null);
            newWorkshopInstructor.setValue(null);
            newWorkshopPrice.clear();
            newWorkshopLevel.setValue(null);

            showAlert("Success", "Workshop '" + title + "' added successfully!");
            refreshTable();
            if (RegistrationController.getInstance() != null) {
                RegistrationController.getInstance().reloadSelectors();
            }
            DiscoverController.refreshFeaturedIfOpen();
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
    private void handleUpdateWorkshop() {
        if (selectedWorkshop == null) {
            showAlert("Error", "Please select a workshop from the table first.");
            return;
        }

        String title = editWorkshopTitle.getText().trim();
        LocalDate date = editWorkshopDate.getValue();
        String timeStr = editWorkshopTime.getText().trim();
        Artist instructor = editWorkshopInstructor.getValue();
        String priceStr = editWorkshopPrice.getText().trim();
        String level = editWorkshopLevel.getValue();

        if (date == null) {
            showAlert("Validation Error", "Please fill in Date.");
            return;
        }

        try {
            double price = priceStr.isEmpty() ? selectedWorkshop.getPrice() : Double.parseDouble(priceStr);
            LocalTime time = timeStr.isEmpty() ? selectedWorkshop.getDate().toLocalTime() : LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm"));
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            if (!title.isEmpty()) selectedWorkshop.setTitle(title);
            selectedWorkshop.setDate(dateTime);
            if (instructor != null) selectedWorkshop.setInstructor(instructor);
            selectedWorkshop.setPrice(price);
            selectedWorkshop.setLevel(level);

            try {
                workshopService.update(selectedWorkshop);
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to update workshop: " + re.getMessage());
                return;
            }

            showAlert("Success", "Workshop '" + selectedWorkshop.getTitle() + "' updated successfully!");
            refreshTable();
            clearEditFields();
            RegistrationController.refreshSelectorsIfOpen();
            DiscoverController.refreshFeaturedIfOpen();
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Update Failed");
            alert.setContentText(errorMsg); 
            alert.showAndWait();
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
            try {
                workshopService.deleteWorkshopById(selectedWorkshop.getId());
                showAlert("Success", "Workshop '" + selectedWorkshop.getTitle() + "' deleted successfully!");
                refreshTable();
                clearEditFields();
                RegistrationController.refreshSelectorsIfOpen();
                DiscoverController.refreshFeaturedIfOpen();
            } catch (RuntimeException re) {
                showAlert("Error", "Failed to delete workshop: " + re.getMessage());
                return;
            }
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedWorkshop = workshopTable.getSelectionModel().getSelectedItem();
        if (selectedWorkshop != null) {
            editWorkshopTitle.setText(selectedWorkshop.getTitle());
            editWorkshopDate.setValue(selectedWorkshop.getDate().toLocalDate());
            editWorkshopTime.setText(selectedWorkshop.getDate().toLocalTime().toString());
            editWorkshopPrice.setText(String.valueOf(selectedWorkshop.getPrice()));
            editWorkshopLevel.setValue(selectedWorkshop.getLevel() != null ? selectedWorkshop.getLevel() : "Beginner");
            editWorkshopInstructor.setValue(selectedWorkshop.getInstructor());
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
        loadInstructors();
        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));
        clearEditFields();
    }

    @FXML
    private void handleSearch() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            refreshTable();
            return;
        }
        String lower = q.toLowerCase();
        var filtered = workshopService.getAllWorkshops().stream()
                .filter(w -> (w.getTitle() != null && w.getTitle().toLowerCase().contains(lower))
                        || (w.getInstructor() != null && w.getInstructor().getName() != null && w.getInstructor().getName().toLowerCase().contains(lower)))
                .toList();
        workshopTable.setItems(FXCollections.observableArrayList(filtered));
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
