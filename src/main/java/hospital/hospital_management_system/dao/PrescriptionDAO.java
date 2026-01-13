package hospital.hospital_management_system.dao;

import hospital.hospital_management_system.model.Prescriptions;
import hospital.hospital_management_system.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Prescription entity
 * Fully normalized (Prescription → Appointment)
 */
public class PrescriptionDAO {


    public boolean addPrescription(Prescriptions prescription) {

        String sql = """
            INSERT INTO prescriptions (appointment_id, date_issued, notes)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, prescription.getAppointmentId());
            ps.setTimestamp(2, Timestamp.valueOf(prescription.getPrescriptionDate()));
            ps.setString(3, prescription.getNotes());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // READ BY ID
    public Prescriptions findById(Long prescriptionId) {

        String sql = "SELECT * FROM prescriptions WHERE prescription_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, prescriptionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPrescription(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // READ BY APPOINTMENT
    public List<Prescriptions> findByAppointmentId(Long appointmentId) {

        List<Prescriptions> prescriptions = new ArrayList<>();

        String sql = "SELECT * FROM prescriptions WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, appointmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prescriptions.add(mapRowToPrescription(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prescriptions;
    }

    // READ ALL
    public List<Prescriptions> findAll() {

        List<Prescriptions> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions ORDER BY date_issued DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prescriptions.add(mapRowToPrescription(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prescriptions;
    }


    // UPDATE (NOTES)
    public boolean updatePrescriptionNotes(Long prescriptionId, String notes) {

        String sql = """
            UPDATE prescriptions
            SET notes = ?
            WHERE prescription_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notes);
            ps.setLong(2, prescriptionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean deletePrescription(Long prescriptionId) {

        String sql = "DELETE FROM prescriptions WHERE prescription_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, prescriptionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //  Mapper
    private Prescriptions mapRowToPrescription(ResultSet rs) throws SQLException {

        return new Prescriptions(
                rs.getLong("prescription_id"),
                rs.getLong("appointment_id"),
                rs.getTimestamp("date_issued").toLocalDateTime(),
                rs.getString("notes")
        );
    }
}
