package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.PatientDAO;
import hospital.hospital_management_system.model.Patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientService {

    private final PatientDAO patientDAO = new PatientDAO();
    private final Map<Long, Patient> patientCache = new HashMap<>();

    public List<Patient> searchPatientByLastName(String lastName) {
        List<Patient> patients = patientDAO.searchPatientByLastName(lastName);
        for (Patient p : patients) {
            patientCache.put(p.getPatientId(), p);
        }
        return patients;
    }

    public Patient getPatientById(long patientId) {
        if (patientCache.containsKey(patientId)) {
            return patientCache.get(patientId);
        }
        Patient patient = patientDAO.searchPatientById(patientId);
        if (patient != null) {
            patientCache.put(patientId, patient);
        }
        return patient;
    }

    public void addPatient(Patient patient) {
        patientDAO.addPatient(patient);
        clearCache(); // DB generates ID → reload later
    }

    public void updatePatient(Patient patient) {
        patientDAO.updatePatient(patient);
        patientCache.put(patient.getPatientId(), patient);
    }

    public void deletePatient(long patientId) {
        patientDAO.deletePatient(patientId);
        patientCache.remove(patientId);
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient p : patients) {
            patientCache.put(p.getPatientId(), p);
        }
        return patients;
    }

    public List<Patient> searchPatientByLastNameUsingStreams(String lastName) {
        return searchPatientByLastName(lastName);
    }

    public List<Patient> sortPatientsByLastName(List<Patient> patients) {
        patients.sort((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()));
        return patients;
    }

    public void clearCache() {
        patientCache.clear();
    }

    public List<Patient> getPatientsPaginated(int limit, int offset) {
        List<Patient> patients = patientDAO.getPatientsPaginated(limit, offset);
        for (Patient p : patients) {
            patientCache.put(p.getPatientId(), p);
        }
        return patients;
    }

    public int getTotalPatientsCount() {
        return patientDAO.getTotalPatientsCount();
    }

    /**
     * Check if contact number is unique across both patients and doctors
     * @param contactNumber The contact number to check
     * @param excludePatientId Patient ID to exclude from check (for updates, pass 0 if not needed)
     * @return true if contact number is unique, false if it exists in either table
     */
    public boolean isContactNumberUnique(String contactNumber, long excludePatientId) {
        // Check if contact exists in patients (excluding current patient if updating)
        if (patientDAO.contactExistsInPatients(contactNumber, excludePatientId)) {
            return false;
        }
        // Check if contact exists in doctors
        if (patientDAO.contactExistsInDoctors(contactNumber)) {
            return false;
        }
        return true;
    }
}
