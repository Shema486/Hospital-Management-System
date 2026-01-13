package hospital.hospital_management_system.model;

import java.time.LocalDateTime;

public class Prescriptions {

    private Long prescriptionId;
    private Long appointmentId;
    private LocalDateTime prescriptionDate;
    private String notes;

    public Prescriptions() {}

    public Prescriptions(Long appointmentId, LocalDateTime prescriptionDate, String notes) {
        this.appointmentId = appointmentId;
        this.prescriptionDate = prescriptionDate;
        this.notes = notes;
    }

    public Prescriptions(Long prescriptionId, Long appointmentId,
                        LocalDateTime prescriptionDate, String notes) {
        this.prescriptionId = prescriptionId;
        this.appointmentId = appointmentId;
        this.prescriptionDate = prescriptionDate;
        this.notes = notes;
    }

    public Long getPrescriptionId() { return prescriptionId; }
    public Long getAppointmentId() { return appointmentId; }
    public LocalDateTime getPrescriptionDate() { return prescriptionDate; }
    public String getNotes() { return notes; }

    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public void setPrescriptionDate(LocalDateTime prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public void setNotes(String notes) { this.notes = notes; }
}
