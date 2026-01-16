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
        
        Collections.sort(sortedList, new Comparator<Doctor>() {
            @Override
            public int compare(Doctor d1, Doctor d2) {
                return d1.getLastName().compareToIgnoreCase(d2.getLastName());
            }
        });
        
        return sortedList;
    }

    /**
     * Sort doctors by ID (lowest to highest)
     */
    public List<Doctor> sortDoctorsById(List<Doctor> doctors) {
        List<Doctor> sortedList = new ArrayList<>(doctors);
        
        Collections.sort(sortedList, new Comparator<Doctor>() {
            @Override
            public int compare(Doctor d1, Doctor d2) {
                return Long.compare(d1.getDoctorId(), d2.getDoctorId());
            }
        });
        
        return sortedList;
    }

    /**
     * Sort doctors by specialization (A to Z)
     */
    public List<Doctor> sortDoctorsBySpecialization(List<Doctor> doctors) {
        List<Doctor> sortedList = new ArrayList<>(doctors);
        
        Collections.sort(sortedList, new Comparator<Doctor>() {
            @Override
            public int compare(Doctor d1, Doctor d2) {
                return d1.getSpecialization().compareToIgnoreCase(d2.getSpecialization());
            }
        });
        
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
}
