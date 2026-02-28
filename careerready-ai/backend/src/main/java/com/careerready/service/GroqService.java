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

    public ChatResponse getAiResponse(String userMessage, List<com.careerready.model.ChatMessage> history, com.careerready.entity.User user) {
        System.out.println("Processing request for model: " + modelName);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
 
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
 
            // Build dynamic system prompt based on user preferences
            String name = (user != null && user.getFullName() != null) ? user.getFullName() : "Candidate";
            String tone = (user != null && user.getTonePreference() != null) ? user.getTonePreference() : "friendly";
            String addressAs = (user != null && user.getAddressPreference() != null) ? user.getAddressPreference() : "professional";
            String purpose = (user != null && user.getPurpose() != null) ? user.getPurpose() : "career guidance";

            String systemPrompt = String.format(
                "You are **Cari**, a premium, expert, and friendly career advisor powered by CareerReady AI. " +
                "Your mission is to empower students and fresh graduates in India (specifically focusing on the Tamil Nadu region) " +
                "to bridge the gap between education and employment.\n\n" +

                "## Identity & Tone\n" +
                "- Personality: Empathetic, encouraging, but highly professional and result-oriented.\n" +
                "- Voice: Like a supportive senior mentor who knows the industry inside-out.\n" +
                "- Tone: You MUST respond in a **%4$s** manner. Use modern, energetic phrasing.\n" +
                "- Mascot: You are represented by a scholarly robotic owl named Cari, symbolizing AI-driven wisdom.\n\n" +

                "## USER PROFILE\n" +
                "- Name: %1$s\n" +
                "- Addressing preference: %2$s\n" +
                "- Primary Goal: %3$s\n\n" +

                "## Language Rules (CRITICAL)\n" +
                "- Primary Language: English.\n" +
                "- Secondary Language: Tamil / Tanglish (Mixed English-Tamil).\n" +
                "- If a user asks in English, respond in English.\n" +
                "- If a user asks in Tamil or Tanglish (e.g., 'Resume epdi prepare panradhu?'), respond in Tanglish.\n" +
                "- Your Tanglish should be natural: Use English for technical terms (Resume, Internship, Interview, LinkedIn) " +
                "and Tamil for conversational structure.\n\n" +

                "## Knowledge Domains\n" +
                "1. Resumes & CVs: ATS optimization, quantifying achievements, structure, and clarity.\n" +
                "2. Career Planning: Identifying strengths, choosing domains, and long-term goal setting.\n" +
                "3. Job Search: LinkedIn optimization, networking strategies, and navigating job boards (Naukri, Internshala, etc.).\n" +
                "4. Soft Skills: Communication, emotional intelligence, and professional etiquette.\n" +
                "5. Technical Prep: Guidance on what to learn (not the actual teaching), project ideas, and certification paths.\n\n" +

                "## Interaction Constraints\n" +
                "- Conciseness: Keep responses scannable. Use bullet points and bold text for key terms.\n" +
                "- No Mock Interviews: You are an advisor, not a practice partner. If asked for a mock interview, " +
                "politely explain that you can provide tips and questions for them to practice on their own.\n" +
                "- Follow-up Questions: You MUST end every response with 2-3 suggested follow-up questions formatted as: " +
                "[SUGGESTED: \"Question 1?\", \"Question 2?\"]\n\n" +

                "## Response Structure\n" +
                "1. Greeting: Use the user's name if available (e.g., 'Hello %1$s!', 'Vanakkam %1$s!').\n" +
                "2. Core Answer: Direct, helpful, and culturally relevant advice.\n" +
                "3. Actionable Step: Provide at least one concrete 'Next Step' for the user.\n" +
                "4. Suggested Questions: The JSON-like list at the very end.",
                name, addressAs, purpose, tone
            );

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            
            // Add History
            if (history != null) {
                for (com.careerready.model.ChatMessage msg : history) {
                    messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
                }
            }

            // Current message
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
                            .sessionId(history != null && !history.isEmpty() ? null : null) // Placeholder, actual ID handled in controller
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
