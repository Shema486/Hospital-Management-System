package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Department;
import hospital.hospital_management_system.model.Doctor;
import hospital.hospital_management_system.model.Appointment;
import hospital.hospital_management_system.services.DoctorService;
import hospital.hospital_management_system.services.DepartmentService;
import hospital.hospital_management_system.services.AppointmentService;
import hospital.hospital_management_system.services.PrescriptionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;

public class DoctorController {

    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, Long> colId;
    @FXML private TableColumn<Doctor, String> colFirstName;
    @FXML private TableColumn<Doctor, String> colLastName;
    @FXML private TableColumn<Doctor, String> colEmail;
    @FXML private TableColumn<Doctor, String> colSpecialization;
    @FXML private TableColumn<Doctor, String> colPhone;

    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtSpecialization;
    @FXML private TextField txtPhone;
    @FXML private ComboBox<Department> cbDepartment;
    @FXML private TextField txtSearch;
    @FXML private Pagination pagination;

    private final DoctorService doctorService = new DoctorService();
    private final DepartmentService departmentService = new DepartmentService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final ObservableList<Doctor> doctorList = FXCollections.observableArrayList();
    private static final int ITEMS_PER_PAGE = 10;
    private boolean isSearchMode = false;
    private boolean isInitializing = true;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getDoctorId()).asObject());
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colSpecialization.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSpecialization()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        loadDepartments();
        
        // Initialize pagination
        initializePagination();
        
        loadDoctors();
        
        // Mark initialization as complete
        isInitializing = false;

        doctorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtFirstName.setText(selected.getFirstName());
                txtLastName.setText(selected.getLastName());
                txtEmail.setText(selected.getEmail());
                txtSpecialization.setText(selected.getSpecialization());
                txtPhone.setText(selected.getPhone());
                cbDepartment.setValue(selected.getDepartment());
            }
        });
    }

    private void initializePagination() {
        int totalCount = doctorService.getTotalDoctorsCount();
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
        List<Doctor> doctors = doctorService.getDoctorsPaginated(ITEMS_PER_PAGE, offset);
        List<Doctor> sortedDoctors = doctorService.sortDoctorsByLastName(doctors);
        doctorList.setAll(sortedDoctors);
        doctorTable.setItems(doctorList);
    }

    private void loadDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        cbDepartment.setItems(FXCollections.observableArrayList(departments));
        
        // Set StringConverter to display department name instead of object representation
        cbDepartment.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department department) {
                return department == null ? "" : department.getDeptName();
            }

            @Override
            public Department fromString(String string) {
                return null;
            }
        });
    }

    private void loadDoctors() {
        isSearchMode = false;
        int totalCount = doctorService.getTotalDoctorsCount();
        int pageCount = (int) Math.ceil((double) totalCount / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        loadPage(0);
        pagination.setCurrentPageIndex(0);
    }

    @FXML
    private void addDoctor() {
        // Validate required fields
        String firstName = txtFirstName.getText().trim();
        if (firstName == null || firstName.isEmpty()) {
            showWarning("Validation Error", "First name is required. Please enter the doctor's first name.");
            return;
        }

        String lastName = txtLastName.getText().trim();
        if (lastName == null || lastName.isEmpty()) {
            showWarning("Validation Error", "Last name is required. Please enter the doctor's last name.");
            return;
        }

        String email = txtEmail.getText().trim();
        if (email == null || email.isEmpty()) {
            showWarning("Validation Error", "Email is required. Please enter the doctor's email address.");
            return;
        }

        // Validate email is unique
        if (!doctorService.isEmailUnique(email, 0)) {
            showWarning("Validation Error", "This email already exists. Please use a different email address.");
            return;
        }

        String specialization = txtSpecialization.getText().trim();
        if (specialization == null || specialization.isEmpty()) {
            showWarning("Validation Error", "Specialization is required. Please enter the doctor's specialization.");
            return;
        }

        String contact = txtPhone.getText().trim();
        if (contact == null || contact.isEmpty()) {
            showWarning("Validation Error", "Contact number is required. Please enter the doctor's contact number.");
            return;
        }

        // Validate contact number is unique across patients and doctors
        if (!doctorService.isContactNumberUnique(contact, 0)) {
            showWarning("Validation Error", "This contact number already exists. Please use a different contact number.");
            return;
        }

        Doctor doctor = new Doctor(firstName, lastName, email, specialization, cbDepartment.getValue(), contact);
        doctorService.addDoctor(doctor);
        int currentPage = pagination.getCurrentPageIndex();
        loadDoctors();
        // Try to stay on the same page if possible
        int totalCount = doctorService.getTotalDoctorsCount();
        int pageCount = (int) Math.ceil((double) totalCount / ITEMS_PER_PAGE);
        if (currentPage < pageCount) {
            pagination.setCurrentPageIndex(currentPage);
            loadPage(currentPage);
        }
        clearFields();
    }

    @FXML
    private void updateDoctor() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select a doctor to update.");
            return;
        }

        // Validate required fields
        String firstName = txtFirstName.getText().trim();
        if (firstName == null || firstName.isEmpty()) {
            showWarning("Validation Error", "First name is required. Please enter the doctor's first name.");
            return;
        }

        String lastName = txtLastName.getText().trim();
        if (lastName == null || lastName.isEmpty()) {
            showWarning("Validation Error", "Last name is required. Please enter the doctor's last name.");
            return;
        }

        String email = txtEmail.getText().trim();
        if (email == null || email.isEmpty()) {
            showWarning("Validation Error", "Email is required. Please enter the doctor's email address.");
            return;
        }

        // Validate email is unique (excluding current doctor)
        if (!doctorService.isEmailUnique(email, selected.getDoctorId())) {
            showWarning("Validation Error", "This email already exists. Please use a different email address.");
            return;
        }

        String specialization = txtSpecialization.getText().trim();
        if (specialization == null || specialization.isEmpty()) {
            showWarning("Validation Error", "Specialization is required. Please enter the doctor's specialization.");
            return;
        }

        String contact = txtPhone.getText().trim();
        if (contact == null || contact.isEmpty()) {
            showWarning("Validation Error", "Contact number is required. Please enter the doctor's contact number.");
            return;
        }

        // Validate contact number is unique across patients and doctors (excluding current doctor)
        if (!doctorService.isContactNumberUnique(contact, selected.getDoctorId())) {
            showWarning("Validation Error", "This contact number already exists. Please use a different contact number.");
            return;
        }

        selected.setFirstName(firstName);
        selected.setLastName(lastName);
        selected.setEmail(email);
        selected.setSpecialization(specialization);
        selected.setPhone(contact);
        selected.setDepartmentId(cbDepartment.getValue());
        doctorService.updateDoctor(selected);
        int currentPage = pagination.getCurrentPageIndex();
        loadPage(currentPage);
        clearFields();
    }

    @FXML
    private void deleteDoctor() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select a doctor to delete.");
            return;
        }

        // Check if doctor has appointments with prescriptions
        // When deleting a doctor, appointments are cascaded, but appointments with prescriptions cannot be deleted
        try {
            List<Appointment> doctorAppointments = appointmentService.getAll().stream()
                    .filter(app -> app.getDoctor() != null && app.getDoctor().getDoctorId() == selected.getDoctorId())
                    .toList();

            for (Appointment appointment : doctorAppointments) {
                if (prescriptionService.hasPrescriptionForAppointment(appointment.getAppointmentId())) {
                    showWarning("Cannot Delete Doctor", 
                        "This doctor cannot be deleted because they have appointments with prescriptions. " +
                        "Please delete the associated prescriptions first before deleting the doctor.");
                    return;
                }
            }
        } catch (Exception e) {
            // If checking fails, continue with deletion attempt (will fail at database level with proper error)
        }

        try {
            doctorService.deleteDoctor(selected.getDoctorId());
            loadDoctors();
            clearFields();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                showWarning("Cannot Delete Doctor", 
                    "This doctor cannot be deleted because they have appointments with prescriptions. " +
                    "Please delete the associated prescriptions first before deleting the doctor.");
            } else {
                showWarning("Error", "Failed to delete doctor: " + (errorMessage != null ? errorMessage : "Unknown error"));
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void searchDoctor() {
        String specialization = txtSearch.getText();
        if (!specialization.isEmpty()) {
            isSearchMode = true;
            List<Doctor> doctors = doctorService.findDoctorsBySpecialization(specialization);
            doctorList.setAll(doctors);
            doctorTable.setItems(doctorList);
            // Hide pagination during search
            pagination.setPageCount(1);
            pagination.setCurrentPageIndex(0);
        } else {
            loadDoctors();
        }
    }

    @FXML
    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtSpecialization.clear();
        txtPhone.clear();
        cbDepartment.setValue(null);
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
