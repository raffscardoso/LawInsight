package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SseEvent {
    private Long contractId;
    private ContractStatus status;
    private String message;
    @Builder.Default
    private Instant timestamp = Instant.now();
}
