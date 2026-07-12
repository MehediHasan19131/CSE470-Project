package com.healthcare.platform.controller.api;

import com.healthcare.platform.repository.MedicineRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyApiController {
    private final MedicineRepository medicines;

    public PharmacyApiController(MedicineRepository medicines) {
        this.medicines = medicines;
    }

    @PostMapping("/medicines/{medicineId}/sell")
    public ResponseEntity<Map<String, Object>> sellMedicine(@PathVariable Long medicineId) {
        return medicines.findById(medicineId)
                .map(medicine -> {
                    if (medicine.getStockQuantity() <= 0) {
                        return ResponseEntity.badRequest().body(Map.<String, Object>of("message", "Medicine is out of stock"));
                    }
                    medicine.setStockQuantity(medicine.getStockQuantity() - 1);
                    medicines.save(medicine);
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("message", "Medicine sold");
                    body.put("medicine", medicine.getName());
                    body.put("remainingStock", medicine.getStockQuantity());
                    return ResponseEntity.ok(body);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
