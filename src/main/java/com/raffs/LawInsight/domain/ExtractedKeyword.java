package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.KeywordType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "extracted_keywords")
public class ExtractedKeyword extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(name = "keyword_value", columnDefinition = "TEXT")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private KeywordType type;

    @Column(nullable = false)
    private Double confidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Contract contract;
}
