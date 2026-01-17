package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Department;
import hospital.hospital_management_system.services.DepartmentService;
import hospital.hospital_management_system.services.DoctorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class DepartmentController {

    @FXML private TableView<Department> departmentTable;
    @FXML private TableColumn<Department, Long> colId;
    @FXML private TableColumn<Department, String> colName;
    @FXML private TableColumn<Department, Integer> colFloor;

    @FXML private TextField txtName;
    @FXML private TextField txtFloor;

    private final DepartmentService departmentService = new DepartmentService();
    private final DoctorService doctorService = new DoctorService();
    private final ObservableList<Department> departmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getDeptId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDeptName()));
        colFloor.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getLocationFloor()).asObject());

        loadDepartments();

        departmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtName.setText(selected.getDeptName());
                txtFloor.setText(String.valueOf(selected.getLocationFloor()));
            }
        });
    }

    private void loadDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        departmentList.setAll(departments);
        departmentTable.setItems(departmentList);
    }

    @FXML
    private void addDepartment() {
        // Validate department name
        String name = txtName.getText().trim();
        if (name == null || name.isEmpty()) {
            showWarning("Validation Error", "Department name is required. Please enter a department name.");
            return;
        }

        // Validate floor
        String floorText = txtFloor.getText().trim();
        if (floorText == null || floorText.isEmpty()) {
            showWarning("Validation Error", "Floor number is required. Please enter the floor number.");
            return;
        }

        int floor;
        try {
            floor = Integer.parseInt(floorText);
        } catch (NumberFormatException e) {
            showWarning("Validation Error", "Invalid floor format. Please enter a valid number.");
            return;
        }

        if (floor < 0) {
            showWarning("Validation Error", "Floor number cannot be negative. Please enter a value greater than or equal to 0.");
            return;
        }

        Department dept = new Department(name, floor);
        departmentService.addDepartment(dept);
        loadDepartments();
        clearFields();
    }

    @FXML
    private void updateDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select a department to update.");
            return;
        }

        // Validate department name
        String name = txtName.getText().trim();
        if (name == null || name.isEmpty()) {
            showWarning("Validation Error", "Department name is required. Please enter a department name.");
            return;
        }

        // Validate floor
        String floorText = txtFloor.getText().trim();
        if (floorText == null || floorText.isEmpty()) {
            showWarning("Validation Error", "Floor number is required. Please enter the floor number.");
            return;
        }

        int floor;
        try {
            floor = Integer.parseInt(floorText);
        } catch (NumberFormatException e) {
            showWarning("Validation Error", "Invalid floor format. Please enter a valid number.");
            return;
        }

        if (floor < 0) {
            showWarning("Validation Error", "Floor number cannot be negative. Please enter a value greater than or equal to 0.");
            return;
        }

        selected.setDeptName(name);
        selected.setLocationFloor(floor);
        departmentService.updateDepartment(selected);
        loadDepartments();
        clearFields();
    }

    @FXML
    private void deleteDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select a department to delete.");
            return;
        }

        // Check if department has doctors (prevent deletion to maintain data integrity)
        try {
            var doctorsInDept = doctorService.getAllDoctors().stream()
                    .filter(doctor -> doctor.getDepartment() != null && 
                            doctor.getDepartment().getDeptId() == selected.getDeptId())
                    .toList();

            if (!doctorsInDept.isEmpty()) {
                showWarning("Cannot Delete Department", 
                    "This department cannot be deleted because it has " + doctorsInDept.size() + 
                    " doctor(s) assigned. Please reassign or remove the doctors first before deleting the department.");
                return;
            }
        } catch (Exception e) {
            // If checking fails, continue with deletion attempt (will fail at database level with proper error)
        }

        try {
            departmentService.deleteDepartment(selected.getDeptId());
            loadDepartments();
            clearFields();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                showWarning("Cannot Delete Department", 
                    "This department cannot be deleted because it is referenced by other records.");
            } else {
                showWarning("Error", "Failed to delete department: " + (errorMessage != null ? errorMessage : "Unknown error"));
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtFloor.clear();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
