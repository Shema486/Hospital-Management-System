package hospital.hospital_management_system.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInitializer {

    public void seedIfEmpty() {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (isTableEmpty(conn, "departments")) {
                    insertDepartments(conn);
                }
                if (isTableEmpty(conn, "doctors")) {
                    insertDoctors(conn);
                }
                if (isTableEmpty(conn, "patients")) {
                    insertPatients(conn);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Warning: failed to seed initial data: " + e.getMessage());
        }
    }

    private boolean isTableEmpty(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getLong(1) == 0;
        }
    }

    private void insertDepartments(Connection conn) throws SQLException {
        String sql = """
            INSERT INTO departments (dept_name, location_floor) VALUES
            ('Cardiology', 2),
            ('Neurology', 3),
            ('Pediatrics', 1),
            ('Orthopedics', 2),
            ('Emergency', 1)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private void insertDoctors(Connection conn) throws SQLException {
        Long cardiologyId = getDepartmentId(conn, "Cardiology");
        Long neurologyId = getDepartmentId(conn, "Neurology");
        Long pediatricsId = getDepartmentId(conn, "Pediatrics");

        String sql = """
            INSERT INTO doctors (first_name, last_name, specialization, dept_id, email, phone)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            addDoctor(ps, "John", "Smith", "Cardiologist", cardiologyId, "john.smith@hospital.com", "555-0101");
            addDoctor(ps, "Sarah", "Johnson", "Neurologist", neurologyId, "sarah.j@hospital.com", "555-0102");
            addDoctor(ps, "Mike", "Williams", "Pediatrician", pediatricsId, "mike.w@hospital.com", "555-0103");
        }
    }

    private void addDoctor(PreparedStatement ps, String firstName, String lastName,
                           String specialization, Long deptId, String email, String phone) throws SQLException {
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setString(3, specialization);
        if (deptId != null) {
            ps.setLong(4, deptId);
        } else {
            ps.setNull(4, java.sql.Types.BIGINT);
        }
        ps.setString(5, email);
        ps.setString(6, phone);
        ps.executeUpdate();
    }

    private void insertPatients(Connection conn) throws SQLException {
        String sql = """
            INSERT INTO patients (first_name, last_name, dob, gender, contact_number, address)
            VALUES
            ('Alice', 'Brown', '1990-05-15', 'Female', '555-1001', '123 Main St'),
            ('Bob', 'Davis', '1985-08-22', 'Male', '555-1002', '456 Oak Ave'),
            ('Carol', 'Wilson', '1992-12-10', 'Female', '555-1003', '789 Elm St')
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private Long getDepartmentId(Connection conn, String name) throws SQLException {
        String sql = "SELECT dept_id FROM departments WHERE dept_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("dept_id");
                }
            }
        }
        return null;
    }
}
