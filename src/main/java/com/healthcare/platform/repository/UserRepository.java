package com.healthcare.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByRoleAndActiveTrue(UserRole role);
    List<User> findByRoleIn(List<UserRole> roles);
    long countByRole(UserRole role);
}
