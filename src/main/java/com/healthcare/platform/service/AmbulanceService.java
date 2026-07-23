package com.healthcare.platform.service;

import com.healthcare.platform.dto.AmbulanceRequestBookingRequest;
import com.healthcare.platform.dto.AmbulanceRequestResponse;
import com.healthcare.platform.dto.AmbulanceResponse;
import com.healthcare.platform.model.Ambulance;
import com.healthcare.platform.model.AmbulanceRequest;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AmbulanceRepository;
import com.healthcare.platform.repository.AmbulanceRequestRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Sprint 2 — Member 4 (Ambulance Service + Map).
 * Handles ambulance fleet listing/location and the request-ambulance / track-ambulance workflow.
 * Distance is computed with the Haversine formula so results can be sorted "nearest first"
 * for the Leaflet/OpenStreetMap booking screen on the frontend.
 */
@Service
public class AmbulanceService {
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final AmbulanceRepository ambulances;
    private final AmbulanceRequestRepository requests;

    public AmbulanceService(AmbulanceRepository ambulances, AmbulanceRequestRepository requests) {
        this.ambulances = ambulances;
        this.requests = requests;
    }

    public List<AmbulanceResponse> list(Double lat, Double lng, Boolean availableOnly) {
        List<Ambulance> source = Boolean.TRUE.equals(availableOnly)
                ? ambulances.findByAvailableTrueOrderByIdAsc()
                : ambulances.findAll();

        return source.stream()
                .map(ambulance -> AmbulanceResponse.from(ambulance, distanceKm(lat, lng, ambulance)))
                .sorted(Comparator.comparing(
                        AmbulanceResponse::distanceKm,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<AmbulanceResponse> myFleet(User provider) {
        requireRole(provider, UserRole.AMBULANCE);
        return ambulances.findByProviderIdOrderByIdAsc(provider.getId()).stream()
                .map(ambulance -> AmbulanceResponse.from(ambulance, null))
                .toList();
    }

    public AmbulanceRequestResponse book(User patient, AmbulanceRequestBookingRequest booking) {
        requireRole(patient, UserRole.PATIENT);

        AmbulanceRequest request = new AmbulanceRequest();
        request.setPatient(patient);
        request.setPickupAddress(booking.pickupAddress());
        request.setPickupLatitude(booking.pickupLatitude());
        request.setPickupLongitude(booking.pickupLongitude());
        request.setDropAddress(booking.dropAddress());
        request.setDropLatitude(booking.dropLatitude());
        request.setDropLongitude(booking.dropLongitude());
        request.setEmergencyType(booking.emergencyType());
        request.setNotes(booking.notes());

        Ambulance nearest = ambulances.findByAvailableTrueOrderByIdAsc().stream()
                .min(Comparator.comparing(a -> distanceOrMax(booking.pickupLatitude(), booking.pickupLongitude(), a)))
                .orElse(null);

        if (nearest != null) {
            request.setAmbulance(nearest);
            request.setStatus("ACCEPTED");
            Double distance = distanceKm(booking.pickupLatitude(), booking.pickupLongitude(), nearest);
            if (distance != null) {
                BigDecimal fare = nearest.getBaseFare().add(nearest.getPerKmRate().multiply(BigDecimal.valueOf(distance)));
                request.setFareEstimate(fare.setScale(2, java.math.RoundingMode.HALF_UP));
            }
        }

        return AmbulanceRequestResponse.from(requests.save(request));
    }

    public List<AmbulanceRequestResponse> myRequests(User patient) {
        requireRole(patient, UserRole.PATIENT);
        return requests.findByPatientIdOrderByRequestedAtDesc(patient.getId()).stream()
                .map(AmbulanceRequestResponse::from)
                .toList();
    }

    public AmbulanceRequestResponse track(User actor, Long requestId) {
        AmbulanceRequest request = requests.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambulance request not found"));
        boolean isOwner = request.getPatient().getId().equals(actor.getId());
        boolean isAssignedProvider = request.getAmbulance() != null
                && request.getAmbulance().getProvider().getId().equals(actor.getId());
        if (!isOwner && !isAssignedProvider && actor.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this request");
        }
        return AmbulanceRequestResponse.from(request);
    }

    public List<AmbulanceRequestResponse> incoming(User provider) {
        requireRole(provider, UserRole.AMBULANCE);
        List<Long> ownedIds = ambulances.findByProviderIdOrderByIdAsc(provider.getId()).stream()
                .map(Ambulance::getId)
                .toList();

        List<AmbulanceRequest> unassigned = requests.findByStatusOrderByRequestedAtAsc("REQUESTED");
        List<AmbulanceRequest> assigned = ownedIds.isEmpty()
                ? List.of()
                : requests.findByAmbulanceIdInOrderByRequestedAtDesc(ownedIds);

        return java.util.stream.Stream.concat(assigned.stream(), unassigned.stream())
                .distinct()
                .map(AmbulanceRequestResponse::from)
                .toList();
    }

    public AmbulanceRequestResponse updateRequestStatus(User provider, Long requestId, String status) {
        requireRole(provider, UserRole.AMBULANCE);
        AmbulanceRequest request = requests.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambulance request not found"));

        String normalized = status.toUpperCase();
        if (!List.of("ACCEPTED", "EN_ROUTE", "COMPLETED", "CANCELLED").contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        if (request.getAmbulance() == null) {
            List<Ambulance> fleet = ambulances.findByProviderIdOrderByIdAsc(provider.getId());
            Ambulance toAssign = fleet.stream().filter(Ambulance::isAvailable).findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "You have no available ambulance to assign"));
            request.setAmbulance(toAssign);
        } else if (!request.getAmbulance().getProvider().getId().equals(provider.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This request is assigned to another provider");
        }

        request.setStatus(normalized);
        if ("COMPLETED".equals(normalized) || "CANCELLED".equals(normalized)) {
            request.getAmbulance().setAvailable(true);
        } else if ("ACCEPTED".equals(normalized)) {
            request.getAmbulance().setAvailable(false);
        }
        return AmbulanceRequestResponse.from(requests.save(request));
    }

    public AmbulanceRequestResponse cancelMyRequest(User patient, Long requestId) {
        requireRole(patient, UserRole.PATIENT);
        AmbulanceRequest request = requests.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambulance request not found"));
        if (!request.getPatient().getId().equals(patient.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your request");
        }
        if ("COMPLETED".equals(request.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Completed requests cannot be cancelled");
        }
        request.setStatus("CANCELLED");
        if (request.getAmbulance() != null) {
            request.getAmbulance().setAvailable(true);
        }
        return AmbulanceRequestResponse.from(requests.save(request));
    }

    public AmbulanceResponse updateLocation(User provider, Long ambulanceId, double lat, double lng) {
        Ambulance ambulance = ownedAmbulance(provider, ambulanceId);
        ambulance.setLocation(lat, lng);
        return AmbulanceResponse.from(ambulances.save(ambulance), null);
    }

    public AmbulanceResponse updateAvailability(User provider, Long ambulanceId, boolean available) {
        Ambulance ambulance = ownedAmbulance(provider, ambulanceId);
        ambulance.setAvailable(available);
        return AmbulanceResponse.from(ambulances.save(ambulance), null);
    }

    private Ambulance ownedAmbulance(User provider, Long ambulanceId) {
        requireRole(provider, UserRole.AMBULANCE);
        Ambulance ambulance = ambulances.findById(ambulanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ambulance not found"));
        if (!ambulance.getProvider().getId().equals(provider.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your ambulance");
        }
        return ambulance;
    }

    private void requireRole(User user, UserRole role) {
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This action requires the " + role + " role");
        }
    }

    private Double distanceKm(Double lat, Double lng, Ambulance ambulance) {
        if (lat == null || lng == null || ambulance.getLatitude() == null || ambulance.getLongitude() == null) {
            return null;
        }
        return haversine(lat, lng, ambulance.getLatitude(), ambulance.getLongitude());
    }

    private double distanceOrMax(double lat, double lng, Ambulance ambulance) {
        Double distance = distanceKm(lat, lng, ambulance);
        return distance == null ? Double.MAX_VALUE : distance;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(EARTH_RADIUS_KM * c * 10.0) / 10.0;
    }
}
