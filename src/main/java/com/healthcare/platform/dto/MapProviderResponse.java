package com.healthcare.platform.dto;

/**
 * A single provider pin for the public "Find care near you" map
 * (Map Integration feature). Serialized straight to JSON by MapController.
 */
public record MapProviderResponse(
        String name,
        String role,
        String city,
        String address,
        String specialization,
        String service,
        boolean emergency,
        double lat,
        double lng
) {
}
