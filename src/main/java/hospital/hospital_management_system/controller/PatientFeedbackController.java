package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.model.PatientFeedback;
import hospital.hospital_management_system.services.PatientFeedbackService;
import hospital.hospital_management_system.services.PatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class PatientFeedbackController {

    @FXML private TableView<PatientFeedback> feedbackTable;
    @FXML private TableColumn<PatientFeedback, Long> colId;
    @FXML private TableColumn<PatientFeedback, Long> colPatientId;
    @FXML private TableColumn<PatientFeedback, Integer> colRating;
    @FXML private TableColumn<PatientFeedback, String> colComments;
    @FXML private TableColumn<PatientFeedback, LocalDate> colDate;

    @FXML private TextField txtPatientId;
    @FXML private ComboBox<Integer> cbRating;
    @FXML private TextArea txtComments;

    private final PatientFeedbackService feedbackService = new PatientFeedbackService();
    private final PatientService patientService = new PatientService();
    private final ObservableList<PatientFeedback> feedbackList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getFeedbackId()).asObject());
        colPatientId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getPatientId()).asObject());
        colRating.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRating()).asObject());
        colComments.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getComments()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getFeedbackDate()));

        cbRating.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        loadFeedback();

        feedbackTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtPatientId.setText(String.valueOf(selected.getPatientId()));
                cbRating.setValue(selected.getRating());
                txtComments.setText(selected.getComments());
            }
        });
    }

    private void loadFeedback() {
        List<PatientFeedback> feedbackList = feedbackService.getAllFeedback();
        this.feedbackList.setAll(feedbackList);
        feedbackTable.setItems(this.feedbackList);
    }

    @FXML
    private void addFeedback() {
        Patient patient = patientService.getPatientById(Long.parseLong(txtPatientId.getText()));
        PatientFeedback feedback = new PatientFeedback(patient, cbRating.getValue(), txtComments.getText());
        feedbackService.addFeedback(feedback);
        loadFeedback();
        clearFields();
    }

    @FXML
    private void deleteFeedback() {
        PatientFeedback selected = feedbackTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            feedbackService.deleteFeedback(selected.getFeedbackId());
            loadFeedback();
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        txtPatientId.clear();
        cbRating.setValue(null);
        txtComments.clear();
    }
}
