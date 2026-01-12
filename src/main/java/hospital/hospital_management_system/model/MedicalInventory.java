package hospital.hospital_management_system.model;

import java.math.BigDecimal;

public class MedicalInventory {
    private Long itemId;
    private String itemName;
    private int stockQuantity;
    private BigDecimal unitPrice;

    public MedicalInventory() {}

    public MedicalInventory(String itemName, int stockQuantity, BigDecimal unitPrice) {
        this.itemName = itemName;
        this.stockQuantity = stockQuantity;
        this.unitPrice = unitPrice;
    }

    public MedicalInventory(Long itemId, String itemName, int stockQuantity, BigDecimal unitPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.stockQuantity = stockQuantity;
        this.unitPrice = unitPrice;
    }

    public Long getItemId() {return itemId;}
    public String getItemName() {return itemName;}
    public int getStockQuantity() {return stockQuantity;}
    public BigDecimal getUnitPrice() {return unitPrice;}

    public void setItemId(Long itemId) {this.itemId = itemId;}
    public void setItemName(String itemName) {this.itemName = itemName;}
    public void setStockQuantity(int stockQuantity) {this.stockQuantity = stockQuantity;}
    public void setUnitPrice(BigDecimal unitPrice) {this.unitPrice = unitPrice;}
}
