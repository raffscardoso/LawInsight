package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractRequest {

    @NotBlank
    @Size(max = 300)
    private String title;

    @NotBlank
    @Size(max = 500)
    private String originalFileName;

    @NotNull
    private FileType fileType;

    @NotBlank
    private String extractedContent;

    @NotBlank
    @Size(min = 64, max = 64)
    private String fileHash;

    @NotNull
    private Long uploadedById;

    @NotNull
    private Long clientId;

    private ContractStatus status;
}
