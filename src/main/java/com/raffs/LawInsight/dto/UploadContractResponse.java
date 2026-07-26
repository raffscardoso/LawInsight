package com.raffs.LawInsight.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadContractResponse {

    private ContractResponse contract;
    private String extractedTextPreview;
}
