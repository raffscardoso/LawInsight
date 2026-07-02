package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByStatus(ContractStatus status);

    List<Contract> findByClientId(Long clientId);

    List<Contract> findByUploadedById(Long uploadedById);

    List<Contract> findByStatusIn(List<ContractStatus> statuses);

    Optional<Contract> findByFileHash(String fileHash);
}
