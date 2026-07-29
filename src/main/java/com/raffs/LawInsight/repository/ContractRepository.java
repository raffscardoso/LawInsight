package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    Page<Contract> findAll(Specification<Contract> spec, Pageable pageable);

    @Query("SELECT c FROM Contract c LEFT JOIN FETCH c.uploadedBy LEFT JOIN FETCH c.client WHERE c.id = :id")
    Optional<Contract> findByIdWithDetails(Long id);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByStatus(ContractStatus status);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    Page<Contract> findByStatus(ContractStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByClientId(Long clientId);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByUploadedById(Long uploadedById);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    List<Contract> findByStatusIn(List<ContractStatus> statuses);

    @EntityGraph(attributePaths = {"uploadedBy", "client"})
    Optional<Contract> findByFileHash(String fileHash);
}
