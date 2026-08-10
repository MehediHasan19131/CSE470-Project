package com.healthcare.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.healthcare.platform.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
}
