package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByStatus(ContractStatus status);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByClientId(Long clientId);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByUploadedById(Long uploadedById);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByStatusIn(List<ContractStatus> statuses);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    Optional<Contract> findByFileHash(String fileHash);
}
