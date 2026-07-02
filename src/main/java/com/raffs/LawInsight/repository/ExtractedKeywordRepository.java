package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.ExtractedKeyword;
import com.raffs.LawInsight.domain.enumeration.KeywordType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtractedKeywordRepository extends JpaRepository<ExtractedKeyword, Long> {

    @EntityGraph(attributePaths = {"contract"})
    List<ExtractedKeyword> findByContractId(Long contractId);

    @EntityGraph(attributePaths = {"contract"})
    List<ExtractedKeyword> findByContractIdAndType(Long contractId, KeywordType type);

    @EntityGraph(attributePaths = {"contract"})
    List<ExtractedKeyword> findByTypeAndConfidenceGreaterThan(KeywordType type, Double confidence);
}
