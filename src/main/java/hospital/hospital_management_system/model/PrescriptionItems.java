package hospital.hospital_management_system.model;



public class PrescriptionItems {

    private Prescriptions prescription;
    private MedicalInventory item;
    private String dosageInstruction;
    private int quantityDispensed;

    public PrescriptionItems() {}

    public PrescriptionItems(Prescriptions prescription,
                            MedicalInventory item,
                            String dosageInstruction,
                            int quantityDispensed) {
        this.prescription = prescription;
        this.item = item;
        this.dosageInstruction = dosageInstruction;
        this.quantityDispensed = quantityDispensed;
    }
    public PrescriptionItems(Long prescriptionId, Long itemId,
                             String dosageInstruction, int quantityDispensed) {
        this.prescription = new Prescriptions();
        this.prescription.setPrescriptionId(prescriptionId);

        this.item = new MedicalInventory();
        this.item.setItemId(itemId);

        this.dosageInstruction = dosageInstruction;
        this.quantityDispensed = quantityDispensed;
    }


    // Getters
    public Prescriptions getPrescription() {return prescription;}
    public Long getPrescriptionId() {return prescription != null ? prescription.getPrescriptionId() : null;}
    public MedicalInventory getItem() {return item;}
    public Long getItemId() {return item != null ? item.getItemId() : null;}
    public String getDosageInstruction() {return dosageInstruction;}
    public int getQuantityDispensed() {return quantityDispensed;}

    // Setters
    public void setPrescription(Prescriptions prescription) {this.prescription = prescription;}
    public void setItem(MedicalInventory item) {this.item = item;}



    @Override
    public String toString() {
        return "PrescriptionItem{" +
                "prescriptionId=" + getPrescriptionId() +
                ", itemId=" + getItemId() +
                ", dosageInstruction='" + dosageInstruction + '\'' +
                ", quantityDispensed=" + quantityDispensed +
                '}';
    }
}

