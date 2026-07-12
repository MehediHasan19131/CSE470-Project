package com.healthcare.platform.controller.api;

import com.healthcare.platform.dto.ServiceListingResponse;
import com.healthcare.platform.service.ListingService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchApiController {
    private final ListingService listingService;

    public SearchApiController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/api/doctors/search")
    public List<ServiceListingResponse> doctors(
            @RequestParam(required = false) String speciality,
            @RequestParam(required = false) String location
    ) {
        return listingService.doctors(speciality, location);
    }

    @GetMapping("/api/hospitals")
    public List<ServiceListingResponse> hospitals(@RequestParam(required = false) String location) {
        return listingService.hospitals(location);
    }

    @GetMapping("/api/pharmacies")
    public List<ServiceListingResponse> pharmacies(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean emergency
    ) {
        return listingService.pharmacies(location, emergency);
    }
}
