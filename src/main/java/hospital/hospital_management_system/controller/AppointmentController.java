package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.*;
import hospital.hospital_management_system.services.AppointmentService;
import hospital.hospital_management_system.services.PatientService;
import hospital.hospital_management_system.services.DoctorService;
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
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        loadAppointments();
    }

    private void setupTableColumns() {
        colPatient.setCellValueFactory(data -> {
            Patient patient = data.getValue().getPatient();
            String name = patient != null 
                ? patient.getFirstName() + " " + patient.getLastName()
                : "N/A";
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        
        colDoctor.setCellValueFactory(data -> {
            Doctor doctor = data.getValue().getDoctor();
            String name = doctor != null 
                ? doctor.getFirstName() + " " + doctor.getLastName()
                : "N/A";
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
    private void handleCancel() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select an appointment to cancel");
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
        
        try {
            appointmentService.delete(selected.getAppointmentId());
            showInfo("Success", "Appointment deleted successfully");
            loadAppointments();
        } catch (Exception e) {
            showError("Error", "Failed to delete appointment", e.getMessage());
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
                    String patientName = (app.getPatient().getFirstName() + " " + app.getPatient().getLastName()).toLowerCase();
                    String doctorName = (app.getDoctor().getFirstName() + " " + app.getDoctor().getLastName()).toLowerCase();
                    return patientName.contains(searchText) || doctorName.contains(searchText);
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
