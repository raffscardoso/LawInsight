package com.raffs.LawInsight.repository;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.enumeration.ClientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByDocumentNumber(String documentNumber);

    List<Client> findByNameContainingIgnoreCase(String name);

    Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Client> findByClientType(ClientType clientType);

    Page<Client> findByClientType(ClientType clientType, Pageable pageable);
}
