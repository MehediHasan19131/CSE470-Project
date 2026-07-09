package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.DashboardService;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardApiController {
    private final CurrentUserService currentUserService;
    private final DashboardService dashboardService;

    public DashboardApiController(CurrentUserService currentUserService, DashboardService dashboardService) {
        this.currentUserService = currentUserService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard")
    public Map<String, Object> dashboard(Authentication authentication) {
        User user = currentUserService.get(authentication);
        return dashboardService.dashboard(user);
    }
}
