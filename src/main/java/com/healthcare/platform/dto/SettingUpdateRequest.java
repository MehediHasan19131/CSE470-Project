package com.healthcare.platform.dto;

import java.util.Map;

public record SettingUpdateRequest(Map<String, String> settings) {
}
