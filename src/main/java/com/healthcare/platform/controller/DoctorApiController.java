package com.healthcare.platform.controller;

import com.healthcare.platform.dto.DoctorRequest;
import com.healthcare.platform.dto.DoctorResponse;
import com.healthcare.platform.service.DoctorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 * Doctor CRUD + Search Doctor by Specialty.
 * <p>
 * Note: the doctor/hospital/pharmacy *listing* search built by the team
 * already lives at GET /api/doctors/search (see SearchApiController).
 * This controller adds full doctor record management (create/update/delete)
 * plus a dedicated specialty-only search route.
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorApiController {
    private final DoctorService doctorService;

    public DoctorApiController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<DoctorResponse> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public DoctorResponse getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse created = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public DoctorResponse updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorRequest request) {
        return doctorService.updateDoctor(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // Search Doctor by Specialty -> /api/doctors/specialty/{specialty}
    @GetMapping("/specialty/{specialty}")
    public List<DoctorResponse> searchBySpecialty(@PathVariable String specialty) {
        return doctorService.searchBySpecialty(specialty);
    }
}
