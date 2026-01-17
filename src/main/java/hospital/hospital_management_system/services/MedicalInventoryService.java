package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.MedicalInventoryDAO;
import hospital.hospital_management_system.model.MedicalInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalInventoryService {
    private final MedicalInventoryDAO inventoryDAO = new MedicalInventoryDAO();
    private final Map<Long, MedicalInventory> inventoryCache = new HashMap<>();

    public void addInventoryItem(MedicalInventory item) {
        inventoryDAO.addInventoryItem(item);
        if (item.getItemId() != null) {
            inventoryCache.put(item.getItemId(), item);
        }
    }

    public void updateInventoryItem(MedicalInventory item) {
        inventoryDAO.updateItem(item);
        inventoryCache.put(item.getItemId(), item);
    }

    public void deleteInventoryItem(Long itemId) {
        inventoryDAO.deleteInventoryItem(itemId);
        inventoryCache.remove(itemId);
    }

    public List<MedicalInventory> getAllInventoryItems() {
        List<MedicalInventory> items = inventoryDAO.findAll();
        for (MedicalInventory item : items) {
            inventoryCache.put(item.getItemId(), item);
        }
        return items;
    }

    public void clearCache() {
        inventoryCache.clear();
    }

    /**
     * Check if medicine name is unique in inventory table
     * @param itemName The medicine name to check (case-insensitive)
     * @param excludeItemId Item ID to exclude from check (for updates, pass 0 if not needed)
     * @return true if medicine name is unique, false if it exists
     */
    public boolean isItemNameUnique(String itemName, long excludeItemId) {
        return !inventoryDAO.itemNameExists(itemName, excludeItemId);
    }
}
