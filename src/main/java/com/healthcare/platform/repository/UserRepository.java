package com.healthcare.platform.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    long countByRole(UserRole role);

    List<User> findByRoleAndActiveTrue(UserRole role);
}
