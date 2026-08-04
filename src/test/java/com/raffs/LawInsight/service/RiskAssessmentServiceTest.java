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
public class RiskAssessmentServiceTest {

    @Mock
    private ChatModel chatModel;

    private RiskAssessmentService riskAssessmentService;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        riskAssessmentService = new RiskAssessmentService(chatClient);
    }

    @Test
    void testAssessRisk() {
        // Arrange
        String contractText = "This contract can be terminated by Party A at any time without notice.";
        String expectedAiResponse = "[\"High Risk: Unilateral termination without notice\"]";

        when(chatModel.call(any(Prompt.class)))
                .thenReturn(new ChatResponse(List.of(new Generation(new AssistantMessage(expectedAiResponse)))));

        // Act
        List<String> risks = riskAssessmentService.assessRisk(contractText);

        // Assert
        assertNotNull(risks);
        assertTrue(risks.contains("High Risk: Unilateral termination without notice"));
    }
}
