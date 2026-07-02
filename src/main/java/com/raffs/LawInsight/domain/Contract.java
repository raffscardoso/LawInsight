package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contracts")
public class Contract extends BaseEntity {

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 500)
    private String originalFileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FileType fileType;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String extractedContent;

    @Column(length = 500)
    private String filePath;

    @Column(nullable = false, updatable = false, length = 64)
    private String fileHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status = ContractStatus.UPLOADED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
