package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.PrescriptionDAO;
import hospital.hospital_management_system.model.Prescriptions;

import java.util.*;

/**
 * Service layer for Prescription management
 * Includes caching for faster access
 */
public class PrescriptionService {
    
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
    private final Map<Long, Prescriptions> prescriptionCache = new HashMap<>();
    
    // CREATE
    public void addPrescription(Prescriptions prescription) {
        prescriptionDAO.addPrescription(prescription);
        clearCache(); // Clear cache after adding new data
    }

    // READ all prescriptions
    public List<Prescriptions> getAllPrescriptions() {
        List<Prescriptions> prescriptions = prescriptionDAO.findAll();
        // Add to cache
        for (Prescriptions p : prescriptions) {
            prescriptionCache.put(p.getPrescriptionId(), p);
        }
        return prescriptions;
    }
    
    // UPDATE
    public void updatePrescriptionNotes(Long id, String notes) {
        prescriptionDAO.updatePrescriptionNotes(id, notes);
        prescriptionCache.remove(id); // Remove from cache to force refresh
    }
    
    // DELETE
    public void deletePrescription(Long id) {
        prescriptionDAO.deletePrescription(id);
        prescriptionCache.remove(id);
    }

    public boolean hasPrescriptionForAppointment(Long appointmentId) {
        if (appointmentId == null) {
            return false;
        }
        return !prescriptionDAO.findByAppointmentId(appointmentId).isEmpty();
    }
    
    // Clear cache
    public void clearCache() {
        prescriptionCache.clear();
    }


}
