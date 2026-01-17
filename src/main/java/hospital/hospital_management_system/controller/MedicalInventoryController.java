package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.MedicalInventory;
import hospital.hospital_management_system.services.MedicalInventoryService;
import hospital.hospital_management_system.services.PrescriptionItemService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.List;

public class MedicalInventoryController {

    @FXML private TableView<MedicalInventory> inventoryTable;
    @FXML private TableColumn<MedicalInventory, Long> colId;
    @FXML private TableColumn<MedicalInventory, String> colName;
    @FXML private TableColumn<MedicalInventory, Integer> colQuantity;
    @FXML private TableColumn<MedicalInventory, BigDecimal> colPrice;

    @FXML private TextField txtName;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtPrice;

    private final MedicalInventoryService inventoryService = new MedicalInventoryService();
    private final PrescriptionItemService prescriptionItemService = new PrescriptionItemService();
    private final ObservableList<MedicalInventory> inventoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getItemId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getItemName()));
        colQuantity.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStockQuantity()).asObject());
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getUnitPrice()));

        loadInventory();

        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                txtName.setText(selected.getItemName());
                txtQuantity.setText(String.valueOf(selected.getStockQuantity()));
                txtPrice.setText(selected.getUnitPrice().toString());
            }
        });
    }

    private void loadInventory() {
        List<MedicalInventory> items = inventoryService.getAllInventoryItems();
        inventoryList.setAll(items);
        inventoryTable.setItems(inventoryList);
    }

    @FXML
    private void addItem() {
        // Validate medicine name
        String itemName = txtName.getText().trim();
        if (itemName == null || itemName.isEmpty()) {
            showWarning("Validation Error", "Medicine name is required. Please enter a medicine name.");
            return;
        }

        // Validate medicine name is unique (case-insensitive)
        if (!inventoryService.isItemNameUnique(itemName, 0)) {
            showWarning("Validation Error", "This medicine name already exists. Please use a different name.");
            return;
        }

        // Validate quantity
        String quantityText = txtQuantity.getText().trim();
        if (quantityText == null || quantityText.isEmpty()) {
            showWarning("Validation Error", "Stock quantity is required. Please enter the quantity.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            showWarning("Validation Error", "Invalid quantity format. Please enter a valid number.");
            return;
        }

        if (quantity < 0) {
            showWarning("Validation Error", "Stock quantity cannot be negative. Please enter a value greater than or equal to 0.");
            return;
        }

        // Validate price
        String priceText = txtPrice.getText().trim();
        if (priceText == null || priceText.isEmpty()) {
            showWarning("Validation Error", "Unit price is required. Please enter the price.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            showWarning("Validation Error", "Invalid price format. Please enter a valid number.");
            return;
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            showWarning("Validation Error", "Unit price cannot be negative. Please enter a value greater than or equal to 0.");
            return;
        }

        MedicalInventory item = new MedicalInventory(
            itemName,
            quantity,
            price
        );
        inventoryService.addInventoryItem(item);
        loadInventory();
        clearFields();
    }

    @FXML
    private void updateItem() {
        MedicalInventory selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select an item to update.");
            return;
        }

        // Validate medicine name
        String itemName = txtName.getText().trim();
        if (itemName == null || itemName.isEmpty()) {
            showWarning("Validation Error", "Medicine name is required. Please enter a medicine name.");
            return;
        }

        // Validate medicine name is unique (case-insensitive, excluding current item)
        if (!inventoryService.isItemNameUnique(itemName, selected.getItemId())) {
            showWarning("Validation Error", "This medicine name already exists. Please use a different name.");
            return;
        }

        // Validate quantity
        String quantityText = txtQuantity.getText().trim();
        if (quantityText == null || quantityText.isEmpty()) {
            showWarning("Validation Error", "Stock quantity is required. Please enter the quantity.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            showWarning("Validation Error", "Invalid quantity format. Please enter a valid number.");
            return;
        }

        if (quantity < 0) {
            showWarning("Validation Error", "Stock quantity cannot be negative. Please enter a value greater than or equal to 0.");
            return;
        }

        // Validate price
        String priceText = txtPrice.getText().trim();
        if (priceText == null || priceText.isEmpty()) {
            showWarning("Validation Error", "Unit price is required. Please enter the price.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            showWarning("Validation Error", "Invalid price format. Please enter a valid number.");
            return;
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            showWarning("Validation Error", "Unit price cannot be negative. Please enter a value greater than or equal to 0.");
            return;
        }

        selected.setItemName(itemName);
        selected.setStockQuantity(quantity);
        selected.setUnitPrice(price);
        inventoryService.updateInventoryItem(selected);
        loadInventory();
        clearFields();
    }

    @FXML
    private void deleteItem() {
        MedicalInventory selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selection Error", "Please select an item to delete.");
            return;
        }

        // Check if item is used in prescription_items (cannot delete if used)
        if (prescriptionItemService.isItemUsedInPrescriptions(selected.getItemId())) {
            showWarning("Cannot Delete Medicine", 
                "This medicine cannot be deleted because it is used in prescription items. " +
                "Please remove it from all prescriptions first before deleting.");
            return;
        }

        try {
            inventoryService.deleteInventoryItem(selected.getItemId());
            loadInventory();
            clearFields();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                showWarning("Cannot Delete Medicine", 
                    "This medicine cannot be deleted because it is used in prescription items. " +
                    "Please remove it from all prescriptions first before deleting.");
            } else {
                showWarning("Error", "Failed to delete medicine: " + (errorMessage != null ? errorMessage : "Unknown error"));
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtQuantity.clear();
        txtPrice.clear();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
