package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.*;
import hospital.hospital_management_system.services.AppointmentService;
import hospital.hospital_management_system.services.PatientService;
import hospital.hospital_management_system.services.DoctorService;
import hospital.hospital_management_system.services.PrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AppointmentController {

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, LocalDateTime> colDate;
    @FXML private TableColumn<Appointment, String> colStatus;
    @FXML private TableColumn<Appointment, String> colReason;
    
    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private ComboBox<Doctor> doctorComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField reasonField;
    @FXML private TextField searchField;

    private final AppointmentService appointmentService = new AppointmentService();
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupDatePicker();
        loadAppointments();
    }

    private void setupDatePicker() {
        // Disable past dates in appointment DatePicker (allow today or future)
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
                if (date.isBefore(today)) {
                    setStyle("-fx-background-color: #ffcccc;"); // Visual indication
                }
            }
        });
    }

    private void setupTableColumns() {
        colPatient.setCellValueFactory(data -> {
            Patient patient = data.getValue().getPatient();
            String name = getPatientDisplayName(patient);
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        
        colDoctor.setCellValueFactory(data -> {
            Doctor doctor = data.getValue().getDoctor();
            String name = getDoctorDisplayName(doctor);
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        
        colDate.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAppointmentDate()));
        
        colStatus.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        
        colReason.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getReason()));
        
        appointmentTable.setItems(appointmentList);
    }

    private void setupComboBoxes() {
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient == null ? "" : patient.getFirstName() + " " + patient.getLastName();
            }
            @Override
            public Patient fromString(String string) {
                return null;
            }
        });
        
        doctorComboBox.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor doctor) {
                return doctor == null ? "" : doctor.getFirstName() + " " + doctor.getLastName();
            }
            @Override
            public Doctor fromString(String string) {
                return null;
            }
        });
        
        try {
            patientComboBox.getItems().setAll(patientService.getAllPatients());
            doctorComboBox.getItems().setAll(doctorService.getAllDoctors());
        } catch (Exception e) {
            showError("Error", "Failed to load patients or doctors", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            if (patientComboBox.getValue() == null) {
                showWarning("Validation Error", "Please select a patient");
                return;
            }
            
            if (doctorComboBox.getValue() == null) {
                showWarning("Validation Error", "Please select a doctor");
                return;
            }
            
            if (datePicker.getValue() == null) {
                showWarning("Validation Error", "Please select a date");
                return;
            }
            
            String timeText = timeField.getText().trim();
            if (timeText.isEmpty()) {
                showWarning("Validation Error", "Please enter a time (HH:mm format)");
                return;
            }
            
            LocalTime time;
            try {
                time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                showWarning("Validation Error", "Invalid time format. Please use HH:mm (e.g., 14:30)");
                return;
            }
            
            String reason = reasonField.getText().trim();
            if (reason.isEmpty()) {
                showWarning("Validation Error", "Please enter a reason for the appointment");
                return;
            }
            
            // Validate date is not in the past
            LocalDate selectedDate = datePicker.getValue();
            LocalDate today = LocalDate.now();
            if (selectedDate.isBefore(today)) {
                showWarning("Validation Error", "Appointment date cannot be in the past. Please select today or a future date.");
                return;
            }
            
            // If date is today, validate time is not in the past
            if (selectedDate.equals(today)) {
                LocalTime now = LocalTime.now();
                if (time.isBefore(now)) {
                    showWarning("Validation Error", "Appointment time cannot be in the past. Please select a future time.");
                    return;
                }
            }
            
            LocalDateTime appointmentDateTime = datePicker.getValue().atTime(time);
            
            Appointment appointment = new Appointment(
                    patientComboBox.getValue(),
                    doctorComboBox.getValue(),
                    appointmentDateTime,
                    reason
            );
            
            appointmentService.create(appointment);
            showInfo("Success", "Appointment created successfully");
            clearFields();
            loadAppointments();
            
        } catch (Exception e) {
            showError("Error", "Failed to create appointment", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleComplete() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select an appointment to complete");
            return;
        }
        
        // Check if appointment is already completed
        if ("Completed".equals(selected.getStatus())) {
            showWarning("Already Completed", "This appointment is already marked as completed.");
            return;
        }
        
        try {
            appointmentService.complete(selected.getAppointmentId());
            showInfo("Success", "Appointment marked as completed successfully");
            loadAppointments();
        } catch (Exception e) {
            showError("Error", "Failed to complete appointment", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select an appointment to cancel");
            return;
        }
        
        // Check if appointment is already cancelled
        if ("Cancelled".equals(selected.getStatus())) {
            showWarning("Already Cancelled", "This appointment is already cancelled.");
            return;
        }
        
        try {
            appointmentService.cancel(selected.getAppointmentId());
            showInfo("Success", "Appointment cancelled successfully");
            loadAppointments();
        } catch (Exception e) {
            showError("Error", "Failed to cancel appointment", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select an appointment to delete");
            return;
        }
        
        // Check if appointment has a prescription (cannot delete if it has prescription)
        if (prescriptionService.hasPrescriptionForAppointment(selected.getAppointmentId())) {
            showWarning("Cannot Delete Appointment", 
                "This appointment cannot be deleted because it has an associated prescription. " +
                "Please delete the prescription first before deleting the appointment.");
            return;
        }
        
        try {
            appointmentService.delete(selected.getAppointmentId());
            showInfo("Success", "Appointment deleted successfully");
            loadAppointments();
        } catch (IllegalStateException e) {
            showWarning("Cannot Delete", e.getMessage());
        } catch (Exception e) {
            // Check if it's a foreign key constraint violation
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                showWarning("Cannot Delete Appointment", 
                    "This appointment cannot be deleted because it is referenced by other records. " +
                    "Please ensure all related prescriptions are deleted first.");
            } else {
                showError("Error", "Failed to delete appointment", errorMessage);
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadAppointments();
            return;
        }
        
        try {
            var allAppointments = appointmentService.getAll();
            var filtered = allAppointments.stream()
                .filter(app -> {
                    String patientName = getPatientDisplayName(app.getPatient()).toLowerCase();
                    String doctorName = getDoctorDisplayName(app.getDoctor()).toLowerCase();
                    String reason = app.getReason() == null ? "" : app.getReason().toLowerCase();
                    return patientName.contains(searchText)
                            || doctorName.contains(searchText)
                            || reason.contains(searchText);
                })
                .toList();
            appointmentList.setAll(filtered);
        } catch (Exception e) {
            showError("Error", "Failed to search appointments", e.getMessage());
        }
    }

    private void loadAppointments() {
        try {
            appointmentList.setAll(appointmentService.getAll());
        } catch (Exception e) {
            showError("Error", "Failed to load appointments", e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        patientComboBox.setValue(null);
        doctorComboBox.setValue(null);
        datePicker.setValue(null);
        timeField.clear();
        reasonField.clear();
    }

    private String getPatientDisplayName(Patient patient) {
        if (patient == null) {
            return "N/A";
        }
        String firstName = patient.getFirstName();
        String lastName = patient.getLastName();
        String fullName = (firstName == null ? "" : firstName.trim()) +
                (lastName == null ? "" : " " + lastName.trim());
        String trimmed = fullName.trim();
        if (!trimmed.isEmpty()) {
            return trimmed;
        }
        return patient.getPatientId() != 0 ? "Patient #" + patient.getPatientId() : "Unknown Patient";
    }

    private String getDoctorDisplayName(Doctor doctor) {
        if (doctor == null) {
            return "N/A";
        }
        String firstName = doctor.getFirstName();
        String lastName = doctor.getLastName();
        String fullName = (firstName == null ? "" : firstName.trim()) +
                (lastName == null ? "" : " " + lastName.trim());
        String trimmed = fullName.trim();
        if (!trimmed.isEmpty()) {
            return trimmed;
        }
        return doctor.getDoctorId() != null ? "Doctor #" + doctor.getDoctorId() : "Unknown Doctor";
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
