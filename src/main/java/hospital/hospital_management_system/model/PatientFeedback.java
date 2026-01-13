package hospital.hospital_management_system.model;

import java.time.LocalDate;

public class PatientFeedback {

    private Long feedbackId;
    private Patient patient;
    private int rating;
    private String comments;
    private LocalDate feedbackDate;

    // Empty constructor (required)
    public PatientFeedback() {}

    // Full constructor
    public PatientFeedback(Long feedbackId, Patient patient, int rating,
                           String comments, LocalDate feedbackDate) {
        this.feedbackId = feedbackId;
        this.patient = patient;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = feedbackDate;
    }

    // Constructor without ID (for INSERT)
    public PatientFeedback(Patient patient, int rating, String comments) {
        this.patient = patient;
        this.rating = rating;
        this.comments = comments;
        this.feedbackDate = LocalDate.now();
    }

    // Getters
    public Long getFeedbackId() { return feedbackId; }
    public Patient getPatient() { return patient; }
    public Long getPatientId() {
        return patient != null ? patient.getPatientId() : null;
    }
    public int getRating() { return rating; }
    public String getComments() { return comments; }
    public LocalDate getFeedbackDate() { return feedbackDate; }

    // Setters
    public void setFeedbackId(Long feedbackId) { this.feedbackId = feedbackId; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setRating(int rating) { this.rating = rating; }
    public void setComments(String comments) { this.comments = comments; }
    public void setFeedbackDate(LocalDate feedbackDate) { this.feedbackDate = feedbackDate; }

    @Override
    public String toString() {
        return "PatientFeedback{" +
                "feedbackId=" + feedbackId +
                ", patientId=" + getPatientId() +
                ", rating=" + rating +
                ", comments='" + comments + '\'' +
                ", feedbackDate=" + feedbackDate +
                '}';
    }
}

