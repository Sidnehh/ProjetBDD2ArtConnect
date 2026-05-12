package com.project.artconnect.ui;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.util.ServiceProvider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
    @FXML
    private Spinner<Integer> newMemberBirthYear;
    @FXML
    private ComboBox<String> newMemberType;

    // Edit fields
    @FXML
    private TextField editMemberName;
    @FXML
    private TextField editMemberEmail;
    @FXML
    private TextField editMemberCity;
    @FXML
    private Spinner<Integer> editMemberBirthYear;
    @FXML
    private ComboBox<String> editMemberType;

    private final CommunityService communityService = ServiceProvider.getCommunityService();
    private Timeline autoRefreshTimeline;
    private CommunityMember selectedMember;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        initializeSpinners();
        // Populate membership type ComboBox items
        newMemberType.setItems(FXCollections.observableArrayList("REGULAR", "PREMIUM", "VIP"));
        editMemberType.setItems(FXCollections.observableArrayList("REGULAR", "PREMIUM", "VIP"));
        
        refreshTable();
        startAutoRefresh();
    }

    private void initializeSpinners() {
        SpinnerValueFactory<Integer> yearFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2024, 2000);
        newMemberBirthYear.setValueFactory(yearFactory);
        
        SpinnerValueFactory<Integer> editYearFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1900, 2024, 2000);
        editMemberBirthYear.setValueFactory(editYearFactory);
    }

    @FXML
    private void handleAddMember() {
        String name = newMemberName.getText().trim();
        String email = newMemberEmail.getText().trim();
        String city = newMemberCity.getText().trim();
        Integer birthYear = newMemberBirthYear.getValue();
        String memberType = newMemberType.getValue();

        if (name.isEmpty() || email.isEmpty() || city.isEmpty()) {
            showAlert("Validation Error", "Please fill in Name, Email, and City.");
            return;
        }

        CommunityMember member = new CommunityMember(name, email, birthYear, city);
        member.setMembershipType(memberType != null ? memberType : "REGULAR");

        communityService.createMember(member);

        newMemberName.clear();
        newMemberEmail.clear();
        newMemberCity.clear();
        newMemberBirthYear.getValueFactory().setValue(2000);
        newMemberType.setValue(null);

        showAlert("Success", "Member '" + name + "' added successfully!");
        refreshTable();
    }

    @FXML
    private void handleUpdateMember() {
        if (selectedMember == null) {
            showAlert("Error", "Please select a member from the table first.");
            return;
        }

        String email = editMemberEmail.getText().trim();
        String city = editMemberCity.getText().trim();
        Integer birthYear = editMemberBirthYear.getValue();
        String memberType = editMemberType.getValue();

        if (email.isEmpty() || city.isEmpty()) {
            showAlert("Validation Error", "Please fill in Email and City.");
            return;
        }

        selectedMember.setEmail(email);
        selectedMember.setCity(city);
        selectedMember.setBirthYear(birthYear);
        selectedMember.setMembershipType(memberType != null ? memberType : "REGULAR");

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
            editMemberBirthYear.getValueFactory().setValue(selectedMember.getBirthYear());
            editMemberType.setValue(selectedMember.getMembershipType() != null ? selectedMember.getMembershipType() : "REGULAR");
        }
    }

    private void clearEditFields() {
        editMemberName.clear();
        editMemberEmail.clear();
        editMemberCity.clear();
        editMemberBirthYear.getValueFactory().setValue(2000);
        editMemberType.setValue(null);
        selectedMember = null;
        memberTable.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        memberTable.setItems(FXCollections.observableArrayList(communityService.getAllMembers()));
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
