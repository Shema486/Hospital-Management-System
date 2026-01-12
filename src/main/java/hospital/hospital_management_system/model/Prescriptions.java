package hospital.hospital_management_system.model;

import java.time.LocalDateTime;

public class Prescriptions {
    private Long prescriptionId;
    private Patient patient;
    private LocalDateTime prescriptionDate;
    private String notes;

    public Prescriptions() {}

    public Prescriptions(Long prescriptionId, Patient patient, LocalDateTime prescriptionDate, String notes) {
        this.prescriptionId = prescriptionId;
        this.patient = patient;
        this.prescriptionDate = prescriptionDate;
        this.notes = notes;
    }

    public Prescriptions(Patient patient, LocalDateTime prescriptionDate, String notes) {
        this.patient = patient;
        this.prescriptionDate = prescriptionDate;
        this.notes = notes;
    }

    // Getters
    public Long getPrescriptionId() { return prescriptionId; }
    public Patient getPatient() { return patient; }
    public LocalDateTime getPrescriptionDate() { return prescriptionDate; }
    public String getNotes() { return notes; }

    // Setters
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setPrescriptionDate(LocalDateTime prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Prescription{" +
                "prescriptionId=" + prescriptionId +
                ", patient=" + (patient != null ? patient.getFirstName() + " " + patient.getLastName() : "null") +
                ", prescriptionDate=" + prescriptionDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
