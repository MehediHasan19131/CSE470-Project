package com.healthcare.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.healthcare.platform.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByTargetUserId(Long targetUserId);
    List<Rating> findByReviewerUserId(Long reviewerUserId);
    long countByTargetUserId(Long targetUserId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(AVG(r.score), 0) FROM Rating r WHERE r.targetUser.id = :targetUserId")
    double averageScore(@org.springframework.data.repository.query.Param("targetUserId") Long targetUserId);
}
