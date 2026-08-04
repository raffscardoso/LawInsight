package com.raffs.LawInsight.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClauseAnalysisService {

    private final ChatClient chatClient;

    public ClauseAnalysisService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<String> extractClauses(String contractText) {
        String prompt = "Extract the main clauses from the following contract text. Return ONLY a JSON array of strings, where each string is a clause. Text: " + contractText;
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // Very basic parsing for MVP/TDD
        // Assuming the response is exactly a JSON array of strings
        if (response != null && response.startsWith("[")) {
            // Strip brackets and quotes for simplicity in this minimum implementation
            String clean = response.replaceAll("^\\[|\\]$", "");
            String[] split = clean.split("\",\\s*\"");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].replaceAll("^\"|\"$", "");
            }
            return List.of(split);
        }
        
        return List.of();
    }
}
