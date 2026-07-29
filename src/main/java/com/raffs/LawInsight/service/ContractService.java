package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.dto.ContractRequest;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.dto.ContractSummary;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.ContractMapper;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractRepository;
import com.raffs.LawInsight.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ContractMapper contractMapper;
    private final PdfExtractionService pdfExtractionService;

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
    public Page<ContractSummary> findByStatus(ContractStatus status, Pageable pageable) {
        return contractRepository.findByStatus(status, pageable)
                .map(contractMapper::toSummary);
    }

    @Transactional(readOnly = true)
    public List<ContractSummary> findAll() {
        return contractRepository.findAll().stream()
                .map(contractMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ContractSummary> findAll(Pageable pageable) {
        return contractRepository.findAll(pageable)
                .map(contractMapper::toSummary);
    }

    @Transactional
    public void deleteContract(Long id) {
        if (!contractRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contract not found with id: " + id);
        }
        contractRepository.deleteById(id);
    }

    @Transactional
    public ContractResponse uploadContract(MultipartFile file, Long uploadedById, Long clientId, String title) {
        var uploadedBy = userRepository.findById(uploadedById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + uploadedById));
        var client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        var content = extractContent(file);
        var hash = computeSha256(file);
        var fileType = resolveFileType(file);

        var request = new ContractRequest();
        request.setTitle(title != null ? title : file.getOriginalFilename());
        request.setOriginalFileName(file.getOriginalFilename());
        request.setFileType(fileType);
        request.setExtractedContent(content);
        request.setFileHash(hash);
        request.setUploadedById(uploadedById);
        request.setClientId(clientId);
        request.setStatus(ContractStatus.UPLOADED);

        var contract = contractMapper.toEntity(request, uploadedBy, client);
        contract = contractRepository.save(contract);
        return contractMapper.toResponse(contract);
    }

    private String extractContent(MultipartFile file) {
        try {
            var bytes = file.getBytes();
            var name = file.getOriginalFilename();
            if (name != null && name.toLowerCase().endsWith(".pdf")) {
                return pdfExtractionService.extractText(bytes);
            }
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }
    }

    private String computeSha256(MultipartFile file) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Failed to compute file hash", e);
        }
    }

    private FileType resolveFileType(MultipartFile file) {
        var name = file.getOriginalFilename();
        if (name == null) return FileType.TXT;
        var lower = name.toLowerCase();
        if (lower.endsWith(".pdf")) return FileType.PDF;
        if (lower.endsWith(".docx")) return FileType.DOCX;
        return FileType.TXT;
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
