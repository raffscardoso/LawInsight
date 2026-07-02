package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.domain.enumeration.UserRole;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractRepository;
import com.raffs.LawInsight.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ContractTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    private User attorney;
    private Client client;

    @BeforeEach
    void setUp() {
        contractRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        attorney = new User();
        attorney.setEmail("contract-attorney@lawfirm.com");
        attorney.setPassword("$2a$10$dummyBcryptHash");
        attorney.setFirstName("Jane");
        attorney.setLastName("Smith");
        attorney.setBarNumber("OAB-999888");
        attorney.setRole(UserRole.ATTORNEY);
        attorney = userRepository.save(attorney);

        client = new Client();
        client.setName("Tech Startup Ltda");
        client.setClientType(ClientType.COMPANY);
        client.setEmail("legal@techstartup.com");
        client.setDocumentNumber("99.888.777/0001-66");
        client = clientRepository.save(client);
    }

    private Contract createValidContract() {
        var contract = new Contract();
        contract.setTitle("Software Development Agreement");
        contract.setOriginalFileName("dev-agreement-2026.pdf");
        contract.setFileType(FileType.PDF);
        contract.setExtractedContent(
                "This Software Development Agreement is entered into between Tech Startup Ltda " +
                "and the Developer. The total contract value is R$ 500,000.00. " +
                "Jurisdiction: Sao Paulo, SP. Term: 12 months. " +
                "Clause 1 — Scope of Work: The Developer shall deliver... ".repeat(50));
        contract.setFilePath("/uploads/dev-agreement-2026.pdf");
        contract.setFileHash("a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2");
        contract.setStatus(ContractStatus.UPLOADED);
        contract.setUploadedBy(attorney);
        contract.setClient(client);
        return contract;
    }

    @Test
    void shouldPersistContractWithAllFields() {
        var contract = createValidContract();
        var saved = contractRepository.save(contract);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Software Development Agreement");
        assertThat(saved.getOriginalFileName()).isEqualTo("dev-agreement-2026.pdf");
        assertThat(saved.getFileType()).isEqualTo(FileType.PDF);
        assertThat(saved.getExtractedContent()).contains("R$ 500,000.00");
        assertThat(saved.getFilePath()).isEqualTo("/uploads/dev-agreement-2026.pdf");
        assertThat(saved.getFileHash()).isEqualTo(
                "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2");
        assertThat(saved.getStatus()).isEqualTo(ContractStatus.UPLOADED);
    }

    @Test
    void shouldPersistManyToOneAssociations() {
        var contract = createValidContract();
        var saved = contractRepository.save(contract);

        assertThat(saved.getUploadedBy()).isNotNull();
        assertThat(saved.getUploadedBy().getEmail()).isEqualTo("contract-attorney@lawfirm.com");
        assertThat(saved.getClient()).isNotNull();
        assertThat(saved.getClient().getName()).isEqualTo("Tech Startup Ltda");
    }

    @Test
    void shouldDefaultStatusToUploadedInMemory() {
        var contract = new Contract();
        assertThat(contract.getStatus()).isEqualTo(ContractStatus.UPLOADED);
    }

    @Test
    void shouldPersistWithSpecifiedStatus() {
        var contract = createValidContract();
        contract.setStatus(ContractStatus.PROCESSED);

        var saved = contractRepository.save(contract);

        assertThat(saved.getStatus()).isEqualTo(ContractStatus.PROCESSED);
    }
}
