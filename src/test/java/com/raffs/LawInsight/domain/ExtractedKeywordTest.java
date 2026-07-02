package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.domain.enumeration.KeywordType;
import com.raffs.LawInsight.domain.enumeration.UserRole;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractRepository;
import com.raffs.LawInsight.repository.ExtractedKeywordRepository;
import com.raffs.LawInsight.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ExtractedKeywordTest {

    @Autowired
    private ExtractedKeywordRepository extractedKeywordRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Contract contract;

    @BeforeEach
    void setUp() {
        extractedKeywordRepository.deleteAll();
        contractRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        var attorney = new User();
        attorney.setEmail("kw-test@lawfirm.com");
        attorney.setPassword("$2a$10$dummyBcryptHash");
        attorney.setFirstName("KW");
        attorney.setLastName("Test");
        attorney.setBarNumber("OAB-KEYWORD");
        attorney.setRole(UserRole.ATTORNEY);
        attorney = userRepository.save(attorney);

        var client = new Client();
        client.setName("KW Test Client");
        client.setClientType(ClientType.COMPANY);
        client.setEmail("kw-client@test.com");
        client.setDocumentNumber("33.333.333/0001-33");
        client = clientRepository.save(client);

        contract = new Contract();
        contract.setTitle("Test Agreement");
        contract.setOriginalFileName("test.pdf");
        contract.setFileType(FileType.PDF);
        contract.setExtractedContent("Test content");
        contract.setFileHash("2222222222222222222222222222222222222222222222222222222222222222");
        contract.setStatus(ContractStatus.UPLOADED);
        contract.setUploadedBy(attorney);
        contract.setClient(client);
        contract = contractRepository.save(contract);
    }

    @Test
    void shouldPersistExtractedKeywordWithAllFields() {
        var kw = new ExtractedKeyword();
        kw.setKeyword("contract_value");
        kw.setValue("R$ 500,000.00");
        kw.setType(KeywordType.MONEY);
        kw.setConfidence(0.95);
        kw.setContract(contract);

        var saved = extractedKeywordRepository.save(kw);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getKeyword()).isEqualTo("contract_value");
        assertThat(saved.getValue()).isEqualTo("R$ 500,000.00");
        assertThat(saved.getType()).isEqualTo(KeywordType.MONEY);
        assertThat(saved.getConfidence()).isEqualTo(0.95);
    }

    @Test
    void shouldAssociateKeywordWithContract() {
        var kw = new ExtractedKeyword();
        kw.setKeyword("party_name");
        kw.setValue("Acme Corp");
        kw.setType(KeywordType.COMPANY_NAME);
        kw.setConfidence(0.85);
        kw.setContract(contract);

        var saved = extractedKeywordRepository.save(kw);

        assertThat(saved.getContract()).isNotNull();
        assertThat(saved.getContract().getId()).isEqualTo(contract.getId());
    }

    @Test
    void shouldFindKeywordsByContractId() {
        var kw1 = new ExtractedKeyword();
        kw1.setKeyword("value");
        kw1.setValue("1000");
        kw1.setType(KeywordType.MONEY);
        kw1.setConfidence(0.95);
        kw1.setContract(contract);
        extractedKeywordRepository.save(kw1);

        var kw2 = new ExtractedKeyword();
        kw2.setKeyword("date");
        kw2.setValue("2026-01-01");
        kw2.setType(KeywordType.DATE);
        kw2.setConfidence(0.99);
        kw2.setContract(contract);
        extractedKeywordRepository.save(kw2);

        var keywords = extractedKeywordRepository.findByContractId(contract.getId());

        assertThat(keywords).hasSize(2);
    }

    @Test
    void shouldFindKeywordsByContractIdAndType() {
        var money = new ExtractedKeyword();
        money.setKeyword("total");
        money.setValue("500000");
        money.setType(KeywordType.MONEY);
        money.setConfidence(0.85);
        money.setContract(contract);
        extractedKeywordRepository.save(money);

        var date = new ExtractedKeyword();
        date.setKeyword("start");
        date.setValue("2026-06-01");
        date.setType(KeywordType.DATE);
        date.setConfidence(0.95);
        date.setContract(contract);
        extractedKeywordRepository.save(date);

        var moneyKeywords = extractedKeywordRepository.findByContractIdAndType(
                contract.getId(), KeywordType.MONEY);

        assertThat(moneyKeywords).hasSize(1);
        assertThat(moneyKeywords.get(0).getKeyword()).isEqualTo("total");
    }

    @Test
    void shouldFindKeywordsByTypeAndMinConfidence() {
        var highConf = new ExtractedKeyword();
        highConf.setKeyword("jurisdiction");
        highConf.setValue("SP");
        highConf.setType(KeywordType.JURISDICTION);
        highConf.setConfidence(0.98);
        highConf.setContract(contract);
        extractedKeywordRepository.save(highConf);

        var lowConf = new ExtractedKeyword();
        lowConf.setKeyword("jurisdiction");
        lowConf.setValue("RJ");
        lowConf.setType(KeywordType.JURISDICTION);
        lowConf.setConfidence(0.45);
        lowConf.setContract(contract);
        extractedKeywordRepository.save(lowConf);

        var results = extractedKeywordRepository.findByTypeAndConfidenceGreaterThan(
                KeywordType.JURISDICTION, 0.9);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getValue()).isEqualTo("SP");
    }
}
