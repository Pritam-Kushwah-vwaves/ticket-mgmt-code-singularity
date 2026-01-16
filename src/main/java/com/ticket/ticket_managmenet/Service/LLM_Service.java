package com.ticket.ticket_managmenet.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LLM_Service {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    private static final int MAX_CHARS = 12000;

    public String summarizeText(String text) {

        if (text == null || text.isBlank()) {
            return "No content to summarize";
        }

        if (text.length() > MAX_CHARS) {
            text = text.substring(0, MAX_CHARS);
        }

        // ✅ SIMPLE RestTemplate (NO builder)
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", "Summarize this document in 4-5 lines and give only summarized" +
                                        "text dont give any other content:\n" + text
                        )
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(apiUrl, request, String.class);

        JsonObject json = JsonParser
                .parseString(response.getBody())
                .getAsJsonObject();

        return json
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content")
                .getAsString();
    }
}
