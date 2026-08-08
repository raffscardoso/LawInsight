package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.ContractMapper;
import com.raffs.LawInsight.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import com.raffs.LawInsight.domain.ContractClause;
import com.raffs.LawInsight.domain.ExtractedKeyword;
import com.raffs.LawInsight.domain.RiskAssessment;
import com.raffs.LawInsight.domain.enumeration.KeywordType;
import com.raffs.LawInsight.domain.enumeration.RiskAssessmentType;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import com.raffs.LawInsight.repository.ContractClauseRepository;
import com.raffs.LawInsight.repository.ExtractedKeywordRepository;
import com.raffs.LawInsight.repository.RiskAssessmentRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.raffs.LawInsight.dto.SseEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractProcessingService {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final ClauseAnalysisService clauseAnalysisService;
    private final RiskAssessmentService riskAssessmentService;
    private final KeywordExtractionService keywordExtractionService;
    private final ContractClauseRepository clauseRepository;
    private final RiskAssessmentRepository riskRepository;
    private final com.raffs.LawInsight.repository.ExtractedKeywordRepository keywordRepository;
    private final SseService sseService;

    @Async("taskExecutor")
    public CompletableFuture<ContractResponse> processContractAsync(Long contractId) {
        log.info("Starting asynchronous processing for contract ID: {}", contractId);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractId));

        try {
            // Transition status to PROCESSING
            contract.setStatus(ContractStatus.PROCESSING);
            contract = contractRepository.save(contract);
            
            sseService.emitEvent(contractId, SseEvent.builder()
                    .contractId(contractId)
                    .status(ContractStatus.PROCESSING)
                    .message("Started AI analysis...")
                    .build());

            // Orchestration pipeline logic (content validation / parsing stubs)
            performContractAnalysis(contract);

            // Transition status to PROCESSED
            contract.setStatus(ContractStatus.PROCESSED);
            contract = contractRepository.save(contract);
            
            sseService.emitEvent(contractId, SseEvent.builder()
                    .contractId(contractId)
                    .status(ContractStatus.PROCESSED)
                    .message("Contract fully processed successfully.")
                    .build());

            log.info("Successfully completed async processing for contract ID: {}", contractId);
            return CompletableFuture.completedFuture(contractMapper.toResponse(contract));
        } catch (Exception ex) {
            log.error("Failed to process contract ID: {}", contractId, ex);
            try {
                contract.setStatus(ContractStatus.FAILED);
                contractRepository.save(contract);
                
                sseService.emitEvent(contractId, SseEvent.builder()
                        .contractId(contractId)
                        .status(ContractStatus.FAILED)
                        .message("Processing failed: " + ex.getMessage())
                        .build());
            } catch (Exception saveEx) {
                log.error("Failed to update status to FAILED for contract ID: {}", contractId, saveEx);
            }
            return CompletableFuture.failedFuture(ex);
        }
    }

    private void performContractAnalysis(Contract contract) {
        if (contract.getExtractedContent() == null || contract.getExtractedContent().isBlank()) {
            throw new IllegalArgumentException("Cannot process contract with empty content");
        }
        String text = contract.getExtractedContent();
        
        sseService.emitEvent(contract.getId(), SseEvent.builder()
                .contractId(contract.getId())
                .status(ContractStatus.PROCESSING)
                .message("Extracting clauses...")
                .build());
        List<String> clauses = clauseAnalysisService.extractClauses(text);
        
        sseService.emitEvent(contract.getId(), SseEvent.builder()
                .contractId(contract.getId())
                .status(ContractStatus.PROCESSING)
                .message("Assessing risks...")
                .build());
        List<String> risks = riskAssessmentService.assessRisk(text);
        
        sseService.emitEvent(contract.getId(), SseEvent.builder()
                .contractId(contract.getId())
                .status(ContractStatus.PROCESSING)
                .message("Extracting keywords...")
                .build());
        List<String> keywords = keywordExtractionService.extractKeywords(text);
        
        // Save Clauses
        java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(1);
        List<ContractClause> clauseEntities = clauses.stream().map(c -> {
            ContractClause clause = new ContractClause();
            clause.setContract(contract);
            clause.setContent(c);
            clause.setNumber(counter.getAndIncrement());
            clause.setTitle("Extracted Clause " + clause.getNumber());
            clause.setRiskLevel(RiskLevel.LOW);
            return clause;
        }).toList();
        clauseRepository.saveAll(clauseEntities);
        
        // Save Risks
        List<RiskAssessment> riskEntities = risks.stream().map(r -> {
            RiskAssessment risk = new RiskAssessment();
            risk.setContract(contract);
            risk.setDescription(r);
            risk.setRiskLevel(RiskLevel.MEDIUM);
            risk.setType(RiskAssessmentType.GENERAL);
            risk.setAssessedAt(Instant.now());
            return risk;
        }).toList();
        riskRepository.saveAll(riskEntities);
        
        // Save Keywords
        List<ExtractedKeyword> keywordEntities = keywords.stream().map(k -> {
            ExtractedKeyword keyword = new ExtractedKeyword();
            keyword.setContract(contract);
            keyword.setKeyword(k);
            keyword.setType(KeywordType.OTHER);
            keyword.setConfidence(1.0);
            return keyword;
        }).toList();
        keywordRepository.saveAll(keywordEntities);
    }
}
