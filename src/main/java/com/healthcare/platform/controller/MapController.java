package com.healthcare.platform.controller;

import com.healthcare.platform.dto.MapProviderResponse;
import com.healthcare.platform.service.MapService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Map Integration feature. Renders the public "Find care near you" page and
 * serves the provider markers (as JSON) that the Leaflet + OpenStreetMap map
 * on that page plots. Both routes are public (see SecurityConfig).
 */
@Controller
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/map")
    public String map() {
        return "map";
    }

    @GetMapping("/api/map/providers")
    @ResponseBody
    public List<MapProviderResponse> providers() {
        return mapService.providersWithLocations();
    }
}
