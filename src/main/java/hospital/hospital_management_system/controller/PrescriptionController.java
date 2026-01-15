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
import java.util.List;

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

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getPrescriptionId()).asObject());
        colAppointment.setCellValueFactory(data -> {
            Long appId = data.getValue().getAppointmentId();
            Appointment app = appointmentService.getAll().stream()
                .filter(a -> a.getAppointmentId().equals(appId))
                .findFirst().orElse(null);
            if (app != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    app.getPatient().getFirstName() + " " + app.getPatient().getLastName() + 
                    " - Dr. " + app.getDoctor().getLastName());
            }
            return new javafx.beans.property.SimpleStringProperty("Appointment #" + appId);
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
        cbAppointment.setItems(FXCollections.observableArrayList(appointments));
        cbAppointment.setCellFactory(lv -> new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("#" + item.getAppointmentId() + " - " + 
                           item.getPatient().getFirstName() + " " + item.getPatient().getLastName() + 
                           " with Dr. " + item.getDoctor().getLastName());
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
                    setText("#" + item.getAppointmentId() + " - " + 
                           item.getPatient().getFirstName() + " " + item.getPatient().getLastName());
                }
            }
        });
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
}
