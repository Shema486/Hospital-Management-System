package hospital.hospital_management_system.dao;

import hospital.hospital_management_system.model.Department;
import hospital.hospital_management_system.model.Doctor;
import hospital.hospital_management_system.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public void addDoctor(Doctor doctor){
        String sql = "INSERT INTO doctors (first_name, last_name, email, specialization, phone, dept_id) VALUES(?,?,?,?,?,?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,doctor.getFirstName());
            ps.setString(2,doctor.getLastName());
            ps.setString(3,doctor.getEmail());
            ps.setString(4, doctor.getSpecialization());
            ps.setString(5, doctor.getPhone());

            if (doctor.getDepartment() != null) {
                ps.setLong(6, doctor.getDepartment().getDeptId());
            } else {
                ps.setNull(6, java.sql.Types.BIGINT);
            }

            ps.executeUpdate();
            System.out.println("Doctor added successfully: " + doctor.getDoctorId());


        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public Doctor findById (long doctorId){
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1,doctorId);
                try(ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                return mapRowToDoctor(rs);

            }
                }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public List<Doctor> findDoctorsBySpecialization(String specialization){
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization ILIKE ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,"%" + specialization + "%");
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                     doctors.add(mapRowToDoctor(rs));
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return doctors;
    }
    public List<Doctor> getAllPatients(){
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while (rs.next()){
                doctors.add(mapRowToDoctor(rs));

            }

        }catch (SQLException e ){
            e.printStackTrace();
        }
        return doctors;
    }
    public List<Doctor> findDoctorsByDepartment(Long deptId){
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE dept_id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1,deptId);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    doctors.add(mapRowToDoctor(rs));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return doctors;
    }
    public void updateDoctor(Doctor doctor){
        String sql = "UPDATE doctors SET first_name = ?, last_name = ?, email = ?, specialization = ?, phone = ?, dept_id = ? WHERE doctor_id = ? ";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, doctor.getFirstName());
            ps.setString(2, doctor.getLastName());
            ps.setString(3, doctor.getEmail());
            ps.setString(4, doctor.getSpecialization());
            ps.setString(5, doctor.getPhone());

            if (doctor.getDepartment() != null){
                ps.setLong(6,doctor.getDepartment().getDeptId());
            }else {
                ps.setNull(6, Types.BIGINT);
            }
            ps.setLong(7,doctor.getDoctorId());

            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteDoctor(Long doctorId){
        String sql = "DELETE FROM doctors WHERE  doctor_id = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1,doctorId);
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private Doctor mapRowToDoctor(ResultSet rs) throws SQLException {
        //rs.getLong("dept_id") != 0 ? new Department(rs.getLong("dept_id")) : null,
        Long deptId = rs.getLong("dept_id");
        Department department = !rs.wasNull() ? new Department(deptId) : null;
        return new Doctor(
                rs.getLong("doctor_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("specialization"),
                department,
                rs.getString("phone")
        );
    }

}
