package com.raffs.LawInsight.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

@Service
public class RiskAssessmentService {

    private final ChatClient chatClient;

    public RiskAssessmentService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public List<String> assessRisk(String contractText) {
        String prompt = "Identify potential legal and business risks in the following contract text. Return ONLY a JSON array of strings, where each string describes a specific risk. Text: " + contractText;
        
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
