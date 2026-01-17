package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.model.PatientFeedback;
import hospital.hospital_management_system.services.PatientFeedbackService;
import hospital.hospital_management_system.services.PatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class PatientFeedbackController {

    @FXML private TableView<PatientFeedback> feedbackTable;
    @FXML private TableColumn<PatientFeedback, Long> colId;
    @FXML private TableColumn<PatientFeedback, String> colPatientName;
    @FXML private TableColumn<PatientFeedback, Integer> colRating;
    @FXML private TableColumn<PatientFeedback, String> colComments;
    @FXML private TableColumn<PatientFeedback, LocalDate> colDate;

    @FXML private ComboBox<Patient> cbPatient;
    @FXML private ComboBox<Integer> cbRating;
    @FXML private TextArea txtComments;

    private final PatientFeedbackService feedbackService = new PatientFeedbackService();
    private final PatientService patientService = new PatientService();
    private final ObservableList<PatientFeedback> feedbackList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getFeedbackId()).asObject());
        colPatientName.setCellValueFactory(data -> {
            PatientFeedback feedback = data.getValue();
            Patient patient = patientService.getPatientById(feedback.getPatientId());
            String patientName = patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(patientName);
        });
        colRating.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRating()).asObject());
        colComments.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getComments()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getFeedbackDate()));

        // Setup patient dropdown
        List<Patient> patients = patientService.getAllPatients();
        cbPatient.setItems(FXCollections.observableArrayList(patients));
        cbPatient.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient == null ? "" : patient.getFirstName() + " " + patient.getLastName();
            }

            @Override
            public Patient fromString(String string) {
                return null;
            }
        });

        cbRating.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        loadFeedback();

        feedbackTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                Patient patient = patientService.getPatientById(selected.getPatientId());
                cbPatient.setValue(patient);
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
        if (cbPatient.getValue() == null) {
            showWarning("Validation Error", "Please select a patient");
            return;
        }
        if (cbRating.getValue() == null) {
            showWarning("Validation Error", "Please select a rating");
            return;
        }
        if (txtComments.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Please enter comments");
            return;
        }
        
        PatientFeedback feedback = new PatientFeedback(cbPatient.getValue(), cbRating.getValue(), txtComments.getText());
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
        cbPatient.setValue(null);
        cbRating.setValue(null);
        txtComments.clear();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
