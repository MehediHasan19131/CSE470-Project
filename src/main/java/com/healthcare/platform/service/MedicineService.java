package com.healthcare.platform.service;

import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.MedicineRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 */
@Service
public class MedicineService {
    private final MedicineRepository medicines;

    public MedicineService(MedicineRepository medicines) {
        this.medicines = medicines;
    }

    public List<Medicine> getAllMedicines() {
        return medicines.findByActiveTrueOrderByNameAsc();
    }

    public Medicine getMedicineById(Long id) {
        return medicines.findById(id)
                .filter(Medicine::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found with id: " + id));
    }

    // Medicine Search
    public List<Medicine> searchMedicines(String query) {
        if (query == null || query.isBlank()) {
            return getAllMedicines();
        }
        return medicines.findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(query.trim());
    }

    // --- Per-pharmacy catalogue management ---------------------------------

    /** A pharmacy's own medicines (active + inactive), for its management page. */
    public List<Medicine> myMedicines(Long pharmacyId) {
        return medicines.findByPharmacyIdOrderByNameAsc(pharmacyId);
    }

    public Medicine addMedicine(User pharmacy, String name, String description, BigDecimal price, int stock) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medicine name is required.");
        }
        if (price == null || price.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a valid price.");
        }
        Medicine medicine = new Medicine(name.trim(), description, price, Math.max(0, stock));
        medicine.setPharmacy(pharmacy);
        return medicines.save(medicine);
    }

    public void updateMedicine(User pharmacy, Long id, BigDecimal price, int stock, boolean active) {
        Medicine medicine = medicines.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found."));
        requireOwner(pharmacy, medicine);
        if (price != null && price.signum() >= 0) {
            medicine.setPrice(price);
        }
        medicine.setStockQuantity(Math.max(0, stock));
        medicine.setActive(active);
        medicines.save(medicine);
    }

    private void requireOwner(User pharmacy, Medicine medicine) {
        if (pharmacy.getRole() == UserRole.ADMIN) {
            return;
        }
        if (medicine.getPharmacy() == null || !medicine.getPharmacy().getId().equals(pharmacy.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this medicine.");
        }
    }
}
