package com.raffs.LawInsight.dto;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ContractResponse {

    private Long id;
    private Long version;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private String createdBy;
    private String lastModifiedBy;

    private String title;
    private String originalFileName;
    private FileType fileType;
    private String extractedContent;
    private String filePath;
    private String fileHash;
    private ContractStatus status;

    private UserSummary uploadedBy;
    private ClientSummary client;

    @Getter
    @Setter
    public static class UserSummary {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
    }

    @Getter
    @Setter
    public static class ClientSummary {
        private Long id;
        private String name;
        private String documentNumber;
    }
}
