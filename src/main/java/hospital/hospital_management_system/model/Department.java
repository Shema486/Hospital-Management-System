package hospital.hospital_management_system.model;

public class Department {
    private Long deptId;
    private String deptName;
    private int locationFloor;

    public Department() {}

    public Department(Long deptId, String deptName, int locationFloor) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.locationFloor = locationFloor;
    }

    public Department(String deptName, int locationFloor) {
        this.deptName = deptName;
        this.locationFloor = locationFloor;
    }

    public Department(long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {return deptId;}
    public String getDeptName() {return deptName;}
    public int getLocationFloor() {return locationFloor;}

    public void setDeptName(String deptName) {this.deptName = deptName;}
    public void setLocationFloor(int locationFloor) {this.locationFloor = locationFloor;}

    @Override
    public String toString() {
        return "Department{" +
                "deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", locationFloor=" + locationFloor +
                '}';
    }
}
