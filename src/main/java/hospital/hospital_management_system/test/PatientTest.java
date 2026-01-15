package hospital.hospital_management_system.test;

import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.services.PatientService;

import java.time.LocalDate;

public class PatientTest {
    static PatientService service = new PatientService();
    public static void main(String[] args) {
        Patient p = new Patient("viv","hoe", LocalDate.now(),"m","078029828","wd");

    service.addPatient(p);
        long start2 = System.nanoTime();
        service.searchPatientByLastName("doe");
        long end1 =System.nanoTime();
        System.out.println("Time without streams is: "+ (end1-start2));
        long start1 = System.nanoTime();
        service.searchPatientByLastNameUsingStreams("doe");
        long end =System.nanoTime();
        System.out.println("Time using streams is: "+ (end-start1));


    }
}
