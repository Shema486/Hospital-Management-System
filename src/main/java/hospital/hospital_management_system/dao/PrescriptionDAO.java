package hospital.hospital_management_system.dao;


import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.model.Prescriptions;
import hospital.hospital_management_system.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAO {


    public void addPrescription(Prescriptions prescription) {
        String sql = "INSERT INTO prescriptions (patient_id, date_issued, notes) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (prescription.getPatient() != null) {
                ps.setLong(1, prescription.getPatient().getPatientId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setTimestamp(2, Timestamp.valueOf(prescription.getPrescriptionDate()));
            ps.setString(3, prescription.getNotes());

            ps.executeUpdate();
            System.out.println("Prescription added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    // READ BY PATIENT
    public List<Prescriptions> findByPatient(Long patientId) {
        List<Prescriptions> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

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

    public List<Prescriptions> findAll() {
        List<Prescriptions> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions";

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

    // DELETE
    public void deletePrescription(Long prescriptionId) {
        String sql = "DELETE FROM prescriptions WHERE prescription_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, prescriptionId);
            ps.executeUpdate();
            System.out.println("Prescription deleted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Prescriptions mapRowToPrescription(ResultSet rs) throws SQLException {
        Long patientId = rs.getLong("patient_id");
        Patient patient = !rs.wasNull() ? new Patient(patientId) : null;

        return new Prescriptions(
                rs.getLong("prescription_id"),
                patient,
                rs.getTimestamp("date_issued").toLocalDateTime(),
                rs.getString("notes")
        );
    }
}

