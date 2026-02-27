package com.careerready.controller;

import com.careerready.entity.ChatMessageEntity;
import com.careerready.entity.ChatSession;
import com.careerready.model.ChatRequest;
import com.careerready.model.ChatResponse;
import com.careerready.service.GroqService;
import com.careerready.service.ChatPersistenceService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Browsers will show origin 'null' or 'file://' depending on OS/Browser
public class ChatController {

    private final GroqService groqService;
    private final ChatPersistenceService persistenceService;

    public ChatController(GroqService groqService, ChatPersistenceService persistenceService) {
        this.groqService = groqService;
        this.persistenceService = persistenceService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            return ChatResponse.builder()
                    .message("Message cannot be empty")
                    .success(false)
                    .error("Validation failed")
                    .build();
        }

        // 1. Save User Message
        ChatSession session = persistenceService.saveMessage(request.getSessionId(), "user", request.getMessage());

        // 2. Get AI Response
        ChatResponse response = groqService.getAiResponse(request.getMessage(), request.getHistory());

        // 3. Save AI Message & Return Session ID
        if (response.isSuccess()) {
            persistenceService.saveMessage(session.getId(), "assistant", response.getMessage());
            response.setSessionId(session.getId());
        }

        return response;
    }

    @GetMapping("/sessions")
    public List<ChatSession> getAllSessions() {
        return persistenceService.getAllSessions();
    }

    @GetMapping("/sessions/{id}")
    public Page<ChatMessageEntity> getSessionHistory(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return persistenceService.getSessionHistory(id, page, size);
    }

    @DeleteMapping("/sessions/{id}")
    public void deleteSession(@PathVariable String id) {
        persistenceService.deleteSession(id);
    }

    @GetMapping("/health")
    public String health() {
        return "CareerReady AI Backend is running!";
    }
}
