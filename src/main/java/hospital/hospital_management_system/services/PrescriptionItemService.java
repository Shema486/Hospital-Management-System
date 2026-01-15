package hospital.hospital_management_system.services;

import hospital.hospital_management_system.dao.PrescriptionItemDAO;
import hospital.hospital_management_system.model.PrescriptionItems;

import java.util.*;

public class PrescriptionItemService {

    private final PrescriptionItemDAO dao = new PrescriptionItemDAO();
    private final Map<Long, List<PrescriptionItems>> cache = new HashMap<>();

    public void addPrescriptionItem(PrescriptionItems item) {
        dao.addPrescriptionItem(item);
        cache.remove(item.getPrescriptionId());
    }

    public List<PrescriptionItems> getItemsByPrescription(Long prescriptionId) {
        if (cache.containsKey(prescriptionId)) {
            return cache.get(prescriptionId);
        }
        List<PrescriptionItems> items = dao.findByPrescription(prescriptionId);
        cache.put(prescriptionId, items);
        return items;
    }

    public void deleteItem(Long prescriptionId, Long itemId) {
        dao.deleteItem(prescriptionId, itemId);
        cache.remove(prescriptionId);
    }

    public void clearCache() {
        cache.clear();
    }
}
