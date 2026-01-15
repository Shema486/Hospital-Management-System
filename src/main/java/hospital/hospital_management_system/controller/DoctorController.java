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

    private final DoctorService doctorService = new DoctorService();
    private final DepartmentService departmentService = new DepartmentService();
    private final ObservableList<Doctor> doctorList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getDoctorId()).asObject());
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colSpecialization.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSpecialization()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        loadDepartments();
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

    private void loadDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        cbDepartment.setItems(FXCollections.observableArrayList(departments));
    }

    private void loadDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        doctorList.setAll(doctors);
        doctorTable.setItems(doctorList);
    }

    @FXML
    private void addDoctor() {
        Doctor doctor = new Doctor(txtFirstName.getText(), txtLastName.getText(), 
            txtEmail.getText(), txtSpecialization.getText(), cbDepartment.getValue(), txtPhone.getText());
        doctorService.addDoctor(doctor);
        loadDoctors();
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
            loadDoctors();
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
            List<Doctor> doctors = doctorService.findDoctorsBySpecialization(specialization);
            doctorList.setAll(doctors);
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
