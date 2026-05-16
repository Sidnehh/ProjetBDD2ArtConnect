package com.project.artconnect.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class MainController {
    @FXML
    private TabPane mainTabPane;

    @FXML
    public void initialize() {
        // Initialization logic if needed
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

        @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About ArtConnect");
        alert.setHeaderText("ArtConnect Pro v1.0");
        alert.setContentText("Welcome to ArtConnect!\n\n" +
                "A professional platform for artists, galleries, and art enthusiasts.\n\n" +
                "Features:\n" +
                "- Manage artworks and exhibitions\n" +
                "- Organize workshops\n" +
                "- Connect with community members\n\n" +
                "© 2026 ArtConnect. All rights reserved.");
        alert.showAndWait();
    }
}
