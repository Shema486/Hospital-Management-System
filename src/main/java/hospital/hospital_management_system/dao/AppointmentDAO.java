package hospital.hospital_management_system.dao;

import hospital.hospital_management_system.model.Appointment;
import hospital.hospital_management_system.model.Doctor;
import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {


    public void addAppointment(Appointment appointment) {

        String sql = """
            INSERT INTO appointments (patient_id, doctor_id, appointment_date, status, reason)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (appointment.getPatientId() != null) {
                ps.setLong(1, appointment.getPatientId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            if (appointment.getDoctorId() != null) {
                ps.setLong(2, appointment.getDoctorId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            ps.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDate()));
            ps.setString(4, appointment.getStatus());
            ps.setString(5, appointment.getReason());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Appointment findById(Long appointmentId) {

        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, appointmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Appointment> findByPatient(Long patientId) {

        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapRowToAppointment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<Appointment> findByDoctor(Long doctorId) {

        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapRowToAppointment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public List<Appointment> findAll() {

        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                appointments.add(mapRowToAppointment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public void updateAppointment(Appointment appointment) {

        String sql = """
            UPDATE appointments
            SET patient_id = ?, doctor_id = ?, appointment_date = ?, status = ?, reason = ?
            WHERE appointment_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (appointment.getPatientId() != null) {
                ps.setLong(1, appointment.getPatientId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            if (appointment.getDoctorId() != null) {
                ps.setLong(2, appointment.getDoctorId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            ps.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDate()));
            ps.setString(4, appointment.getStatus());
            ps.setString(5, appointment.getReason());
            ps.setLong(6, appointment.getAppointmentId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAppointment(Long appointmentId) {

        String sql = "DELETE FROM appointments WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, appointmentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(Long appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, appointmentId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Long appointmentId) {
        deleteAppointment(appointmentId);
    }

    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {

        Long patientId = rs.getLong("patient_id");
        Patient patient = rs.wasNull() ? null : new Patient(patientId);

        Long doctorId = rs.getLong("doctor_id");
        Doctor doctor = rs.wasNull() ? null : new Doctor(doctorId);

        return new Appointment(
                rs.getLong("appointment_id"),
                patient,
                doctor,
                rs.getTimestamp("appointment_date").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("reason")
        );
    }
}
