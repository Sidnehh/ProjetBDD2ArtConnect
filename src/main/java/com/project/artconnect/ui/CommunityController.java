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

    // Edit fields
    @FXML
    private TextField editMemberName;
    @FXML
    private TextField editMemberEmail;
    @FXML
    private TextField editMemberCity;

    @FXML
    private TextField searchField;

    private final CommunityService communityService = ServiceProvider.getCommunityService();
    private Timeline autoRefreshTimeline;
    private CommunityMember selectedMember;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        refreshTable();
    }

    @FXML
    private void handleAddMember() {
        String name = newMemberName.getText().trim();
        String email = newMemberEmail.getText().trim();
        String city = newMemberCity.getText().trim();

        if (name.isEmpty() || email.isEmpty() || city.isEmpty()) {
            showAlert("Validation Error", "Please fill in Name, Email, and City.");
            return;
        }

        try {
            CommunityMember member = new CommunityMember(name, email, city);
            communityService.createMember(member);
        } catch (RuntimeException e) {
            showAlert("Error", "Failed to add member: " + e.getMessage());
            return;
        }

        newMemberName.clear();
        newMemberEmail.clear();
        newMemberCity.clear();

        showAlert("Success", "Member '" + name + "' added successfully!");
        refreshTable();
        if (RegistrationController.getInstance() != null) {
            RegistrationController.getInstance().reloadSelectors();
        }
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

        try {
            communityService.updateMember(selectedMember);
        } catch (RuntimeException e) {
            showAlert("Error", "Failed to update member: " + e.getMessage());
            return;
        }

        showAlert("Success", "Member '" + selectedMember.getName() + "' updated successfully!");
        refreshTable();
        clearEditFields();
        RegistrationController.refreshSelectorsIfOpen();
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
            try {
                communityService.deleteMember(selectedMember.getId());
            } catch (RuntimeException e) {
                showAlert("Error", "Failed to delete member: " + e.getMessage());
                return;
            }
            showAlert("Success", "Member '" + selectedMember.getName() + "' deleted successfully!");
            refreshTable();
            clearEditFields();
            RegistrationController.refreshSelectorsIfOpen();
        }
    }

    @FXML
    private void handleTableSelection() {
        selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {
            editMemberName.setText(selectedMember.getName());
            editMemberEmail.setText(selectedMember.getEmail() != null ? selectedMember.getEmail() : "");
            editMemberCity.setText(selectedMember.getCity() != null ? selectedMember.getCity() : "");
        }
    }

    private void clearEditFields() {
        editMemberName.clear();
        editMemberEmail.clear();
        editMemberCity.clear();
        selectedMember = null;
        memberTable.getSelectionModel().clearSelection();
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
