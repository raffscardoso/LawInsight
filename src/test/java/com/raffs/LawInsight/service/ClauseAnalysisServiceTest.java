package com.raffs.LawInsight.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import org.springframework.ai.chat.messages.AssistantMessage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClauseAnalysisServiceTest {

    @Mock
    private ChatModel chatModel;

    private ClauseAnalysisService clauseAnalysisService;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        clauseAnalysisService = new ClauseAnalysisService(chatClient);
    }

    @Test
    void testExtractClauses() {
        // Arrange
        String contractText = "This agreement is between Party A and Party B. The governing law is California.";
        String expectedAiResponse = "[\"Parties Clause: Party A and Party B\", \"Governing Law: California\"]";
        
        when(chatModel.call(any(Prompt.class)))
                .thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(expectedAiResponse)))));

        // Act
        List<String> clauses = clauseAnalysisService.extractClauses(contractText);

        // Assert
        assertNotNull(clauses);
        assertTrue(clauses.contains("Parties Clause: Party A and Party B"));
    }
}
