package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.dto.ClientRequest;
import com.raffs.LawInsight.dto.ClientResponse;
import com.raffs.LawInsight.dto.ClientSummary;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.ClientMapper;
import com.raffs.LawInsight.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientResponse create(ClientRequest request) {
        var client = clientMapper.toEntity(request);
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return clientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientSummary> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ClientSummary> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable)
                .map(clientMapper::toSummary);
    }

    @Transactional(readOnly = true)
    public List<ClientSummary> findByName(String name) {
        return clientRepository.findByNameContainingIgnoreCase(name).stream()
                .map(clientMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ClientSummary> findByName(String name, Pageable pageable) {
        return clientRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(clientMapper::toSummary);
    }

    @Transactional(readOnly = true)
    public List<ClientSummary> findByType(ClientType type) {
        return clientRepository.findByClientType(type).stream()
                .map(clientMapper::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ClientSummary> findByType(ClientType type, Pageable pageable) {
        return clientRepository.findByClientType(type, pageable)
                .map(clientMapper::toSummary);
    }

    @Transactional
    public ClientResponse update(Long id, ClientRequest request) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        client.setName(request.getName());
        client.setClientType(request.getClientType());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setDocumentNumber(request.getDocumentNumber());
        client.setAddress(request.getAddress());
        client.setNotes(request.getNotes());
        client = clientRepository.save(client);
        return clientMapper.toResponse(client);
    }

    @Transactional
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
}
