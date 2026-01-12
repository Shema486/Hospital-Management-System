package hospital.hospital_management_system.model;

import java.time.LocalDate;

public class Patient {
    private  long patient_id;
    private String first_name;
    private String last_name;
    private LocalDate dob;
    private String gender;
    private String contact_number;
    private String address;

    public Patient() {}

    public Patient(
                    Long patient_id,
                    String first_name,
                    String last_name,
                    LocalDate dob,
                    String gender,
                    String contact_number,
                    String address) {
        this.patient_id = patient_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.dob = dob;
        this.gender = gender;
        this.contact_number = contact_number;
        this.address = address;
    }

    public Patient(
            String first_name,
            String last_name,
            LocalDate dob,
            String gender,
            String contact_number,
            String address) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.dob = dob;
        this.gender = gender;
        this.contact_number = contact_number;
        this.address = address;
    }

    public Patient(Long patientId) {
        this.patient_id = patientId;
    }

    public long getPatientId() {return patient_id;}
    public String getFirstName() {return first_name;}
    public String getLastName() {return last_name;}
    public LocalDate getDob() {return dob;}
    public String getGender() {return gender;}
    public String getContact_number() {return contact_number;}
    public String getAddress() {return address;}

    public void setLastName(String last_name) {this.last_name = last_name;}
    public void setDob(LocalDate dob) {this.dob = dob;}
    public void setGender(String gender) {this.gender = gender;}
    public void setContact_number(String contact_number) {this.contact_number = contact_number;}
    public void setAddress(String address) {this.address = address;}

    @Override
    public String toString() {
        return "Patients{" +
                "patient_id=" + patient_id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", dob=" + dob +
                ", gender='" + gender + '\'' +
                ", contact_number='" + contact_number + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
