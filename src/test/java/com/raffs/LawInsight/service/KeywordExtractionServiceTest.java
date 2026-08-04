package com.raffs.LawInsight.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KeywordExtractionServiceTest {

    @Mock
    private ChatModel chatModel;

    private KeywordExtractionService keywordExtractionService;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        keywordExtractionService = new KeywordExtractionService(chatClient);
    }

    @Test
    void testExtractKeywords() {
        // Arrange
        String contractText = "This Non-Disclosure Agreement protects Confidential Information between the Disclosing Party and Receiving Party.";
        String expectedAiResponse = "[\"Non-Disclosure Agreement\", \"Confidential Information\"]";

        when(chatModel.call(any(Prompt.class)))
                .thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(expectedAiResponse)))));

        // Act
        List<String> keywords = keywordExtractionService.extractKeywords(contractText);

        // Assert
        assertNotNull(keywords);
        assertTrue(keywords.contains("Confidential Information"));
    }
}
