package com.raffs.LawInsight.controller;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.dto.ContractRequest;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.dto.ContractSummary;
import com.raffs.LawInsight.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest request) {
        var response = contractService.createContract(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> findById(@PathVariable Long id) {
        var response = contractService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ContractSummary>> findAll(
            @RequestParam(required = false) ContractStatus status) {
        if (status != null) {
            return ResponseEntity.ok(contractService.findByStatus(status));
        }
        return ResponseEntity.ok(contractService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ContractResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ContractStatus status) {
        var response = contractService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
