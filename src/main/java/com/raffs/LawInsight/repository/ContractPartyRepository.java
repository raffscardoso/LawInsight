package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.ContractParty;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractPartyRepository extends JpaRepository<ContractParty, Long> {

    @EntityGraph(attributePaths = {"contract"})
    List<ContractParty> findByContractId(Long contractId);

    @EntityGraph(attributePaths = {"contract"})
    List<ContractParty> findByContractIdAndIsSignatoryTrue(Long contractId);
}
