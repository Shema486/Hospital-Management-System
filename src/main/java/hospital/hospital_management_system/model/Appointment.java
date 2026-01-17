package hospital.hospital_management_system.model;

import java.time.LocalDateTime;

public class Appointment {

    private Long appointmentId;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime appointmentDate;
    private String status;
    private String reason;

    public Appointment() {}

    // Constructor for creating new appointments
    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentDate, String reason) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.status = "Scheduled";
        this.reason = reason;
    }

    // Constructor for reading from DB
    public Appointment(
            Long appointmentId,
            Patient patient,
            Doctor doctor,
            LocalDateTime appointmentDate,
            String status,
            String reason
    ) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.reason = reason;
    }

    // Getters
    public Long getAppointmentId() { return appointmentId; }

    public Patient getPatient() { return patient; }

    public Long getPatientId() {
        return patient != null ? patient.getPatientId() : null;
    }

    public Doctor getDoctor() { return doctor; }

    public Long getDoctorId() {
        return doctor != null ? doctor.getDoctorId() : null;
    }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }

    public String getStatus() { return status; }

    public String getReason() { return reason; }

    // Setters

    public void setPatient(Patient patient) { this.patient = patient; }

    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", patient=" + (patient != null
                ? patient.getFirstName() + " " + patient.getLastName()
                : "null") +
                ", doctor=" + (doctor != null
                ? doctor.getFirstName() + " " + doctor.getLastName()
                : "null") +
                ", appointmentDate=" + appointmentDate +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
