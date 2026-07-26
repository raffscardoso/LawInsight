package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ContractSummary {

    private Long id;
    private String title;
    private ContractStatus status;
    private FileType fileType;
    private Instant createdAt;
    private String uploadedByName;
    private String clientName;
}
