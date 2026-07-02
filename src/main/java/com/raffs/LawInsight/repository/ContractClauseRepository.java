package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.ContractClause;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractClauseRepository extends JpaRepository<ContractClause, Long> {

    List<ContractClause> findByContractIdOrderByNumber(Long contractId);

    List<ContractClause> findByContractIdAndRiskLevel(Long contractId, RiskLevel riskLevel);
}
