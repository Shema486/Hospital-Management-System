package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.AppointmentDAO;
import hospital.hospital_management_system.model.Appointment;

import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void create(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null");
        }
        
        if (appointment.getPatient() == null || appointment.getDoctor() == null) {
            throw new IllegalArgumentException("Patient and Doctor are required");
        }
        
        if (appointment.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Appointment date is required");
        }

        appointmentDAO.add(appointment);
    }

    public List<Appointment> getAll() {
        return appointmentDAO.findAll();
    }

    public void cancel(Long appointmentId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        appointmentDAO.updateStatus(appointmentId, "Cancelled");
    }

    public void complete(Long appointmentId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        appointmentDAO.updateStatus(appointmentId, "Completed");
    }

    public void delete(Long appointmentId) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        appointmentDAO.delete(appointmentId);
    }
}
