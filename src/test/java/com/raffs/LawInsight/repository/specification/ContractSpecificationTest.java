package com.raffs.LawInsight.repository.specification;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.User;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ContractSpecificationTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("atty@law.com");
        user.setPassword("hashedpassword123");
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setRole(UserRole.ATTORNEY);
        user = userRepository.save(user);

        client = new Client();
        client.setName("Acme Corp");
        client.setClientType(ClientType.COMPANY);
        client.setDocumentNumber("11.222.333/0001-44");
        client = clientRepository.save(client);

        var c1 = new Contract();
        c1.setTitle("Software License Agreement");
        c1.setOriginalFileName("license.pdf");
        c1.setFileType(FileType.PDF);
        c1.setExtractedContent("Software license terms");
        c1.setFileHash("1".repeat(64));
        c1.setStatus(ContractStatus.UPLOADED);
        c1.setUploadedBy(user);
        c1.setClient(client);
        contractRepository.save(c1);

        var c2 = new Contract();
        c2.setTitle("Employment Services Contract");
        c2.setOriginalFileName("employment.txt");
        c2.setFileType(FileType.TXT);
        c2.setExtractedContent("Employment terms");
        c2.setFileHash("2".repeat(64));
        c2.setStatus(ContractStatus.PROCESSED);
        c2.setUploadedBy(user);
        c2.setClient(client);
        contractRepository.save(c2);
    }

    @Test
    void shouldFilterByTitleKeyword() {
        var spec = ContractSpecification.filterBy("license", null, null, null, null);
        var page = contractRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Software License Agreement");
    }

    @Test
    void shouldFilterByStatusAndFileType() {
        var spec = ContractSpecification.filterBy(null, ContractStatus.PROCESSED, FileType.TXT, null, null);
        var page = contractRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getOriginalFileName()).isEqualTo("employment.txt");
    }

    @Test
    void shouldFilterByClientIdAndUploadedById() {
        var spec = ContractSpecification.filterBy(null, null, null, client.getId(), user.getId());
        var page = contractRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void shouldReturnEmptyWhenNoMatch() {
        var spec = ContractSpecification.filterBy("Nonexistent", null, null, null, null);
        var page = contractRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).isEmpty();
    }
}
