package com.raffs.LawInsight.controller;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.dto.ContractRequest;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.dto.ContractSummary;
import com.raffs.LawInsight.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.raffs.LawInsight.service.ContractProcessingService;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractProcessingService contractProcessingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest request) {
        var response = contractService.createContract(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<ContractResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clientId") Long clientId,
            @RequestParam(required = false) String title) {
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        var response = contractService.uploadContract(file, currentUserEmail, clientId, title);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<ContractResponse> findById(@PathVariable Long id) {
        var response = contractService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<Page<ContractSummary>> findAll(
            @RequestParam(required = false) ContractStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(contractService.findByStatus(status, pageable));
        }
        return ResponseEntity.ok(contractService.findAll(pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<Page<ContractSummary>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) FileType fileType,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long uploadedById,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var result = contractService.searchContracts(title, status, fileType, clientId, uploadedById, pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<ContractResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ContractStatus status) {
        var response = contractService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'ATTORNEY', 'PARALEGAL')")
    public ResponseEntity<Void> processAsync(@PathVariable Long id) {
        contractProcessingService.processContractAsync(id);
        return ResponseEntity.accepted().build();
    }
}
