package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.PatientFeedbackDAO;
import hospital.hospital_management_system.model.PatientFeedback;

import java.util.*;

/**
 * Service layer for Patient Feedback management
 * Includes caching for faster access
 */
public class PatientFeedbackService {

    private final PatientFeedbackDAO feedbackDAO = new PatientFeedbackDAO();
    private final Map<Long, PatientFeedback> feedbackCache = new HashMap<>();

    // CREATE
    public void addFeedback(PatientFeedback feedback) {
        feedbackDAO.addFeedback(feedback);
        clearCache();
    }


    // READ all feedback
    public List<PatientFeedback> getAllFeedback() {
        List<PatientFeedback> feedbackList = feedbackDAO.findAll();
        // Add to cache
        for (PatientFeedback f : feedbackList) {
            feedbackCache.put(f.getFeedbackId(), f);
        }
        return feedbackList;
    }

    // DELETE
    public void deleteFeedback(Long id) {
        feedbackDAO.deleteFeedback(id);
        feedbackCache.remove(id);
    }

    // Clear cache
    public void clearCache() {
        feedbackCache.clear();
    }


}
