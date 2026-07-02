package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.ExtractedKeyword;
import com.raffs.LawInsight.domain.enumeration.KeywordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtractedKeywordRepository extends JpaRepository<ExtractedKeyword, Long> {

    List<ExtractedKeyword> findByContractId(Long contractId);

    List<ExtractedKeyword> findByContractIdAndType(Long contractId, KeywordType type);

    List<ExtractedKeyword> findByTypeAndConfidenceGreaterThan(KeywordType type, Double confidence);
}
