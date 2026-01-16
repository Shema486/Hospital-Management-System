package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.*;
import hospital.hospital_management_system.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ReportsController {

    @FXML private ComboBox<Patient> cbPatient;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Long> colAppId;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, String> colDepartment;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colStatus;
    
    @FXML private Label lblPatientInfo;
    @FXML private Label lblTotalAppointments;

    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup table columns
        colAppId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getAppointmentId()).asObject());
        colDoctor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            getDoctorDisplay(data.getValue())));
        colDepartment.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            getDepartmentDisplay(data.getValue())));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getAppointmentDate() != null ? data.getValue().getAppointmentDate().toString() : "N/A"));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getStatus() != null ? data.getValue().getStatus() : "N/A"));

        // Load patients into dropdown
        loadPatients();

        // When patient is selected, show their appointments
        cbPatient.setOnAction(e -> showPatientAppointments());
    }

    private void loadPatients() {
        List<Patient> patients = patientService.getAllPatients();
        cbPatient.setItems(FXCollections.observableArrayList(patients));
        
        // Custom display for patient dropdown
        cbPatient.setCellFactory(lv -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName() + " (ID: " + item.getPatientId() + ")");
                }
            }
        });
        
        cbPatient.setButtonCell(new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFirstName() + " " + item.getLastName());
                }
            }
        });
    }

    private void showPatientAppointments() {
        Patient selected = cbPatient.getValue();
        if (selected != null) {
            // Show patient info
            lblPatientInfo.setText("Patient: " + selected.getFirstName() + " " + selected.getLastName() +
                                  " | DOB: " + selected.getDob() + " | Contact: " + selected.getContact_number());
            
            // Get all appointments and filter by patient
            List<Appointment> allAppointments = appointmentService.getAll();
            List<Appointment> patientAppointments = allAppointments.stream()
                .filter(app -> app.getPatientId().equals(selected.getPatientId()))
                .toList();
            
            appointmentList.setAll(patientAppointments);
            appointmentTable.setItems(appointmentList);
            
            lblTotalAppointments.setText("Total Appointments: " + patientAppointments.size());
        }
    }

    private String getDoctorDisplay(Appointment appointment) {
        if (appointment == null || appointment.getDoctor() == null) {
            return "N/A";
        }
        Doctor doctor = appointment.getDoctor();
        String specialization = doctor.getSpecialization() != null ? doctor.getSpecialization() : "General";
        return "Dr. " + doctor.getLastName() + " (" + specialization + ")";
    }

    private String getDepartmentDisplay(Appointment appointment) {
        if (appointment == null || appointment.getDoctor() == null) {
            return "N/A";
        }
        Department dept = appointment.getDoctor().getDepartment();
        return dept != null ? dept.getDeptName() : "N/A";
    }
}
