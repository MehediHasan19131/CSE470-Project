package com.healthcare.platform.controller;

import com.healthcare.platform.dto.RoleUpdateRequest;
import com.healthcare.platform.dto.SettingUpdateRequest;
import com.healthcare.platform.dto.UserResponse;
import com.healthcare.platform.model.AppSetting;
import com.healthcare.platform.repository.AppSettingRepository;
import com.healthcare.platform.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final UserRepository users;
    private final AppSettingRepository settings;

    public AdminApiController(UserRepository users, AppSettingRepository settings) {
        this.users = users;
        this.settings = settings;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long userId, @Valid @RequestBody RoleUpdateRequest request) {
        return users.findById(userId)
                .map(user -> {
                    user.setRole(request.role());
                    return ResponseEntity.ok(UserResponse.from(users.save(user)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/settings")
    public Map<String, String> settings() {
        Map<String, String> response = new LinkedHashMap<>();
        settings.findAll().forEach(setting -> response.put(setting.getKey(), setting.getValue()));
        return response;
    }

    @PutMapping("/settings")
    public Map<String, Object> updateSettings(@RequestBody SettingUpdateRequest request) {
        request.settings().forEach((key, value) -> {
            AppSetting setting = settings.findByKey(key).orElseGet(() -> new AppSetting(key, value));
            setting.setValue(value);
            settings.save(setting);
        });
        return Map.of("message", "Settings updated", "settings", request.settings());
    }
}
