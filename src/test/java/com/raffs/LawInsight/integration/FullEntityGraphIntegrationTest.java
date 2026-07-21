package com.raffs.LawInsight.integration;

import com.raffs.LawInsight.domain.Client;
import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.ContractClause;
import com.raffs.LawInsight.domain.ContractParty;
import com.raffs.LawInsight.domain.ExtractedKeyword;
import com.raffs.LawInsight.domain.RiskAssessment;
import com.raffs.LawInsight.domain.User;
import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.domain.enumeration.KeywordType;
import com.raffs.LawInsight.domain.enumeration.RiskAssessmentType;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import com.raffs.LawInsight.domain.enumeration.UserRole;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractClauseRepository;
import com.raffs.LawInsight.repository.ContractPartyRepository;
import com.raffs.LawInsight.repository.ContractRepository;
import com.raffs.LawInsight.repository.ExtractedKeywordRepository;
import com.raffs.LawInsight.repository.RiskAssessmentRepository;
import com.raffs.LawInsight.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.datasource.url=jdbc:h2:mem:lawinsight-it;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class FullEntityGraphIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractClauseRepository contractClauseRepository;

    @Autowired
    private ContractPartyRepository contractPartyRepository;

    @Autowired
    private ExtractedKeywordRepository extractedKeywordRepository;

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User attorney;
    private Client client;
    private Contract contract;

    @BeforeEach
    void setUp() {
        riskAssessmentRepository.deleteAll();
        extractedKeywordRepository.deleteAll();
        contractPartyRepository.deleteAll();
        contractClauseRepository.deleteAll();
        contractRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        attorney = new User();
        attorney.setEmail("integration-test@lawfirm.com");
        attorney.setPassword("$2a$10$dummyBcryptHash");
        attorney.setFirstName("Jane");
        attorney.setLastName("Dao");
        attorney.setBarNumber("OAB-INTEG");
        attorney.setRole(UserRole.ATTORNEY);
        attorney = userRepository.save(attorney);

        client = new Client();
        client.setName("Integration Corp");
        client.setClientType(ClientType.COMPANY);
        client.setEmail("legal@integration-corp.com");
        client.setDocumentNumber("00.000.000/0001-00");
        client = clientRepository.save(client);

        contract = new Contract();
        contract.setTitle("Integration Test Agreement");
        contract.setOriginalFileName("integration-v1.pdf");
        contract.setFileType(FileType.PDF);
        contract.setExtractedContent("Integration test content for the full entity graph.");
        contract.setFileHash("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        contract.setStatus(ContractStatus.UPLOADED);
        contract.setUploadedBy(attorney);
        contract.setClient(client);
        contract = contractRepository.save(contract);
    }

    @Test
    void shouldPersistFullEntityGraph() {
        var clause = new ContractClause();
        clause.setNumber(1);
        clause.setTitle("Governing Law");
        clause.setContent("Governed by Brazilian law.");
        clause.setRiskLevel(RiskLevel.MEDIUM);
        clause.setContract(contract);
        clause = contractClauseRepository.save(clause);

        var party = new ContractParty();
        party.setName("Integration Corp (Party)");
        party.setRoleInContract("Client");
        party.setDocumentNumber("00.000.000/0001-00");
        party.setIsSignatory(true);
        party.setContract(contract);
        party = contractPartyRepository.save(party);

        var keyword = new ExtractedKeyword();
        keyword.setKeyword("contract_value");
        keyword.setValue("R$ 1,000,000.00");
        keyword.setType(KeywordType.MONEY);
        keyword.setConfidence(0.97);
        keyword.setContract(contract);
        keyword = extractedKeywordRepository.save(keyword);

        var assessment = new RiskAssessment();
        assessment.setType(RiskAssessmentType.CLAUSE_RISK);
        assessment.setRiskLevel(RiskLevel.CRITICAL);
        assessment.setDescription("Unlimited liability clause detected.");
        assessment.setRecommendation("Cap liability at contract value.");
        assessment.setAssessedAt(Instant.now());
        assessment.setContract(contract);
        assessment.setContractClause(clause);
        assessment = riskAssessmentRepository.save(assessment);

        entityManager.clear();

        var fetchedContracts = contractRepository.findByStatus(ContractStatus.UPLOADED);
        assertThat(fetchedContracts).hasSize(1);
        var fetchedContract = fetchedContracts.get(0);
        assertThat(fetchedContract.getTitle()).isEqualTo("Integration Test Agreement");
        assertThat(fetchedContract.getUploadedBy().getEmail()).isEqualTo("integration-test@lawfirm.com");
        assertThat(fetchedContract.getClient().getName()).isEqualTo("Integration Corp");

        var clauses = contractClauseRepository.findByContractIdOrderByNumber(contract.getId());
        assertThat(clauses).hasSize(1);
        assertThat(clauses.get(0).getContract().getTitle()).isEqualTo("Integration Test Agreement");

        var parties = contractPartyRepository.findByContractId(contract.getId());
        assertThat(parties).hasSize(1);
        assertThat(parties.get(0).getContract().getTitle()).isEqualTo("Integration Test Agreement");

        var keywords = extractedKeywordRepository.findByContractId(contract.getId());
        assertThat(keywords).hasSize(1);
        assertThat(keywords.get(0).getContract().getTitle()).isEqualTo("Integration Test Agreement");

        var assessments = riskAssessmentRepository.findByContractId(contract.getId());
        assertThat(assessments).hasSize(1);
        assertThat(assessments.get(0).getContract().getTitle()).isEqualTo("Integration Test Agreement");
        assertThat(assessments.get(0).getContractClause().getTitle()).isEqualTo("Governing Law");
    }

    @Test
    void shouldFetchFullGraphWithSingleQueryPerRepository() {
        var clause = contractClauseRepository.save(createClause());
        contractPartyRepository.save(createParty());
        extractedKeywordRepository.save(createKeyword());
        riskAssessmentRepository.save(createAssessment(clause));

        entityManager.clear();

        var stats = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        var fetched = contractRepository.findByStatus(ContractStatus.UPLOADED);
        assertThat(fetched).hasSize(1);
        assertThat(fetched.get(0).getUploadedBy().getEmail()).isEqualTo("integration-test@lawfirm.com");
        assertThat(stats.getQueryExecutionCount()).isEqualTo(1);

        stats.clear();
        var clauses = contractClauseRepository.findByContractIdOrderByNumber(contract.getId());
        assertThat(clauses.get(0).getContract().getTitle()).isEqualTo("Integration Test Agreement");
        assertThat(stats.getQueryExecutionCount()).isEqualTo(1);
    }

    @Test
    void shouldCascadeDeleteContractToChildren() {
        contractClauseRepository.save(createClause());
        contractPartyRepository.save(createParty());
        extractedKeywordRepository.save(createKeyword());
        contractRepository.delete(contract);

        entityManager.clear();

        assertThat(contractClauseRepository.findByContractIdOrderByNumber(contract.getId())).isEmpty();
        assertThat(contractPartyRepository.findByContractId(contract.getId())).isEmpty();
        assertThat(extractedKeywordRepository.findByContractId(contract.getId())).isEmpty();
        assertThat(riskAssessmentRepository.findByContractId(contract.getId())).isEmpty();
    }

    private ContractClause createClause() {
        var c = new ContractClause();
        c.setNumber(1);
        c.setTitle("Test Clause");
        c.setContent("Clause content.");
        c.setRiskLevel(RiskLevel.LOW);
        c.setContract(contract);
        return c;
    }

    private ContractParty createParty() {
        var p = new ContractParty();
        p.setName("Test Party");
        p.setRoleInContract("Provider");
        p.setContract(contract);
        return p;
    }

    private ExtractedKeyword createKeyword() {
        var k = new ExtractedKeyword();
        k.setKeyword("test");
        k.setValue("value");
        k.setType(KeywordType.OTHER);
        k.setConfidence(0.5);
        k.setContract(contract);
        return k;
    }

    private RiskAssessment createAssessment(ContractClause clause) {
        var a = new RiskAssessment();
        a.setType(RiskAssessmentType.GENERAL);
        a.setRiskLevel(RiskLevel.LOW);
        a.setDescription("Test assessment");
        a.setAssessedAt(Instant.now());
        a.setContract(contract);
        a.setContractClause(clause);
        return a;
    }
}
