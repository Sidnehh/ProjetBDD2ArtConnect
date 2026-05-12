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

    private final CommunityService communityService = ServiceProvider.getCommunityService();
    private Timeline autoRefreshTimeline;
    private CommunityMember selectedMember;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

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
