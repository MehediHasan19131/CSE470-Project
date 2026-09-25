package com.healthcare.platform.controller.healthprofile;
import com.healthcare.platform.model.healthprofile.*;
import com.healthcare.platform.dto.healthprofile.*;
import com.healthcare.platform.service.healthprofile.*;

import com.healthcare.platform.model.auth.AuthUser;
import com.healthcare.platform.repository.auth.AuthUserJdbcRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * JSON API for the Health Profile feature (Member 1, Sprint 3 task:
 * Add History, Update History). Session or JWT auth both work here already -
 * this controller doesn't do anything auth-specific itself, it just reads the
 * logged-in user via {@link AuthUserJdbcRepository}, the same pattern
 * {@code ReviewApiController} uses. Every path below is restricted to
 * ROLE_PATIENT in SecurityConfig ({@code .requestMatchers("/api/health/**").hasRole("PATIENT")}) -
 * a health profile belongs to a patient, not a provider or admin.
 */
@RestController
public class HealthProfileApiController {

    private final HealthProfileService healthProfileService;
    private final AuthUserJdbcRepository authUsers;

    public HealthProfileApiController(HealthProfileService healthProfileService, AuthUserJdbcRepository authUsers) {
        this.healthProfileService = healthProfileService;
        this.authUsers = authUsers;
    }

    // ---------------------------------------------------------------------
    // Medical history - "Add History" / "Update History"
    // ---------------------------------------------------------------------

    @GetMapping("/api/health/history")
    public List<MedicalHistoryResponse> myHistory(Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        return healthProfileService.getHistory(me.getId()).stream().map(MedicalHistoryResponse::from).toList();
    }

    @PostMapping("/api/health/history")
    public ResponseEntity<?> addHistory(@Valid @RequestBody MedicalHistoryCreateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            MedicalHistoryEntry saved = healthProfileService.addHistory(me.getId(), request.condition(), request.diagnosedOn(), request.notes());
            return ResponseEntity.status(HttpStatus.CREATED).body(MedicalHistoryResponse.from(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/api/health/history/{id}")
    public ResponseEntity<?> updateHistory(@PathVariable Long id, @Valid @RequestBody MedicalHistoryUpdateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            MedicalHistoryEntry updated = healthProfileService.updateHistory(id, me.getId(), request.condition(), request.diagnosedOn(), request.notes());
            return ResponseEntity.ok(MedicalHistoryResponse.from(updated));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/api/health/history/{id}")
    public ResponseEntity<?> deleteHistory(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.deleteHistory(id, me.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // ---------------------------------------------------------------------
    // Allergies
    // ---------------------------------------------------------------------

    @GetMapping("/api/health/allergies")
    public List<AllergyResponse> myAllergies(Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        return healthProfileService.getAllergies(me.getId()).stream().map(AllergyResponse::from).toList();
    }

    @PostMapping("/api/health/allergies")
    public ResponseEntity<?> addAllergy(@Valid @RequestBody AllergyCreateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            Allergy saved = healthProfileService.addAllergy(me.getId(), request.allergen(), request.severity(), request.reaction());
            return ResponseEntity.status(HttpStatus.CREATED).body(AllergyResponse.from(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/api/health/allergies/{id}")
    public ResponseEntity<?> updateAllergy(@PathVariable Long id, @Valid @RequestBody AllergyUpdateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            Allergy updated = healthProfileService.updateAllergy(id, me.getId(), request.allergen(), request.severity(), request.reaction());
            return ResponseEntity.ok(AllergyResponse.from(updated));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/api/health/allergies/{id}")
    public ResponseEntity<?> deleteAllergy(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.deleteAllergy(id, me.getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
}
