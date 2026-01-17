package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.DepartmentDAO;
import hospital.hospital_management_system.dao.DoctorDAO;
import hospital.hospital_management_system.model.Doctor;

import java.util.*;

public class DoctorService {
    private DoctorDAO doctorDAO = new DoctorDAO();
    private final Map<Long, Doctor> doctorCache = new HashMap<>();
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    public void addDoctor(Doctor doctor){
        doctorDAO.addDoctor(doctor);
        clearCache();
    }

    public void updateDoctor(Doctor doctor){
        doctorDAO.updateDoctor(doctor);
        doctorCache.put(doctor.getDoctorId(),doctor);
    }

    public List<Doctor> findDoctorsBySpecialization(String specialization){
        List<Doctor> doctors = doctorDAO.findDoctorsBySpecialization(specialization);
        for(Doctor d: doctors){
            doctorCache.put(d.getDoctorId(),d);
        }
     return  doctors;
    }
    public List<Doctor> getAllDoctors(){
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor d: doctors){
            doctorCache.put(d.getDoctorId(),d);
        }
        return doctors;

    }
    public void clearCache() {
        doctorCache.clear();
    }
    
    public void deleteDoctor(Long doctorId) {
        doctorDAO.deleteDoctor(doctorId);
        doctorCache.remove(doctorId);
    }

    /**
     * Sort doctors by last name (A to Z)
     */
    public List<Doctor> sortDoctorsByLastName(List<Doctor> doctors) {
        List<Doctor> sortedList = new ArrayList<>(doctors);
        
        Collections.sort(sortedList, (d1, d2) -> d1.getLastName().compareToIgnoreCase(d2.getLastName()));
        
        return sortedList;
    }


    /**
     * Get paginated list of doctors
     */
    public List<Doctor> getDoctorsPaginated(int limit, int offset) {
        List<Doctor> doctors = doctorDAO.getDoctorsPaginated(limit, offset);
        for (Doctor d : doctors) {
            doctorCache.put(d.getDoctorId(), d);
        }
        return doctors;
    }

    /**
     * Get total count of doctors
     */
    public int getTotalDoctorsCount() {
        return doctorDAO.getTotalDoctorsCount();
    }

    /**
     * Check if email is unique in doctors table
     * @param email The email to check
     * @param excludeDoctorId Doctor ID to exclude from check (for updates, pass 0 if not needed)
     * @return true if email is unique, false if it exists
     */
    public boolean isEmailUnique(String email, long excludeDoctorId) {
        return !doctorDAO.emailExists(email, excludeDoctorId);
    }

    /**
     * Check if contact number is unique across both patients and doctors
     * @param contactNumber The contact number to check
     * @param excludeDoctorId Doctor ID to exclude from check (for updates, pass 0 if not needed)
     * @return true if contact number is unique, false if it exists in either table
     */
    public boolean isContactNumberUnique(String contactNumber, long excludeDoctorId) {
        // Check if contact exists in doctors (excluding current doctor if updating)
        if (doctorDAO.contactExistsInDoctors(contactNumber, excludeDoctorId)) {
            return false;
        }
        // Check if contact exists in patients
        if (doctorDAO.contactExistsInPatients(contactNumber)) {
            return false;
        }
        return true;
    }
}
