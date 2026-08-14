package com.healthcare.platform.repository;

import com.healthcare.platform.model.AiChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long> {
    List<AiChatMessage> findByUserIdOrderByCreatedAtAsc(Long userId);

    List<AiChatMessage> findByUserIdAndSessionIdOrderByCreatedAtAsc(Long userId, String sessionId);

    // Used to let a user clear one conversation or their whole AI chat history.
    long deleteByUserIdAndSessionId(Long userId, String sessionId);

    long deleteByUserId(Long userId);
}
