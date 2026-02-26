package com.careerready.controller;

import com.careerready.model.ChatRequest;
import com.careerready.model.ChatResponse;
import com.careerready.service.GroqService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Browsers will show origin 'null' or 'file://' depending on OS/Browser
public class ChatController {

    private final GroqService groqService;

    public ChatController(GroqService groqService) {
        this.groqService = groqService;
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
        return groqService.getAiResponse(request.getMessage());
    }

    @GetMapping("/health")
    public String health() {
        return "CareerReady AI Backend is running!";
    }
}
