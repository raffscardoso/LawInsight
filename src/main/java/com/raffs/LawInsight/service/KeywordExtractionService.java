package com.raffs.LawInsight.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeywordExtractionService {

    private final ChatClient chatClient;

    public KeywordExtractionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<String> extractKeywords(String contractText) {
        String prompt = "Extract key legal terms, entities, and keywords from the following contract text. Return ONLY a JSON array of strings. Text: " + contractText;
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        if (response != null && response.startsWith("[")) {
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
