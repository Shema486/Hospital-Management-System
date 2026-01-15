package hospital.hospital_management_system.controller;

import hospital.hospital_management_system.model.Appointment;
import hospital.hospital_management_system.model.MedicalInventory;
import hospital.hospital_management_system.services.MedicalInventoryService;
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
        MedicalInventory item = new MedicalInventory(
            txtName.getText(),
            Integer.parseInt(txtQuantity.getText()),
            new BigDecimal(txtPrice.getText())
        );
        inventoryService.addInventoryItem(item);
        loadInventory();
        clearFields();
    }

    @FXML
    private void updateItem() {
        MedicalInventory selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setItemName(txtName.getText());
            selected.setStockQuantity(Integer.parseInt(txtQuantity.getText()));
            selected.setUnitPrice(new BigDecimal(txtPrice.getText()));
            inventoryService.updateInventoryItem(selected);
            loadInventory();
            clearFields();
        }
    }

    @FXML
    private void deleteItem() {
        MedicalInventory selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            inventoryService.deleteInventoryItem(selected.getItemId());
            loadInventory();
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        txtName.clear();
        txtQuantity.clear();
        txtPrice.clear();
    }
}
