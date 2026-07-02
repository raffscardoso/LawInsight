package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.RiskAssessment;
import com.raffs.LawInsight.domain.enumeration.RiskAssessmentType;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    List<RiskAssessment> findByContractId(Long contractId);

    List<RiskAssessment> findByContractIdAndRiskLevel(Long contractId, RiskLevel riskLevel);

    List<RiskAssessment> findByContractIdAndType(Long contractId, RiskAssessmentType type);
}
