package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.dto.ContractRequest;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.dto.ContractSummary;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.ContractMapper;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractRepository;
import com.raffs.LawInsight.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ContractMapper contractMapper;

    @Transactional
    public ContractResponse createContract(ContractRequest request) {
        var uploadedBy = userRepository.findById(request.getUploadedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUploadedById()));
        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + request.getClientId()));

        var contract = contractMapper.toEntity(request, uploadedBy, client);
        contract = contractRepository.save(contract);
        return contractMapper.toResponse(contract);
    }

    @Transactional(readOnly = true)
    public ContractResponse findById(Long id) {
        var contract = contractRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        return contractMapper.toResponse(contract);
    }

    @Transactional(readOnly = true)
    public List<ContractSummary> findByStatus(ContractStatus status) {
        return contractRepository.findByStatus(status).stream()
                .map(contractMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContractSummary> findAll() {
        return contractRepository.findAll().stream()
                .map(contractMapper::toSummary)
                .toList();
    }

    @Transactional
    public void deleteContract(Long id) {
        if (!contractRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contract not found with id: " + id);
        }
        contractRepository.deleteById(id);
    }

    @Transactional
    public ContractResponse updateStatus(Long id, ContractStatus status) {
        var contract = contractRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
        contract.setStatus(status);
        contract = contractRepository.save(contract);
        return contractMapper.toResponse(contract);
    }
}
