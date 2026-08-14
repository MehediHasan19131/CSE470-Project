package com.healthcare.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.healthcare.platform.model.AppSetting;

public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
    Optional<AppSetting> findByKey(String key);
}
