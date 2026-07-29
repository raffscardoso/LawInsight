package com.raffs.LawInsight.service;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.domain.enumeration.UserRole;
import com.raffs.LawInsight.dto.ContractRequest;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.mapper.ContractMapper;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractRepository;
import com.raffs.LawInsight.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ContractMapper contractMapper;

    @Mock
    private PdfExtractionService pdfExtractionService;

    @InjectMocks
    private ContractService contractService;

    private User createUser() {
        var user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        user.setEmail("attorney@law.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.ATTORNEY);
        return user;
    }

    private Client createClient() {
        var client = new Client();
        ReflectionTestUtils.setField(client, "id", 1L);
        client.setName("Test Client");
        client.setClientType(ClientType.COMPANY);
        client.setDocumentNumber("00.000.000/0001-00");
        return client;
    }

    private Contract createContract(User user, Client client) {
        var contract = new Contract();
        ReflectionTestUtils.setField(contract, "id", 1L);
        contract.setTitle("Test Contract");
        contract.setOriginalFileName("test.pdf");
        contract.setFileType(FileType.PDF);
        contract.setExtractedContent("content");
        contract.setFileHash("a".repeat(64));
        contract.setStatus(ContractStatus.UPLOADED);
        contract.setUploadedBy(user);
        contract.setClient(client);
        return contract;
    }

    @Test
    void shouldCreateContract() {
        var user = createUser();
        var client = createClient();
        var request = new ContractRequest();
        request.setTitle("New Contract");
        request.setOriginalFileName("doc.pdf");
        request.setFileType(FileType.PDF);
        request.setExtractedContent("content");
        request.setFileHash("b".repeat(64));
        request.setUploadedById(1L);
        request.setClientId(1L);

        var saved = createContract(user, client);
        saved.setTitle("New Contract");
        saved.setFileHash("b".repeat(64));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(contractMapper.toEntity(any(ContractRequest.class), any(User.class), any(Client.class))).thenReturn(saved);
        when(contractRepository.save(any(Contract.class))).thenReturn(saved);
        when(contractMapper.toResponse(any(Contract.class))).thenReturn(new ContractResponse());

        var result = contractService.createContract(request);

        assertThat(result).isNotNull();
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        var request = new ContractRequest();
        request.setUploadedById(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldThrowWhenClientNotFoundOnCreate() {
        var request = new ContractRequest();
        request.setUploadedById(1L);
        request.setClientId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser()));
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client not found");
    }

    @Test
    void shouldFindById() {
        var user = createUser();
        var client = createClient();
        var contract = createContract(user, client);

        when(contractRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(contract));
        when(contractMapper.toResponse(contract)).thenReturn(new ContractResponse());

        var result = contractService.findById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void shouldThrowWhenNotFoundById() {
        when(contractRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Contract not found");
    }

    @Test
    void shouldFindByStatus() {
        var contract = createContract(createUser(), createClient());
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(contractRepository.findByStatus(ContractStatus.UPLOADED, pageable))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(contract), pageable, 1));
        when(contractMapper.toSummary(contract)).thenReturn(null);

        var result = contractService.findByStatus(ContractStatus.UPLOADED, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldFindAll() {
        var contract = createContract(createUser(), createClient());
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(contractRepository.findAll(pageable))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(contract), pageable, 1));
        when(contractMapper.toSummary(contract)).thenReturn(null);

        var result = contractService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldDeleteContract() {
        when(contractRepository.existsById(1L)).thenReturn(true);

        contractService.deleteContract(1L);

        verify(contractRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        when(contractRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> contractService.deleteContract(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Contract not found");
    }

    @Test
    void shouldUploadContract() throws Exception {
        var user = createUser();
        var client = createClient();
        var file = new MockMultipartFile("file", "agreement.pdf", "application/pdf", "dummy content".getBytes());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(pdfExtractionService.extractText(any())).thenReturn("extracted");
        when(contractMapper.toEntity(any(ContractRequest.class), any(User.class), any(Client.class))).thenAnswer(invocation -> {
            var req = invocation.<ContractRequest>getArgument(0);
            var c = new Contract();
            c.setTitle(req.getTitle());
            c.setFileHash(req.getFileHash());
            c.setStatus(req.getStatus());
            c.setUploadedBy(invocation.getArgument(1));
            c.setClient(invocation.getArgument(2));
            return c;
        });
        when(contractRepository.save(any(Contract.class))).thenAnswer(invocation -> {
            var c = invocation.<Contract>getArgument(0);
            ReflectionTestUtils.setField(c, "id", 1L);
            return c;
        });
        when(contractMapper.toResponse(any(Contract.class))).thenReturn(new ContractResponse());

        var result = contractService.uploadContract(file, 1L, 1L, "Custom Title");

        assertThat(result).isNotNull();
        var captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractRepository).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Custom Title");
        assertThat(saved.getStatus()).isEqualTo(ContractStatus.UPLOADED);
        assertThat(saved.getFileHash()).hasSize(64);
    }

    @Test
    void shouldUpdateStatus() {
        var user = createUser();
        var client = createClient();
        var contract = createContract(user, client);

        when(contractRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any(Contract.class))).thenReturn(contract);
        when(contractMapper.toResponse(any(Contract.class))).thenReturn(new ContractResponse());

        var result = contractService.updateStatus(1L, ContractStatus.PROCESSED);

        assertThat(result).isNotNull();
        assertThat(contract.getStatus()).isEqualTo(ContractStatus.PROCESSED);
    }
}
