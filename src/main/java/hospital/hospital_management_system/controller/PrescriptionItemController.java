package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.*;
import hospital.hospital_management_system.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrescriptionItemController {

    @FXML private TableView<PrescriptionItems> itemTable;
    @FXML private TableColumn<PrescriptionItems, String> colMedicine;
    @FXML private TableColumn<PrescriptionItems, String> colDosage;
    @FXML private TableColumn<PrescriptionItems, Integer> colQuantity;

    @FXML private ComboBox<Prescriptions> cbPrescription;
    @FXML private ComboBox<MedicalInventory> cbMedicine;
    @FXML private TextField txtDosage;
    @FXML private Spinner<Integer> spinQuantity;

    private final PrescriptionItemService itemService = new PrescriptionItemService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final MedicalInventoryService inventoryService = new MedicalInventoryService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final ObservableList<PrescriptionItems> itemList = FXCollections.observableArrayList();
    // Simple lookup to avoid repeated DB calls in cell renderers.
    private final Map<Long, Appointment> appointmentById = new HashMap<>();

    @FXML
    public void initialize() {
        colMedicine.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getItem() != null ? data.getValue().getItem().getItemName() : "N/A"));
        colDosage.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDosageInstruction()));
        colQuantity.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantityDispensed()).asObject());

        spinQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        loadPrescriptions();
        loadMedicines();

        cbPrescription.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) loadItems();
        });

        itemTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                cbMedicine.setValue(selected.getItem());
                txtDosage.setText(selected.getDosageInstruction());
                spinQuantity.getValueFactory().setValue(selected.getQuantityDispensed());
            }
        });
    }

    private void loadPrescriptions() {
        List<Prescriptions> prescriptions = prescriptionService.getAllPrescriptions();
        appointmentById.clear();
        for (Appointment appointment : appointmentService.getAll()) {
            appointmentById.put(appointment.getAppointmentId(), appointment);
        }
        cbPrescription.setItems(FXCollections.observableArrayList(prescriptions));
        cbPrescription.setCellFactory(lv -> new ListCell<Prescriptions>() {
            @Override
            protected void updateItem(Prescriptions item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Appointment app = appointmentById.get(item.getAppointmentId());
                    setText(getPrescriptionLabel(item, app));
                }
            }
        });
        cbPrescription.setButtonCell(new ListCell<Prescriptions>() {
            @Override
            protected void updateItem(Prescriptions item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Appointment app = appointmentById.get(item.getAppointmentId());
                    setText(getPrescriptionLabel(item, app));
                }
            }
        });
    }

    private void loadMedicines() {
        List<MedicalInventory> medicines = inventoryService.getAllInventoryItems();
        cbMedicine.setItems(FXCollections.observableArrayList(medicines));
        cbMedicine.setCellFactory(lv -> new ListCell<MedicalInventory>() {
            @Override
            protected void updateItem(MedicalInventory item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getItemName() + " (Stock: " + item.getStockQuantity() + ")");
            }
        });
        cbMedicine.setButtonCell(new ListCell<MedicalInventory>() {
            @Override
            protected void updateItem(MedicalInventory item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getItemName());
            }
        });
    }

    private void loadItems() {
        if (cbPrescription.getValue() != null) {
            List<PrescriptionItems> items = itemService.getItemsByPrescription(cbPrescription.getValue().getPrescriptionId());
            itemList.setAll(items);
            itemTable.setItems(itemList);
        }
    }

    @FXML
    private void addItem() {
        if (!isFormValid()) {
            return;
        }
        MedicalInventory medicine = cbMedicine.getValue();
        int requested = spinQuantity.getValue();
        if (medicine.getStockQuantity() < requested) {
            showWarning("Insufficient Stock", "Not enough stock for this item.");
            return;
        }
        PrescriptionItems item = new PrescriptionItems(
            cbPrescription.getValue().getPrescriptionId(),
            medicine.getItemId(),
            txtDosage.getText(),
            requested
        );
        itemService.addPrescriptionItem(item);

        updateInventoryStock(medicine, -requested);
        loadItems();
        loadMedicines();
        clearFields();
    }

    @FXML
    private void deleteItem() {
        PrescriptionItems selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            MedicalInventory medicine = selected.getItem();
            updateInventoryStock(medicine, selected.getQuantityDispensed());
            itemService.deleteItem(selected.getPrescriptionId(), selected.getItemId());
            loadItems();
            loadMedicines();
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        cbMedicine.setValue(null);
        txtDosage.clear();
        spinQuantity.getValueFactory().setValue(1);
    }

    private boolean isFormValid() {
        if (cbPrescription.getValue() == null) {
            showWarning("Missing Data", "Please select a prescription.");
            return false;
        }
        if (cbMedicine.getValue() == null) {
            showWarning("Missing Data", "Please select a medicine.");
            return false;
        }
        if (txtDosage.getText().isEmpty()) {
            showWarning("Missing Data", "Please enter dosage instructions.");
            return false;
        }
        return true;
    }

    private void updateInventoryStock(MedicalInventory medicine, int change) {
        int newStock = medicine.getStockQuantity() + change;
        medicine.setStockQuantity(newStock);
        inventoryService.updateInventoryItem(medicine);
    }

    private String getPrescriptionLabel(Prescriptions prescription, Appointment appointment) {
        if (appointment == null) {
            return "Prescription #" + prescription.getPrescriptionId();
        }
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        String patientName = patient == null ? "Unknown Patient" : patient.getFirstName() + " " + patient.getLastName();
        String doctorName = doctor == null ? "Unknown Doctor" : "Dr. " + doctor.getLastName();
        return patientName + " - " + doctorName;
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
