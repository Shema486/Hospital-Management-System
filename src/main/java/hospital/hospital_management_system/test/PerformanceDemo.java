package hospital.hospital_management_system.test;

import hospital.hospital_management_system.services.PatientService;
import hospital.hospital_management_system.model.Patient;
import hospital.hospital_management_system.utils.PerformanceMeasurer;

import java.util.List;

/**
 * Simple demonstration of performance improvements with caching
 * This shows how caching makes data retrieval faster
 */
public class PerformanceDemo {
    
    public static void main(String[] args) {
        System.out.println("=== PERFORMANCE DEMONSTRATION ===\n");
        
        PatientService patientService = new PatientService();
        
        // Test 1: First access (no cache) vs Second access (with cache)
        System.out.println("Test 1: Cache Performance");
        System.out.println("-------------------------");
        
        // Clear cache to start fresh
        patientService.clearCache();
        
        // First access - data loaded from database
        long startTime1 = System.nanoTime();
        Patient patient1 = patientService.getPatientById(1L);
        long endTime1 = System.nanoTime();
        long time1 = endTime1 - startTime1;
        
        System.out.println("First access (from database): " + time1 + " nanoseconds");
        
        // Second access - data loaded from cache
        long startTime2 = System.nanoTime();
        Patient patient2 = patientService.getPatientById(1L);
        long endTime2 = System.nanoTime();
        long time2 = endTime2 - startTime2;
        
        System.out.println("Second access (from cache): " + time2 + " nanoseconds");
        
        // Calculate improvement
        if (time2 > 0) {
            double improvement = ((double)(time1 - time2) / time1) * 100;
            System.out.println("Performance improvement: " + String.format("%.2f", improvement) + "%");
        }
        
        System.out.println("\n");
        
        // Test 2: Sorting performance
        System.out.println("Test 2: Sorting Performance");
        System.out.println("---------------------------");
        
        List<Patient> patients = patientService.getAllPatients();
        System.out.println("Total patients: " + patients.size());
        
        // Measure sorting time
        long sortStart = System.nanoTime();
        List<Patient> sortedPatients = patientService.sortPatientsByLastName(patients);
        long sortEnd = System.nanoTime();
        long sortTime = sortEnd - sortStart;
        
        System.out.println("Sorting time: " + sortTime + " nanoseconds");
        System.out.println("Sorted " + sortedPatients.size() + " patients by last name");
        
        // Show first 3 sorted patients
        System.out.println("\nFirst 3 patients (sorted by last name):");
        for (int i = 0; i < Math.min(3, sortedPatients.size()); i++) {
            Patient p = sortedPatients.get(i);
            System.out.println((i+1) + ". " + p.getLastName() + ", " + p.getFirstName());
        }
        
        System.out.println("\n=== DEMO COMPLETE ===");
    }
}
