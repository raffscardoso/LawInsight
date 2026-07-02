package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.ContractParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractPartyRepository extends JpaRepository<ContractParty, Long> {

    List<ContractParty> findByContractId(Long contractId);

    List<ContractParty> findByContractIdAndIsSignatoryTrue(Long contractId);
}
