package com.project.artconnect.ui;

import com.project.artconnect.model.Booking;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.ExhibitionBooking;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.BookingService;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;

public class RegistrationController {

    @FXML
    private ComboBox<CommunityMember> memberCombo;
    @FXML
    private ComboBox<String> targetTypeCombo;
    @FXML
    private ComboBox<Workshop> workshopCombo;
    @FXML
    private ComboBox<Exhibition> exhibitionCombo;

    @FXML
    private Label selectedTargetLabel;

    @FXML
    private TableView<Workshop> memberWorkshopsTable;
    @FXML
    private TableColumn<Workshop, String> mwTitle;
    @FXML
    private TableColumn<Workshop, String> mwInstructor;
    @FXML
    private TableColumn<Workshop, String> mwDate;
    @FXML
    private TableColumn<Workshop, Double> mwPrice;
    @FXML
    private TableColumn<Workshop, String> mwLevel;

    @FXML
    private TableView<Exhibition> memberExhibitionsTable;
    @FXML
    private TableColumn<Exhibition, String> meTitle;
    @FXML
    private TableColumn<Exhibition, String> meGallery;
    @FXML
    private TableColumn<Exhibition, String> meStartDate;
    @FXML
    private TableColumn<Exhibition, String> meTheme;

    @FXML
    private TableView<CommunityMember> targetMembersTable;
    @FXML
    private TableColumn<CommunityMember, String> tmName;
    @FXML
    private TableColumn<CommunityMember, String> tmEmail;
    @FXML
    private TableColumn<CommunityMember, String> tmCity;

    private final CommunityService communityService = ServiceProvider.getCommunityService();
    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();
    private final BookingService bookingService = ServiceProvider.getBookingService();

    @FXML
    public void initialize() {
        setupColumns();
        setupConverters();
        setupListeners();

        targetTypeCombo.setItems(FXCollections.observableArrayList("Workshop", "Exhibition"));
        targetTypeCombo.getSelectionModel().selectFirst();

        reloadSelectors();
        refreshMemberEnrollments();
        refreshTargetMembers();
    }

