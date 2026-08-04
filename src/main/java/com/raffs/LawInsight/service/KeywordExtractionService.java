package com.raffs.LawInsight.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class KeywordExtractionService {

    private final ChatClient chatClient;

    public KeywordExtractionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<String> extractKeywords(String contractText) {
        String prompt = "Extract key legal terms, entities, and keywords from the following contract text. Return ONLY a JSON array of strings. Text: " + contractText;
        
        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(new ParameterizedTypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
