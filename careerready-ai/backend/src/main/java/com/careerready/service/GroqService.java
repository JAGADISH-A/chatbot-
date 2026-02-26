package com.careerready.service;
import com.careerready.model.ChatRequest;
import com.careerready.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.model}")
    private String modelName;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    
    private final RestTemplate restTemplate;

    public GroqService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        this.restTemplate = new RestTemplate(factory);
    }

    public ChatResponse getAiResponse(String userMessage) {
        System.out.println("Processing request for model: " + modelName);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", 
                "You are CareerReady AI, a friendly and professional career advisor for students. " +
                "You specialize in resume writing, interview preparation, soft skills, networking, LinkedIn tips, workplace stress, and career planning. " +
                "Keep answers clear, practical, encouraging, and concise. Use bullet points where helpful. " +
                "If asked something unrelated to career topics, politely redirect back to career advice."));
            messages.add(Map.of("role", "user", "content", userMessage));

            body.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            Map<String, Object> response = restTemplate.postForObject(GROQ_URL, entity, Map.class);
            System.out.println("Received response from Groq");

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return ChatResponse.builder()
                            .message(content)
                            .success(true)
                            .build();
                }
            }

            return ChatResponse.builder()
                    .message("No response from AI")
                    .success(false)
                    .error("Empty response from Groq API")
                    .build();

        } catch (Exception e) {
            System.err.println("Error calling Groq: " + e.getMessage());
            e.printStackTrace();
            return ChatResponse.builder()
                    .message("Error communicating with AI")
                    .success(false)
                    .error(e.getMessage())
                    .build();
        }
    }
}
