package hospital.hospital_management_system.dao;

import hospital.hospital_management_system.model.Department;
import hospital.hospital_management_system.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public void addDepartment(Department department) {

        String sql = "INSERT INTO departments (dept_name, location_floor) VALUES(?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department.getDeptName());
            ps.setInt(2, department.getLocationFloor());
            ps.executeUpdate();
            System.out.println("Department added successfully: " + department.getDeptName());


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public List<Department> getAllDepartments(){
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments;";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    departments.add(mapToDepartment(rs));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return departments;
    }
    public Department mapToDepartment(ResultSet rs)throws SQLException{
        return new Department(
                rs.getLong("dept_id"),
                rs.getString("dept_name"),
                rs.getInt("location_floor")
        );
    }
}
