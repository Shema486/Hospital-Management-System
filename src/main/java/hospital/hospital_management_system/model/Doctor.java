package hospital.hospital_management_system.model;

public class Doctor {
    private Long doctorId;
    private String firstName;
    private String lastName;
    private String email;
    private String specialization;
    private Department department;
    private String phone;



    public Doctor(Long doctorId, String firstName, String lastName, String email, String specialization, Department department, String phone) {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.specialization = specialization;
        this.department = department;
        this.phone = phone;
    }

    public Doctor(String firstName, String lastName, String email, String specialization, Department department, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.specialization = specialization;
        this.department = department;
        this.phone = phone;
    }

    public Doctor() {}

    public Doctor(Long doctorId) {
        this.doctorId=doctorId;
    }

    public Long getDoctorId() {return doctorId;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getEmail() {return email;}
    public String getSpecialization() {return specialization;}
    public Department getDepartment() {return department ;}
    public String getPhone() {return phone;}

    public void setDoctorId(Long doctorId) {this.doctorId = doctorId;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setEmail(String email) {this.email = email;}
    public void setSpecialization(String specialization) {this.specialization = specialization;}
    public void setDepartmentId(Department department) {this.department = this.department;}
    public void setPhone(String phone) {this.phone = phone;}

    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", specialization='" + specialization + '\'' +
                ", department=" + department +
                ", phone='" + phone + '\'' +
                '}';
    }
}
