package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.services.PatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class PatientController {

    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, Long> colId;
    @FXML
    private TableColumn<Patient, String> colFirstName;
    @FXML
    private TableColumn<Patient, String> colLastName;
    @FXML
    private TableColumn<Patient, LocalDate> colDob;
    @FXML
    private TableColumn<Patient, String> colGender;
    @FXML
    private TableColumn<Patient, String> colContact;
    @FXML
    private TableColumn<Patient, String> colAddress;

    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private DatePicker dpDob;
    @FXML
    private ComboBox<String> cbGender;
    @FXML
    private TextField txtContact;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtSearch;

    private final PatientService patientService = new PatientService();
    private final ObservableList<Patient> patientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Table column bindings
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getPatientId()).asObject());
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colDob.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDob()));
        colGender.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGender()));
        colContact.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getContact_number()));
        colAddress.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));

        // Gender options
        cbGender.getItems().addAll("Male", "Female", "Other");

        // Load data
        loadPatients();

        // Fill form when selecting a row
        patientTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    if (selected != null) {
                        txtFirstName.setText(selected.getFirstName());
                        txtLastName.setText(selected.getLastName());
                        dpDob.setValue(selected.getDob());
                        cbGender.setValue(selected.getGender());
                        txtContact.setText(selected.getContact_number());
                        txtAddress.setText(selected.getAddress());
                    }
                }
        );
    }

    private void loadPatients() {
        List<Patient> patients = patientService.getAllPatients();
        patientList.setAll(patients);
        patientTable.setItems(patientList);
    }

    @FXML
    private void addPatient() {
        Patient patient = new Patient(
                txtFirstName.getText(),
                txtLastName.getText(),
                dpDob.getValue(),
                cbGender.getValue(),
                txtContact.getText(),
                txtAddress.getText()
        );

        patientService.addPatient(patient);
        loadPatients();
        clearFields();
    }

    @FXML
    private void updatePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setFirstName(txtFirstName.getText());
            selected.setLastName(txtLastName.getText());
            selected.setDob(dpDob.getValue());
            selected.setGender(cbGender.getValue());
            selected.setContact_number(txtContact.getText());
            selected.setAddress(txtAddress.getText());

            patientService.updatePatient(selected);
            loadPatients();
            clearFields();
        }
    }

    @FXML
    private void deletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            patientService.deletePatient(selected.getPatientId());
            loadPatients();
            clearFields();
        }
    }

    @FXML
    private void searchPatient() {
        String lastName = txtSearch.getText();
        if (!lastName.isEmpty()) {
            List<Patient> patients = patientService.searchPatientByLastNameUsingStreams(lastName);
            patientList.setAll(patients);
        } else {
            loadPatients();
        }
    }

    @FXML
    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        dpDob.setValue(null);
        cbGender.setValue(null);
        txtContact.clear();
        txtAddress.clear();
    }
}
