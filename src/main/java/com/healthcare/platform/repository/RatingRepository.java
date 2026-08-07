package com.healthcare.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.healthcare.platform.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    long countByTargetUserId(Long targetUserId);

    @Query("select coalesce(avg(r.score), 0) from Rating r where r.targetUser.id = :targetUserId")
    double averageScore(@Param("targetUserId") Long targetUserId);

    List<Rating> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId);
}
