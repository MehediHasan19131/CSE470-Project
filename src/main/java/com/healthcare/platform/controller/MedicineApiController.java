package com.healthcare.platform.controller;

import com.healthcare.platform.dto.MedicineResponse;
import com.healthcare.platform.service.MedicineService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 * Medicine browsing + Medicine Search.
 */
@RestController
@RequestMapping("/api/medicines")
public class MedicineApiController {
    private final MedicineService medicineService;

    public MedicineApiController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public List<MedicineResponse> getAllMedicines() {
        return medicineService.getAllMedicines().stream().map(MedicineResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MedicineResponse getMedicineById(@PathVariable Long id) {
        return MedicineResponse.from(medicineService.getMedicineById(id));
    }

    // Medicine Search -> /api/medicines/search?q=paracetamol
    @GetMapping("/search")
    public List<MedicineResponse> searchMedicines(@RequestParam(required = false) String q) {
        return medicineService.searchMedicines(q).stream().map(MedicineResponse::from).toList();
    }
}
