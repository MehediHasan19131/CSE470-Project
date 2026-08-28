package com.healthcare.platform.repository;

import com.healthcare.platform.model.Faq;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByPublishedTrueOrderByDisplayOrderAscIdAsc();
    List<Faq> findAllByOrderByDisplayOrderAscIdAsc();
}
