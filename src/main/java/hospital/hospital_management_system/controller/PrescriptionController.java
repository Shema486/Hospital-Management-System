package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Appointment;
import hospital.hospital_management_system.model.Prescriptions;
import hospital.hospital_management_system.services.AppointmentService;
import hospital.hospital_management_system.services.PrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrescriptionController {

    @FXML private TableView<Prescriptions> prescriptionTable;
    @FXML private TableColumn<Prescriptions, Long> colId;
    @FXML private TableColumn<Prescriptions, String> colAppointment;
    @FXML private TableColumn<Prescriptions, LocalDateTime> colDate;
    @FXML private TableColumn<Prescriptions, String> colNotes;

    @FXML private ComboBox<Appointment> cbAppointment;
    @FXML private TextArea txtNotes;

    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final ObservableList<Prescriptions> prescriptionList = FXCollections.observableArrayList();
    // Simple lookup to avoid repeated DB calls in cell renderers.
    private final Map<Long, Appointment> appointmentById = new HashMap<>();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getPrescriptionId()).asObject());
        colAppointment.setCellValueFactory(data -> {
            Long appId = data.getValue().getAppointmentId();
            Appointment app = appointmentById.get(appId);
            return new javafx.beans.property.SimpleStringProperty(getAppointmentDisplay(appId, app));
        });
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrescriptionDate()));
        colNotes.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNotes()));

        loadAppointments();
        loadPrescriptions();

        prescriptionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtNotes.setText(selected.getNotes());
            }
        });
    }

    private void loadAppointments() {
        List<Appointment> appointments = appointmentService.getAll();
        appointmentById.clear();
        for (Appointment appointment : appointments) {
            appointmentById.put(appointment.getAppointmentId(), appointment);
        }
        cbAppointment.setItems(FXCollections.observableArrayList(appointments));
        configureAppointmentComboBox();
    }

    private void loadPrescriptions() {
        List<Prescriptions> prescriptions = prescriptionService.getAllPrescriptions();
        prescriptionList.setAll(prescriptions);
        prescriptionTable.setItems(prescriptionList);
    }

    @FXML
    private void addPrescription() {
        if (cbAppointment.getValue() != null) {
            Prescriptions prescription = new Prescriptions(
                cbAppointment.getValue().getAppointmentId(),
                LocalDateTime.now(),
                txtNotes.getText()
            );
            prescriptionService.addPrescription(prescription);
            loadPrescriptions();
            clearFields();
        }
    }

    @FXML
    private void updatePrescription() {
        Prescriptions selected = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            prescriptionService.updatePrescriptionNotes(selected.getPrescriptionId(), txtNotes.getText());
            loadPrescriptions();
            clearFields();
        }
    }

    @FXML
    private void deletePrescription() {
        Prescriptions selected = prescriptionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            prescriptionService.deletePrescription(selected.getPrescriptionId());
            loadPrescriptions();
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        cbAppointment.setValue(null);
        txtNotes.clear();
    }

    private void configureAppointmentComboBox() {
        cbAppointment.setCellFactory(lv -> new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getAppointmentDisplay(item.getAppointmentId(), item));
                }
            }
        });
        cbAppointment.setButtonCell(new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getAppointmentDisplay(item.getAppointmentId(), item));
                }
            }
        });
    }

    private String getAppointmentDisplay(Long appointmentId, Appointment appointment) {
        if (appointment == null) {
            return "Appointment #" + appointmentId;
        }
        String patientName = "Unknown Patient";
        if (appointment.getPatient() != null) {
            patientName = appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName();
        }
        String doctorName = "Unknown Doctor";
        if (appointment.getDoctor() != null) {
            doctorName = "Dr. " + appointment.getDoctor().getLastName();
        }
        return "#" + appointmentId + " - " + patientName + " - " + doctorName;
    }
}
