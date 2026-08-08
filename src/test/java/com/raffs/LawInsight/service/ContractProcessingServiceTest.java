package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.ContractMapper;
import com.raffs.LawInsight.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractProcessingServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @Mock
    private ClauseAnalysisService clauseAnalysisService;

    @Mock
    private RiskAssessmentService riskAssessmentService;

    @Mock
    private KeywordExtractionService keywordExtractionService;

    @Mock
    private com.raffs.LawInsight.repository.ContractClauseRepository clauseRepository;

    @Mock
    private com.raffs.LawInsight.repository.RiskAssessmentRepository riskRepository;

    @Mock
    private com.raffs.LawInsight.repository.ExtractedKeywordRepository keywordRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private ContractProcessingService processingService;

    private Contract sampleContract;

    @BeforeEach
    void setUp() {
        sampleContract = new Contract();
        sampleContract.setTitle("Standard Non-Disclosure Agreement");
        sampleContract.setOriginalFileName("nda.pdf");
        sampleContract.setFileType(FileType.PDF);
        sampleContract.setExtractedContent("Confidential information shall be kept secret.");
        sampleContract.setFileHash("a".repeat(64));
        sampleContract.setStatus(ContractStatus.UPLOADED);
    }

    @Test
    void shouldProcessContractSuccessfullyAndTransitionStatus() throws Exception {
        java.util.List<ContractStatus> savedStatuses = new java.util.ArrayList<>();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(sampleContract));
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
            Contract c = inv.getArgument(0);
            savedStatuses.add(c.getStatus());
            return c;
        });
        when(contractMapper.toResponse(any(Contract.class))).thenReturn(new ContractResponse());
        
        when(clauseAnalysisService.extractClauses("Confidential information shall be kept secret.")).thenReturn(java.util.List.of("Confidentiality Clause"));
        when(riskAssessmentService.assessRisk("Confidential information shall be kept secret.")).thenReturn(java.util.List.of("High Risk: vague terms"));
        when(keywordExtractionService.extractKeywords("Confidential information shall be kept secret.")).thenReturn(java.util.List.of("Confidentiality"));

        CompletableFuture<ContractResponse> future = processingService.processContractAsync(1L);
        ContractResponse response = future.get();

        assertThat(response).isNotNull();
        assertThat(sampleContract.getStatus()).isEqualTo(ContractStatus.PROCESSED);
        assertThat(savedStatuses).contains(ContractStatus.PROCESSING, ContractStatus.PROCESSED);
        
        verify(clauseRepository).saveAll(any());
        verify(riskRepository).saveAll(any());
        verify(keywordRepository).saveAll(any());
        
        verify(sseService, atLeastOnce()).emitEvent(eq(1L), any(com.raffs.LawInsight.dto.SseEvent.class));
    }

    @Test
    void shouldHandleFailureAndTransitionStatusToFailed() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(sampleContract));
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
            Contract c = inv.getArgument(0);
            if (c.getStatus() == ContractStatus.PROCESSING) {
                throw new RuntimeException("Processing engine database error");
            }
            return c;
        });

        CompletableFuture<ContractResponse> future = processingService.processContractAsync(1L);

        assertThatThrownBy(future::get)
                .hasCauseInstanceOf(RuntimeException.class);

        assertThat(sampleContract.getStatus()).isEqualTo(ContractStatus.FAILED);
    }

    @Test
    void shouldThrowExceptionWhenContractNotFound() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processingService.processContractAsync(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
