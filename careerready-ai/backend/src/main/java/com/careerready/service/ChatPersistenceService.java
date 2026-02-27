package com.careerready.service;

import com.careerready.entity.ChatMessageEntity;
import com.careerready.entity.ChatSession;
import com.careerready.repository.ChatMessageRepository;
import com.careerready.repository.ChatSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatPersistenceService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public ChatPersistenceService(ChatSessionRepository sessionRepository, ChatMessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public ChatSession createSession(String initialTitle) {
        ChatSession session = ChatSession.builder()
                .id(UUID.randomUUID().toString())
                .title(initialTitle != null ? truncateTitle(initialTitle) : "New Session")
                .build();
        return sessionRepository.save(session);
    }

    @Transactional
    public ChatSession saveMessage(String sessionId, String role, String content) {
        ChatSession session;
        if (sessionId == null || sessionId.trim().isEmpty()) {
            session = createSession(content);
        } else {
            session = sessionRepository.findById(sessionId)
                    .orElseGet(() -> createSession(content));
        }
        
        ChatMessageEntity message = ChatMessageEntity.builder()
                .session(session)
                .role(role)
                .content(content)
                .build();
        
        messageRepository.save(message);

        // Update title if it's the first user message
        if ("user".equals(role) && ("New Session".equals(session.getTitle()))) {
            session.setTitle(truncateTitle(content));
            sessionRepository.save(session);
        }
        return session;
    }

    public List<ChatSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public Page<ChatMessageEntity> getSessionHistory(String sessionId, int page, int size) {
        return messageRepository.findBySessionIdOrderByTimestampAsc(sessionId, PageRequest.of(page, size));
    }

    @Transactional
    public void deleteSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    private String truncateTitle(String text) {
        if (text == null) return "New Session";
        return text.length() > 30 ? text.substring(0, 27) + "..." : text;
    }
}
