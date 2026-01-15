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
    
    // READ by ID (with caching)
    public PatientFeedback getFeedbackById(Long id) {
        // Check cache first
        if (feedbackCache.containsKey(id)) {
            return feedbackCache.get(id);
        }
        
        // Get from database
        PatientFeedback feedback = feedbackDAO.findById(id);
        if (feedback != null) {
            feedbackCache.put(id, feedback);
        }
        return feedback;
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
    
    /**
     * Sort feedback by rating (highest first)
     */
    public List<PatientFeedback> sortByRatingHighest(List<PatientFeedback> feedbackList) {
        List<PatientFeedback> sorted = new ArrayList<>(feedbackList);
        
        Collections.sort(sorted, new Comparator<PatientFeedback>() {
            @Override
            public int compare(PatientFeedback f1, PatientFeedback f2) {
                // Reverse order for highest first
                return Integer.compare(f2.getRating(), f1.getRating());
            }
        });
        
        return sorted;
    }
    
    /**
     * Sort feedback by date (newest first)
     */
    public List<PatientFeedback> sortByDateNewest(List<PatientFeedback> feedbackList) {
        List<PatientFeedback> sorted = new ArrayList<>(feedbackList);
        
        Collections.sort(sorted, new Comparator<PatientFeedback>() {
            @Override
            public int compare(PatientFeedback f1, PatientFeedback f2) {
                return f2.getFeedbackDate().compareTo(f1.getFeedbackDate());
            }
        });
        
        return sorted;
    }
}
