package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.PatientDAO;
import hospital.hospital_management_system.model.Patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientService {
    private PatientDAO patientDAO = new PatientDAO();
    private Map<Long, Patient> patientCaching = new HashMap<>();

    public List<Patient> searchingPatientByLastName(String lastName){

        List<Patient> allPatients = patientDAO.searchPatientByLastName(lastName);

        for (Patient p:allPatients){
            patientCaching.put(p.getPatientId(),p );
        }
        return allPatients;

    }

}
