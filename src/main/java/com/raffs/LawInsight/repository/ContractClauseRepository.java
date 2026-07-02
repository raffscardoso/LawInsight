package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.ContractClause;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractClauseRepository extends JpaRepository<ContractClause, Long> {

    @EntityGraph(attributePaths = {"contract"})
    List<ContractClause> findByContractIdOrderByNumber(Long contractId);

    @EntityGraph(attributePaths = {"contract"})
    List<ContractClause> findByContractIdAndRiskLevel(Long contractId, RiskLevel riskLevel);
}
