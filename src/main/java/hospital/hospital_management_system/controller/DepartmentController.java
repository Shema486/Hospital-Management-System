package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Department;
import hospital.hospital_management_system.services.DepartmentService;
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
        Department dept = new Department(txtName.getText(), Integer.parseInt(txtFloor.getText()));
        departmentService.addDepartment(dept);
        loadDepartments();
        clearFields();
    }

    @FXML
    private void updateDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setDeptName(txtName.getText());
            selected.setLocationFloor(Integer.parseInt(txtFloor.getText()));
            departmentService.updateDepartment(selected);
            loadDepartments();
            clearFields();
        }
    }

    @FXML
    private void deleteDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            departmentService.deleteDepartment(selected.getDeptId());
            loadDepartments();
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtFloor.clear();
    }
}
