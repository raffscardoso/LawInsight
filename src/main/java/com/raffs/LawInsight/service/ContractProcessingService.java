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
    private final ExtractedKeywordRepository keywordRepository;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ContractResponse> processContractAsync(Long contractId) {
        log.info("Starting asynchronous processing for contract ID: {}", contractId);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + contractId));

        try {
            // Transition status to PROCESSING
            contract.setStatus(ContractStatus.PROCESSING);
            contract = contractRepository.save(contract);

            // Orchestration pipeline logic (content validation / parsing stubs)
            performContractAnalysis(contract);

            // Transition status to PROCESSED
            contract.setStatus(ContractStatus.PROCESSED);
            contract = contractRepository.save(contract);

            log.info("Successfully completed async processing for contract ID: {}", contractId);
            return CompletableFuture.completedFuture(contractMapper.toResponse(contract));
        } catch (Exception ex) {
            log.error("Failed to process contract ID: {}", contractId, ex);
            try {
                contract.setStatus(ContractStatus.FAILED);
                contractRepository.save(contract);
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
        
        List<String> clauses = clauseAnalysisService.extractClauses(text);
        List<String> risks = riskAssessmentService.assessRisk(text);
        List<String> keywords = keywordExtractionService.extractKeywords(text);
        
        int i = 1;
        List<ContractClause> clauseEntities = new java.util.ArrayList<>();
        for (String c : clauses) {
            ContractClause clause = new ContractClause();
            clause.setContract(contract);
            clause.setContent(c);
            clause.setNumber(i++);
            clause.setTitle("Extracted Clause " + clause.getNumber());
            clause.setRiskLevel(RiskLevel.LOW);
            clauseEntities.add(clause);
        }
        clauseRepository.saveAll(clauseEntities);
        
        List<RiskAssessment> riskEntities = new java.util.ArrayList<>();
        for (String r : risks) {
            RiskAssessment risk = new RiskAssessment();
            risk.setContract(contract);
            risk.setDescription(r);
            risk.setRiskLevel(RiskLevel.MEDIUM);
            risk.setType(RiskAssessmentType.GENERAL);
            risk.setAssessedAt(Instant.now());
            riskEntities.add(risk);
        }
        riskRepository.saveAll(riskEntities);
        
        List<ExtractedKeyword> keywordEntities = new java.util.ArrayList<>();
        for (String k : keywords) {
            ExtractedKeyword keyword = new ExtractedKeyword();
            keyword.setContract(contract);
            keyword.setKeyword(k);
            keyword.setType(KeywordType.OTHER);
            keyword.setConfidence(1.0);
            keywordEntities.add(keyword);
        }
        keywordRepository.saveAll(keywordEntities);
    }
}
