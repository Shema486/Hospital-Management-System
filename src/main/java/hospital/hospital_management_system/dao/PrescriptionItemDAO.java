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

            if (item.getPrescription() != null) {
                ps.setLong(1, item.getPrescriptionId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }
            if (item.getItem() != null) {
                ps.setLong(2, item.getItemId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
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
            SELECT pi.prescription_id, pi.item_id, pi.dosage_instruction, pi.quantity_dispensed,
                   mi.item_name, mi.stock_quantity, mi.unit_price
            FROM prescription_items pi
            INNER JOIN medical_inventory mi ON pi.item_id = mi.item_id
            WHERE pi.prescription_id = ?
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

    /**
     * Check if medical inventory item is used in any prescription_items
     * @param itemId The inventory item ID to check
     * @return true if item is used, false otherwise
     */
    public boolean isItemUsedInPrescriptions(Long itemId) {
        String sql = "SELECT COUNT(*) FROM prescription_items WHERE item_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mapper
    private PrescriptionItems mapRow(ResultSet rs) throws SQLException {

        Prescriptions prescription = new Prescriptions();
        prescription.setPrescriptionId(rs.getLong("prescription_id"));

        // Load full MedicalInventory details from joined table
        MedicalInventory item = new MedicalInventory();
        item.setItemId(rs.getLong("item_id"));
        item.setItemName(rs.getString("item_name"));
        item.setStockQuantity(rs.getInt("stock_quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));

        return new PrescriptionItems(
                prescription,
                item,
                rs.getString("dosage_instruction"),
                rs.getInt("quantity_dispensed")
        );
    }
}
