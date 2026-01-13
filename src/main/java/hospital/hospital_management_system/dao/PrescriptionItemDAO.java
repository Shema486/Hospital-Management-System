package hospital.hospital_management_system.dao;

import hospital.hospital_management_system.model.MedicalInventory;
import hospital.hospital_management_system.model.PrescriptionItems;
import hospital.hospital_management_system.model.Prescriptions;
import hospital.hospital_management_system.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionItemDAO {

    // CREATE
    public void addPrescriptionItem(PrescriptionItems item) {
        String sql = """
            INSERT INTO prescription_items
            (prescription_id, item_id, dosage_instruction, quantity_dispensed)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, item.getPrescriptionId());
            ps.setLong(2, item.getItemId());
            ps.setString(3, item.getDosageInstruction());
            ps.setInt(4, item.getQuantityDispensed());

            ps.executeUpdate();
            System.out.println("Prescription item added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ by Prescription
    public List<PrescriptionItems> findByPrescription(Long prescriptionId) {
        List<PrescriptionItems> items = new ArrayList<>();

        String sql = """
            SELECT * FROM prescription_items
            WHERE prescription_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, prescriptionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // DELETE (important when editing prescriptions)
    public void deleteItem(Long prescriptionId, Long itemId) {
        String sql = """
            DELETE FROM prescription_items
            WHERE prescription_id = ? AND item_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, prescriptionId);
            ps.setLong(2, itemId);
            ps.executeUpdate();

            System.out.println("Prescription item deleted");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mapper
    private PrescriptionItems mapRow(ResultSet rs) throws SQLException {

        Prescriptions prescription = new Prescriptions();
        prescription.setPrescriptionId(rs.getLong("prescription_id"));

        MedicalInventory item = new MedicalInventory();
        item.setItemId(rs.getLong("item_id"));

        return new PrescriptionItems(
                prescription,
                item,
                rs.getString("dosage_instruction"),
                rs.getInt("quantity_dispensed")
        );
    }
}
