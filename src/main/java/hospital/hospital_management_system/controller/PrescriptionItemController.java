package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.*;
import hospital.hospital_management_system.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

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
        cbPrescription.setItems(FXCollections.observableArrayList(prescriptions));
        cbPrescription.setCellFactory(lv -> new ListCell<Prescriptions>() {
            @Override
            protected void updateItem(Prescriptions item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Appointment app = appointmentService.getAll().stream()
                        .filter(a -> a.getAppointmentId().equals(item.getAppointmentId()))
                        .findFirst().orElse(null);
                    if (app != null) {
                        setText(app.getPatient().getFirstName() + " " + app.getPatient().getLastName() + 
                               " - Dr. " + app.getDoctor().getLastName());
                    } else {
                        setText("Prescription #" + item.getPrescriptionId());
                    }
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
                    Appointment app = appointmentService.getAll().stream()
                        .filter(a -> a.getAppointmentId().equals(item.getAppointmentId()))
                        .findFirst().orElse(null);
                    if (app != null) {
                        setText(app.getPatient().getFirstName() + " " + app.getPatient().getLastName());
                    } else {
                        setText("Prescription #" + item.getPrescriptionId());
                    }
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
        if (cbPrescription.getValue() != null && cbMedicine.getValue() != null && !txtDosage.getText().isEmpty()) {
            PrescriptionItems item = new PrescriptionItems(
                cbPrescription.getValue().getPrescriptionId(),
                cbMedicine.getValue().getItemId(),
                txtDosage.getText(),
                spinQuantity.getValue()
            );
            itemService.addPrescriptionItem(item);
            
            // Update inventory stock
            MedicalInventory medicine = cbMedicine.getValue();
            int newStock = medicine.getStockQuantity() - spinQuantity.getValue();
            if (newStock >= 0) {
                medicine.setStockQuantity(newStock);
                inventoryService.updateInventoryItem(medicine);
            }
            
            loadItems();
            loadMedicines();
            clearFields();
        }
    }

    @FXML
    private void deleteItem() {
        PrescriptionItems selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Return stock to inventory
            MedicalInventory medicine = selected.getItem();
            medicine.setStockQuantity(medicine.getStockQuantity() + selected.getQuantityDispensed());
            inventoryService.updateInventoryItem(medicine);
            
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
}
