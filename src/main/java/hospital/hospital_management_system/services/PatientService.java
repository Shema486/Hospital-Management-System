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
}
