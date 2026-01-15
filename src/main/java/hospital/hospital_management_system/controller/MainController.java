package hospital.hospital_management_system.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    
    @FXML private BorderPane mainPane;

    @FXML
    private void showPatients() {
         loadView("PatientView.fxml");
    }

    @FXML
    private void showDoctors() {
        loadView("DoctorView.fxml");
    }

    @FXML
    private void showDepartments() {
        loadView("DepartmentView.fxml");
    }

    @FXML
    private void showAppointments() {
        loadView("Appointment-view.fxml");
    }

    @FXML
    private void showPrescriptions() {
        loadView("PrescriptionView.fxml");
    }

    @FXML
    private void showFeedback() {
        loadView("PatientFeedbackView.fxml");
    }
    
    @FXML
    private void showInventory() {
        loadView("MedicalInventoryView.fxml");
    }
    
    @FXML
    private void showReports() {
        loadView("ReportsView.fxml");
    }
    
    @FXML
    private void showPrescriptionItems() {
        loadView("PrescriptionItemView.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hospital/hospital_management_system/" + fxmlFile));
            Parent view = loader.load();
            if (mainPane != null) {
                mainPane.setCenter(view);
            } else {
                System.err.println("mainPane is null!");
            }
        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlFile);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
