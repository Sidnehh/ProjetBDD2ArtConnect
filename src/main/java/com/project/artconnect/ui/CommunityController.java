package com.project.artconnect.ui;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Booking;
import com.project.artconnect.model.ExhibitionBooking;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.service.BookingService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class CommunityController {
    @FXML
    private TableView<CommunityMember> memberTable;
    @FXML
    private TableColumn<CommunityMember, String> nameColumn;
    @FXML
    private TableColumn<CommunityMember, String> emailColumn;
    @FXML
    private TableColumn<CommunityMember, String> cityColumn;

    // Create fields
    @FXML
    private TextField newMemberName;
    @FXML
    private TextField newMemberEmail;
    @FXML
    private TextField newMemberCity;

    // Edit fields
    @FXML
    private TextField editMemberName;
    @FXML
    private TextField editMemberEmail;
    @FXML
    private TextField editMemberCity;

    @FXML
    private TextField searchField;

    // Booking UI fields
    @FXML
    private TableView<Workshop> availableWorkshopsTable;
    @FXML
    private TableColumn<Workshop, String> wsTitle;
    @FXML
    private TableColumn<Workshop, String> wsInstructor;
    @FXML
    private TableColumn<Workshop, String> wsDate;
    @FXML
    private TableColumn<Workshop, Double> wsPrice;

    @FXML
    private TableView<Exhibition> availableExhibitionsTable;
    @FXML
    private TableColumn<Exhibition, String> exTitle;
    @FXML
    private TableColumn<Exhibition, String> exTheme;
    @FXML
    private TableColumn<Exhibition, String> exDate;

    @FXML
    private TableView<Workshop> myWorkshopsTable;
    @FXML
    private TableColumn<Workshop, String> myWsTitle;
    @FXML
    private TableColumn<Workshop, String> myWsDate;

    @FXML
    private TableView<Exhibition> myExhibitionsTable;
    @FXML
    private TableColumn<Exhibition, String> myExTitle;
    @FXML
    private TableColumn<Exhibition, String> myExDate;

    private final CommunityService communityService = ServiceProvider.getCommunityService();
    private final BookingService bookingService = ServiceProvider.getBookingService();
    private Timeline autoRefreshTimeline;
    private CommunityMember selectedMember;
    private Workshop selectedAvailableWorkshop;
    private Exhibition selectedAvailableExhibition;
    private Workshop selectedMyWorkshop;
    private Exhibition selectedMyExhibition;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        // Setup available workshops table
        wsTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        wsDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""));
        wsPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        wsInstructor.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getInstructor() != null ? cellData.getValue().getInstructor().getName() : "Unknown"));

        // Setup available exhibitions table
        exTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        exTheme.setCellValueFactory(new PropertyValueFactory<>("theme"));
        exDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate() != null ? cellData.getValue().getStartDate().toString() : ""));

        // Setup my workshops table
        myWsTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        myWsDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""));

        // Setup my exhibitions table
        myExTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        myExDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate() != null ? cellData.getValue().getStartDate().toString() : ""));

        refreshTable();
        startAutoRefresh();
    }

    @FXML
    private void handleAddMember() {
        String name = newMemberName.getText().trim();
        String email = newMemberEmail.getText().trim();
        String city = newMemberCity.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Validation Error", "Please fill in Name and Email.");
            return;
        }

        CommunityMember member = new CommunityMember(name, email, city);

        communityService.createMember(member);

        newMemberName.clear();
        newMemberEmail.clear();
        newMemberCity.clear();

        showAlert("Success", "Member '" + name + "' added successfully!");
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
        var filtered = communityService.getAllMembers().stream()
                .filter(m -> (m.getName() != null && m.getName().toLowerCase().contains(lower))
                        || (m.getEmail() != null && m.getEmail().toLowerCase().contains(lower))
                        || (m.getCity() != null && m.getCity().toLowerCase().contains(lower)))
                .toList();
        memberTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        refreshTable();
    }

    @FXML
    private void handleUpdateMember() {
        if (selectedMember == null) {
            showAlert("Error", "Please select a member from the table first.");
            return;
        }

        String name = editMemberName.getText().trim();
        String email = editMemberEmail.getText().trim();
        String city = editMemberCity.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Validation Error", "Please fill in Name and Email.");
            return;
        }

        selectedMember.setName(name);
        selectedMember.setEmail(email);
        selectedMember.setCity(city);

        communityService.updateMember(selectedMember);
        showAlert("Success", "Member '" + selectedMember.getName() + "' updated successfully!");
        refreshTable();
        clearEditFields();
    }

    @FXML
    private void handleDeleteMember() {
        if (selectedMember == null) {
            showAlert("Error", "Please select a member from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Are you sure you want to delete '" + selectedMember.getName() + "'?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            communityService.deleteMember(selectedMember.getId());
            showAlert("Success", "Member '" + selectedMember.getName() + "' deleted successfully!");
            refreshTable();
            clearEditFields();
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {
            editMemberName.setText(selectedMember.getName());
            editMemberEmail.setText(selectedMember.getEmail() != null ? selectedMember.getEmail() : "");
            editMemberCity.setText(selectedMember.getCity() != null ? selectedMember.getCity() : "");
            
            // Load available workshops and exhibitions for this member
            loadAvailableWorkshops();
            loadAvailableExhibitions();
            loadMyWorkshops();
            loadMyExhibitions();
        }
    }

    @FXML
    private void handleWorkshopSelection() {
        selectedAvailableWorkshop = availableWorkshopsTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleExhibitionSelection() {
        selectedAvailableExhibition = availableExhibitionsTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleMyWorkshopSelection() {
        selectedMyWorkshop = myWorkshopsTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleMyExhibitionSelection() {
        selectedMyExhibition = myExhibitionsTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void handleRegisterWorkshop() {
        if (selectedMember == null) {
            showAlert("Error", "Please select a member first.");
            return;
        }
        if (selectedAvailableWorkshop == null) {
            showAlert("Error", "Please select a workshop to register.");
            return;
        }

        try {
            bookingService.registerToWorkshop(selectedMember.getId(), selectedAvailableWorkshop.getId());
            showAlert("Success", "Member registered to workshop successfully!");
            loadAvailableWorkshops();
            loadMyWorkshops();
            selectedAvailableWorkshop = null;
        } catch (Exception e) {
            showAlert("Error", "Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterExhibition() {
        if (selectedMember == null) {
            showAlert("Error", "Please select a member first.");
            return;
        }
        if (selectedAvailableExhibition == null) {
            showAlert("Error", "Please select an exhibition to register.");
            return;
        }

        try {
            bookingService.registerToExhibition(selectedMember.getId(), selectedAvailableExhibition.getId());
            showAlert("Success", "Member registered to exhibition successfully!");
            loadAvailableExhibitions();
            loadMyExhibitions();
            selectedAvailableExhibition = null;
        } catch (Exception e) {
            showAlert("Error", "Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleUnregisterWorkshop() {
        if (selectedMember == null) {
            showAlert("Error", "Please select a member first.");
            return;
        }
        if (selectedMyWorkshop == null) {
            showAlert("Error", "Please select a workshop to unregister.");
            return;
        }

        try {
            bookingService.unregisterFromWorkshop(selectedMember.getId(), selectedMyWorkshop.getId());
            showAlert("Success", "Member unregistered from workshop successfully!");
            loadAvailableWorkshops();
            loadMyWorkshops();
            selectedMyWorkshop = null;
        } catch (Exception e) {
            showAlert("Error", "Unregistration failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleUnregisterExhibition() {
        if (selectedMember == null) {
            showAlert("Error", "Please select a member first.");
            return;
        }
        if (selectedMyExhibition == null) {
            showAlert("Error", "Please select an exhibition to unregister.");
            return;
        }

        try {
            bookingService.unregisterFromExhibition(selectedMember.getId(), selectedMyExhibition.getId());
            showAlert("Success", "Member unregistered from exhibition successfully!");
            loadAvailableExhibitions();
            loadMyExhibitions();
            selectedMyExhibition = null;
        } catch (Exception e) {
            showAlert("Error", "Unregistration failed: " + e.getMessage());
        }
    }

    private void loadAvailableWorkshops() {
        if (selectedMember == null) {
            availableWorkshopsTable.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            var workshops = bookingService.getAvailableWorkshopsForMember(selectedMember.getId());
            availableWorkshopsTable.setItems(FXCollections.observableArrayList(workshops));
        } catch (Exception e) {
            showAlert("Error", "Failed to load available workshops: " + e.getMessage());
        }
    }

    private void loadAvailableExhibitions() {
        if (selectedMember == null) {
            availableExhibitionsTable.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            var exhibitions = bookingService.getAvailableExhibitionsForMember(selectedMember.getId());
            availableExhibitionsTable.setItems(FXCollections.observableArrayList(exhibitions));
        } catch (Exception e) {
            showAlert("Error", "Failed to load available exhibitions: " + e.getMessage());
        }
    }

    private void loadMyWorkshops() {
        if (selectedMember == null) {
            myWorkshopsTable.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            var bookings = bookingService.getMemberWorkshopBookings(selectedMember.getId());
            var workshops = bookings.stream().map(Booking::getWorkshop).toList();
            myWorkshopsTable.setItems(FXCollections.observableArrayList(workshops));
        } catch (Exception e) {
            showAlert("Error", "Failed to load your workshops: " + e.getMessage());
        }
    }

    private void loadMyExhibitions() {
        if (selectedMember == null) {
            myExhibitionsTable.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            var bookings = bookingService.getMemberExhibitionBookings(selectedMember.getId());
            var exhibitions = bookings.stream().map(ExhibitionBooking::getExhibition).toList();
            myExhibitionsTable.setItems(FXCollections.observableArrayList(exhibitions));
        } catch (Exception e) {
            showAlert("Error", "Failed to load your exhibitions: " + e.getMessage());
        }
    }

    private void clearEditFields() {
        editMemberName.clear();
        editMemberEmail.clear();
        editMemberCity.clear();
        selectedMember = null;
        memberTable.getSelectionModel().clearSelection();
        availableWorkshopsTable.setItems(FXCollections.observableArrayList());
        availableExhibitionsTable.setItems(FXCollections.observableArrayList());
        myWorkshopsTable.setItems(FXCollections.observableArrayList());
        myExhibitionsTable.setItems(FXCollections.observableArrayList());
    }

    private void refreshTable() {
        memberTable.setItems(FXCollections.observableArrayList(communityService.getAllMembers()));
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
            if (selectedMember != null) {
                loadMyWorkshops();
                loadMyExhibitions();
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
