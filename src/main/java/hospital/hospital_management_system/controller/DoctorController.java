package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Department;
import hospital.hospital_management_system.model.Doctor;
import hospital.hospital_management_system.services.DoctorService;
import hospital.hospital_management_system.services.DepartmentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private final ObservableList<Doctor> doctorList = FXCollections.observableArrayList();
    private static final int ITEMS_PER_PAGE = 10;
    private boolean isSearchMode = false;

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
            if (!isSearchMode) {
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
        Doctor doctor = new Doctor(txtFirstName.getText(), txtLastName.getText(), 
            txtEmail.getText(), txtSpecialization.getText(), cbDepartment.getValue(), txtPhone.getText());
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
        if (selected != null) {
            selected.setFirstName(txtFirstName.getText());
            selected.setLastName(txtLastName.getText());
            selected.setEmail(txtEmail.getText());
            selected.setSpecialization(txtSpecialization.getText());
            selected.setPhone(txtPhone.getText());
            selected.setDepartmentId(cbDepartment.getValue());
            doctorService.updateDoctor(selected);
            int currentPage = pagination.getCurrentPageIndex();
            loadPage(currentPage);
            clearFields();
        }
    }

    @FXML
    private void deleteDoctor() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            doctorService.deleteDoctor(selected.getDoctorId());
            loadDoctors();
            clearFields();
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
}
