package com.healthcare.platform.service;

import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.repository.MedicineRepository;
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
}
