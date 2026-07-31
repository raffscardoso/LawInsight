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

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractProcessingService {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

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
        // Simulated parsing step (to be extended with Spring AI pipeline in Marco 4)
    }
}