    @FXML
    private void handleRegister() {
        CommunityMember member = memberCombo.getValue();
        if (member == null) {
            showError("Please select a member.");
            return;
        }

        try {
            if (isWorkshopMode()) {
                Workshop workshop = workshopCombo.getValue();
                if (workshop == null) {
                    showError("Please select a workshop.");
                    return;
                }
                bookingService.registerToWorkshop(member.getId(), workshop.getId());
            } else {
                Exhibition exhibition = exhibitionCombo.getValue();
                if (exhibition == null) {
                    showError("Please select an exhibition.");
                    return;
                }
                bookingService.registerToExhibition(member.getId(), exhibition.getId());
            }

            refreshMemberEnrollments();
            refreshTargetMembers();
            showInfo("Registration completed.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUnregister() {
        CommunityMember member = memberCombo.getValue();
        if (member == null) {
            showError("Please select a member.");
            return;
        }

        try {
            if (isWorkshopMode()) {
                Workshop workshop = workshopCombo.getValue();
                if (workshop == null) {
                    showError("Please select a workshop.");
                    return;
                }
                bookingService.unregisterFromWorkshop(member.getId(), workshop.getId());
            } else {
                Exhibition exhibition = exhibitionCombo.getValue();
                if (exhibition == null) {
                    showError("Please select an exhibition.");
                    return;
                }
                bookingService.unregisterFromExhibition(member.getId(), exhibition.getId());
            }

            refreshMemberEnrollments();
            refreshTargetMembers();
            showInfo("Unregistration completed.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleRefreshAll() {
        reloadSelectors();
        refreshMemberEnrollments();
        refreshTargetMembers();
    }

    private void setupColumns() {
        mwTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        mwInstructor.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getInstructor() != null ? cellData.getValue().getInstructor().getName() : ""));
        mwDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""));
        mwPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        mwLevel.setCellValueFactory(new PropertyValueFactory<>("level"));

        meTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        meGallery.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getGallery() != null ? cellData.getValue().getGallery().getName() : ""));
        meStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStartDate() != null ? cellData.getValue().getStartDate().toString() : ""));
        meTheme.setCellValueFactory(new PropertyValueFactory<>("theme"));

        tmName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tmEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tmCity.setCellValueFactory(new PropertyValueFactory<>("city"));
    }

    private void setupConverters() {
        memberCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(CommunityMember member) {
                if (member == null) {
                    return "";
                }
                return member.getName() + " (" + member.getEmail() + ")";
            }

            @Override
            public CommunityMember fromString(String string) {
                return null;
            }
        });

        workshopCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Workshop workshop) {
                if (workshop == null) {
                    return "";
                }
                String instructor = workshop.getInstructor() != null ? workshop.getInstructor().getName() : "";
                String date = workshop.getDate() != null ? workshop.getDate().toString() : "";
                return workshop.getTitle() + " | " + instructor + " | " + date + " | " + workshop.getPrice() + " | " + workshop.getLevel();
            }

            @Override
            public Workshop fromString(String string) {
                return null;
            }
        });

        exhibitionCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Exhibition exhibition) {
                if (exhibition == null) {
                    return "";
                }
                String gallery = exhibition.getGallery() != null ? exhibition.getGallery().getName() : "";
                String date = exhibition.getStartDate() != null ? exhibition.getStartDate().toString() : "";
                return exhibition.getTitle() + " | " + gallery + " | " + date + " | " + exhibition.getTheme();
            }

            @Override
            public Exhibition fromString(String string) {
                return null;
            }
        });
    }

    private void setupListeners() {
        memberCombo.valueProperty().addListener((obs, oldV, newV) -> refreshMemberEnrollments());
        targetTypeCombo.valueProperty().addListener((obs, oldV, newV) -> {
            updateTargetSelectorVisibility();
            refreshTargetMembers();
        });
        workshopCombo.valueProperty().addListener((obs, oldV, newV) -> refreshTargetMembers());
        exhibitionCombo.valueProperty().addListener((obs, oldV, newV) -> refreshTargetMembers());
    }

    private void reloadSelectors() {
        List<CommunityMember> members = communityService.getAllMembers();
        List<Workshop> workshops = workshopService.getAllWorkshops();
        List<Exhibition> exhibitions = exhibitionService.getAllExhibitions();

        memberCombo.setItems(FXCollections.observableArrayList(members));
        workshopCombo.setItems(FXCollections.observableArrayList(workshops));
        exhibitionCombo.setItems(FXCollections.observableArrayList(exhibitions));

        if (!memberCombo.getItems().isEmpty() && memberCombo.getValue() == null) {
            memberCombo.getSelectionModel().selectFirst();
        }
        if (!workshopCombo.getItems().isEmpty() && workshopCombo.getValue() == null) {
            workshopCombo.getSelectionModel().selectFirst();
        }
        if (!exhibitionCombo.getItems().isEmpty() && exhibitionCombo.getValue() == null) {
            exhibitionCombo.getSelectionModel().selectFirst();
        }

        updateTargetSelectorVisibility();
    }

    private void refreshMemberEnrollments() {
        CommunityMember member = memberCombo.getValue();
        if (member == null) {
            memberWorkshopsTable.setItems(FXCollections.observableArrayList());
            memberExhibitionsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Workshop> workshops = bookingService.getMemberWorkshopBookings(member.getId())
                .stream()
                .map(Booking::getWorkshop)
                .toList();

        List<Exhibition> exhibitions = bookingService.getMemberExhibitionBookings(member.getId())
                .stream()
                .map(ExhibitionBooking::getExhibition)
                .toList();

        memberWorkshopsTable.setItems(FXCollections.observableArrayList(workshops));
        memberExhibitionsTable.setItems(FXCollections.observableArrayList(exhibitions));
    }

    private void refreshTargetMembers() {
        if (isWorkshopMode()) {
            Workshop workshop = workshopCombo.getValue();
            if (workshop == null) {
                selectedTargetLabel.setText("No workshop selected");
                targetMembersTable.setItems(FXCollections.observableArrayList());
                return;
            }
            selectedTargetLabel.setText("Members registered in workshop: " + workshop.getTitle());
            targetMembersTable.setItems(FXCollections.observableArrayList(bookingService.getWorkshopMembers(workshop.getId())));
        } else {
            Exhibition exhibition = exhibitionCombo.getValue();
            if (exhibition == null) {
                selectedTargetLabel.setText("No exhibition selected");
                targetMembersTable.setItems(FXCollections.observableArrayList());
                return;
            }
            selectedTargetLabel.setText("Members registered in exhibition: " + exhibition.getTitle());
            targetMembersTable.setItems(FXCollections.observableArrayList(bookingService.getExhibitionMembers(exhibition.getId())));
        }
    }

    private void updateTargetSelectorVisibility() {
        boolean workshopMode = isWorkshopMode();
        workshopCombo.setManaged(workshopMode);
        workshopCombo.setVisible(workshopMode);
        exhibitionCombo.setManaged(!workshopMode);
        exhibitionCombo.setVisible(!workshopMode);
    }

    private boolean isWorkshopMode() {
        return "Workshop".equals(targetTypeCombo.getValue());
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
