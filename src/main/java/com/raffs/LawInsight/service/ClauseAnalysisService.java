package com.raffs.LawInsight.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class ClauseAnalysisService {

    private final ChatClient chatClient;

    public ClauseAnalysisService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<String> extractClauses(String contractText) {
        String prompt = "Extract the main clauses from the following contract text. Return ONLY a JSON array of strings, where each string is a clause. Text: " + contractText;
        
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
