package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.services.PatientService;
import hospital.hospital_management_system.services.AppointmentService;
import hospital.hospital_management_system.services.PrescriptionService;
import hospital.hospital_management_system.model.Appointment;
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
    @FXML
    private Pagination pagination;

    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final ObservableList<Patient> patientList = FXCollections.observableArrayList();
    private static final int ITEMS_PER_PAGE = 10;
    private boolean isSearchMode = false;
    private boolean isInitializing = true;

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

        // Disable future dates in DOB DatePicker
        dpDob.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isAfter(today));
                if (date.isAfter(today)) {
                    setStyle("-fx-background-color: #ffcccc;"); // Visual indication
                }
            }
        });

        // Initialize pagination
        initializePagination();

        // Load data
        loadPatients();
        
        // Mark initialization as complete
        isInitializing = false;

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

    private void initializePagination() {
        int totalCount = patientService.getTotalPatientsCount();
        int pageCount = (int) Math.ceil((double) totalCount / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            // Only load page if not initializing and not in search mode
            if (!isInitializing && !isSearchMode) {
                loadPage(newIndex.intValue());
            }
        });
    }

    private void loadPage(int pageIndex) {
        int offset = pageIndex * ITEMS_PER_PAGE;
        List<Patient> patients = patientService.getPatientsPaginated(ITEMS_PER_PAGE, offset);
        List<Patient> sortedPatients = patientService.sortPatientsByLastName(patients);
        patientList.setAll(sortedPatients);
        patientTable.setItems(patientList);
    }

    private void loadPatients() {
        isSearchMode = false;
        int totalCount = patientService.getTotalPatientsCount();
        int pageCount = (int) Math.ceil((double) totalCount / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        loadPage(0);
        pagination.setCurrentPageIndex(0);
    }

    @FXML
    private void addPatient() {
        // Validate required fields
        String firstName = txtFirstName.getText().trim();
        if (firstName == null || firstName.isEmpty()) {
            showWarning("Validation Error", "First name is required. Please enter the patient's first name.");
            return;
        }

        String lastName = txtLastName.getText().trim();
        if (lastName == null || lastName.isEmpty()) {
            showWarning("Validation Error", "Last name is required. Please enter the patient's last name.");
            return;
        }

        if (dpDob.getValue() == null) {
            showWarning("Validation Error", "Date of birth is required. Please select the patient's date of birth.");
            return;
        }

        // Validate DOB is not in the future
        if (dpDob.getValue().isAfter(LocalDate.now())) {
            showWarning("Validation Error", "Date of birth cannot be in the future. Please select a valid date of birth.");
            return;
        }

        if (cbGender.getValue() == null || cbGender.getValue().isEmpty()) {
            showWarning("Validation Error", "Gender is required. Please select the patient's gender.");
            return;
        }

        String contact = txtContact.getText().trim();
        if (contact == null || contact.isEmpty()) {
            showWarning("Validation Error", "Contact number is required. Please enter the patient's contact number.");
            return;
        }

        // Validate contact number is unique across patients and doctors
        if (!patientService.isContactNumberUnique(contact, 0)) {
            showWarning("Validation Error", "This contact number already exists. Please use a different contact number.");
            return;
        }

        Patient patient = new Patient(
                firstName,
                lastName,
                dpDob.getValue(),
                cbGender.getValue(),
                contact,
                txtAddress.getText()
        );

        patientService.addPatient(patient);
        int currentPage = pagination.getCurrentPageIndex();
        loadPatients();
        // Try to stay on the same page if possible
        int totalCount = patientService.getTotalPatientsCount();
        int pageCount = (int) Math.ceil((double) totalCount / ITEMS_PER_PAGE);
        if (currentPage < pageCount) {
            pagination.setCurrentPageIndex(currentPage);
            loadPage(currentPage);
        }
        clearFields();
    }

    @FXML
    private void updatePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select a patient to update.");
            return;
        }

        // Validate required fields
        String firstName = txtFirstName.getText().trim();
        if (firstName == null || firstName.isEmpty()) {
            showWarning("Validation Error", "First name is required. Please enter the patient's first name.");
            return;
        }

        String lastName = txtLastName.getText().trim();
        if (lastName == null || lastName.isEmpty()) {
            showWarning("Validation Error", "Last name is required. Please enter the patient's last name.");
            return;
        }

        if (dpDob.getValue() == null) {
            showWarning("Validation Error", "Date of birth is required. Please select the patient's date of birth.");
            return;
        }

        // Validate DOB is not in the future
        if (dpDob.getValue().isAfter(LocalDate.now())) {
            showWarning("Validation Error", "Date of birth cannot be in the future. Please select a valid date of birth.");
            return;
        }

        if (cbGender.getValue() == null || cbGender.getValue().isEmpty()) {
            showWarning("Validation Error", "Gender is required. Please select the patient's gender.");
            return;
        }

        String contact = txtContact.getText().trim();
        if (contact == null || contact.isEmpty()) {
            showWarning("Validation Error", "Contact number is required. Please enter the patient's contact number.");
            return;
        }

        // Validate contact number is unique across patients and doctors (excluding current patient)
        if (!patientService.isContactNumberUnique(contact, selected.getPatientId())) {
            showWarning("Validation Error", "This contact number already exists. Please use a different contact number.");
            return;
        }

        selected.setFirstName(firstName);
        selected.setLastName(lastName);
        selected.setDob(dpDob.getValue());
        selected.setGender(cbGender.getValue());
        selected.setContact_number(contact);
        selected.setAddress(txtAddress.getText());

        patientService.updatePatient(selected);
        int currentPage = pagination.getCurrentPageIndex();
        loadPage(currentPage);
        clearFields();
    }

    @FXML
    private void deletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select a patient to delete.");
            return;
        }

        // Check if patient has appointments with prescriptions
        // When deleting a patient, appointments are cascaded, but appointments with prescriptions cannot be deleted
        try {
            List<Appointment> patientAppointments = appointmentService.getAll().stream()
                    .filter(app -> app.getPatient() != null && app.getPatient().getPatientId() == selected.getPatientId())
                    .toList();

            for (Appointment appointment : patientAppointments) {
                if (prescriptionService.hasPrescriptionForAppointment(appointment.getAppointmentId())) {
                    showWarning("Cannot Delete Patient", 
                        "This patient cannot be deleted because they have appointments with prescriptions. " +
                        "Please delete the associated prescriptions first before deleting the patient.");
                    return;
                }
            }
        } catch (Exception e) {
            // If checking fails, continue with deletion attempt (will fail at database level with proper error)
        }

        try {
            patientService.deletePatient(selected.getPatientId());
            loadPatients();
            clearFields();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                showWarning("Cannot Delete Patient", 
                    "This patient cannot be deleted because they have appointments with prescriptions. " +
                    "Please delete the associated prescriptions first before deleting the patient.");
            } else {
                showWarning("Error", "Failed to delete patient: " + (errorMessage != null ? errorMessage : "Unknown error"));
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void searchPatient() {
        String lastName = txtSearch.getText();
        if (!lastName.isEmpty()) {
            isSearchMode = true;
            List<Patient> patients = patientService.searchPatientByLastNameUsingStreams(lastName);
            patientList.setAll(patients);
            patientTable.setItems(patientList);
            // Hide pagination during search
            pagination.setPageCount(1);
            pagination.setCurrentPageIndex(0);
        } else {
            loadPatients();
        }
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
