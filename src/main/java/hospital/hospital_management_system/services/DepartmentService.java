package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.DepartmentDAO;
import hospital.hospital_management_system.model.Department;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentService {
    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private Map<Long, Department> departmentCache = new HashMap<>();

    public void addDepartment(Department department) {
        departmentDAO.addDepartment(department);
        if (department.getDeptId() != null) {
            departmentCache.put(department.getDeptId(), department);
        }
    }
    
    public void deleteDepartment(Long deptId) {
        departmentDAO.deleteDepartment(deptId);
        departmentCache.remove(deptId);
    }

    public Department getDepartmentById(Long id) {
        if (departmentCache.containsKey(id)) {
            return departmentCache.get(id);
        }
        Department dept = departmentDAO.getDepartmentById(id);
        if (dept != null) departmentCache.put(dept.getDeptId(), dept);
        return dept;
    }

    public List<Department> getAllDepartments() {
        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department dept : departments) {
            departmentCache.put(dept.getDeptId(), dept);
        }
        return departments;
    }

    public void updateDepartment(Department dept) {
        departmentDAO.updateDepartment(dept);
        departmentCache.put(dept.getDeptId(), dept);
    }
    public  void clearCache(){
        departmentCache.clear();
    }
}
