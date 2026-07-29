package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.dto.ClientRequest;
import com.raffs.LawInsight.dto.ClientResponse;
import com.raffs.LawInsight.dto.ClientSummary;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.ClientMapper;
import com.raffs.LawInsight.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    private Client createClient() {
        var client = new Client();
        ReflectionTestUtils.setField(client, "id", 1L);
        client.setName("Test Client");
        client.setClientType(ClientType.COMPANY);
        client.setDocumentNumber("00.000.000/0001-00");
        return client;
    }

    @Test
    void shouldCreate() {
        var request = new ClientRequest();
        request.setName("New Client");
        request.setClientType(ClientType.PERSON);
        request.setDocumentNumber("123.456.789-00");

        var saved = createClient();
        when(clientMapper.toEntity(request)).thenReturn(saved);
        when(clientRepository.save(saved)).thenReturn(saved);
        when(clientMapper.toResponse(saved)).thenReturn(new ClientResponse());

        var result = clientService.create(request);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldFindById() {
        var client = createClient();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(new ClientResponse());

        var result = clientService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldFindAll() {
        var client = createClient();
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(clientRepository.findAll(pageable)).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(client), pageable, 1));
        when(clientMapper.toSummary(client)).thenReturn(new ClientSummary());

        var result = clientService.findAll(pageable);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldFindByName() {
        var client = createClient();
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(clientRepository.findByNameContainingIgnoreCase("Test", pageable)).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(client), pageable, 1));
        when(clientMapper.toSummary(client)).thenReturn(new ClientSummary());

        var result = clientService.findByName("Test", pageable);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldUpdate() {
        var existing = createClient();
        var request = new ClientRequest();
        request.setName("Updated");
        request.setClientType(ClientType.PERSON);
        request.setDocumentNumber("999.999.999-99");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clientRepository.save(existing)).thenReturn(existing);
        when(clientMapper.toResponse(existing)).thenReturn(new ClientResponse());

        var result = clientService.update(1L, request);
        assertThat(result).isNotNull();
        assertThat(existing.getName()).isEqualTo("Updated");
    }

    @Test
    void shouldDelete() {
        when(clientRepository.existsById(1L)).thenReturn(true);

        clientService.delete(1L);
        verify(clientRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        when(clientRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> clientService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
