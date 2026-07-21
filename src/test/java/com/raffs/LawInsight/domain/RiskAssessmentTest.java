package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.RiskAssessmentType;
import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractClauseRepository;
import com.raffs.LawInsight.repository.ContractRepository;
import com.raffs.LawInsight.repository.RiskAssessmentRepository;
import com.raffs.LawInsight.repository.UserRepository;
import com.raffs.LawInsight.util.TestDataFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RiskAssessmentTest {

    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractClauseRepository contractClauseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Contract contract;
    private ContractClause clause;

    @BeforeEach
    void setUp() {
        riskAssessmentRepository.deleteAll();
        contractClauseRepository.deleteAll();
        contractRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        var attorney = userRepository.save(TestDataFactory.createUser("risk-test"));
        var client = clientRepository.save(
                TestDataFactory.createClient("risk-test", "44.444.444/0001-44"));
        contract = contractRepository.save(
                TestDataFactory.createContract(
                        "3333333333333333333333333333333333333333333333333333333333333333",
                        attorney, client));

        clause = contractClauseRepository.save(
                TestDataFactory.createClause(5, "Indemnification",
                        "Party A shall indemnify Party B against all losses.",
                        RiskLevel.HIGH, contract));
    }

    @Test
    void shouldPersistRiskAssessmentWithAllFields() {
        var assessment = new RiskAssessment();
        assessment.setType(RiskAssessmentType.CLAUSE_RISK);
        assessment.setRiskLevel(RiskLevel.CRITICAL);
        assessment.setDescription("This clause creates unlimited liability exposure.");
        assessment.setRecommendation("Cap liability at contract value.");
        assessment.setAssessedAt(Instant.now());
        assessment.setContract(contract);
        assessment.setContractClause(clause);

        var saved = riskAssessmentRepository.save(assessment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(RiskAssessmentType.CLAUSE_RISK);
        assertThat(saved.getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
        assertThat(saved.getDescription()).contains("unlimited liability");
        assertThat(saved.getRecommendation()).contains("Cap liability");
        assertThat(saved.getAssessedAt()).isNotNull();
    }

    @Test
    void shouldAssociateWithContractAndClause() {
        var assessment = new RiskAssessment();
        assessment.setType(RiskAssessmentType.FINANCIAL_RISK);
        assessment.setRiskLevel(RiskLevel.MEDIUM);
        assessment.setDescription("Financial risk");
        assessment.setAssessedAt(Instant.now());
        assessment.setContract(contract);
        assessment.setContractClause(clause);

        var saved = riskAssessmentRepository.save(assessment);

        assertThat(saved.getContract().getId()).isEqualTo(contract.getId());
        assertThat(saved.getContractClause()).isNotNull();
        assertThat(saved.getContractClause().getId()).isEqualTo(clause.getId());
    }

    @Test
    void shouldAllowNullContractClause() {
        var assessment = new RiskAssessment();
        assessment.setType(RiskAssessmentType.GENERAL);
        assessment.setRiskLevel(RiskLevel.LOW);
        assessment.setDescription("General assessment");
        assessment.setAssessedAt(Instant.now());
        assessment.setContract(contract);

        var saved = riskAssessmentRepository.save(assessment);

        assertThat(saved.getContractClause()).isNull();
    }

    @Test
    void shouldFindAssessmentsByContractId() {
        var a1 = new RiskAssessment();
        a1.setType(RiskAssessmentType.CLAUSE_RISK);
        a1.setRiskLevel(RiskLevel.HIGH);
        a1.setDescription("Risk 1");
        a1.setAssessedAt(Instant.now());
        a1.setContract(contract);
        riskAssessmentRepository.save(a1);

        var a2 = new RiskAssessment();
        a2.setType(RiskAssessmentType.FINANCIAL_RISK);
        a2.setRiskLevel(RiskLevel.MEDIUM);
        a2.setDescription("Risk 2");
        a2.setAssessedAt(Instant.now());
        a2.setContract(contract);
        riskAssessmentRepository.save(a2);

        var assessments = riskAssessmentRepository.findByContractId(contract.getId());

        assertThat(assessments).hasSize(2);
    }

    @Test
    void shouldFindAssessmentsByContractIdAndRiskLevel() {
        var critical = new RiskAssessment();
        critical.setType(RiskAssessmentType.CLAUSE_RISK);
        critical.setRiskLevel(RiskLevel.CRITICAL);
        critical.setDescription("Critical issue");
        critical.setAssessedAt(Instant.now());
        critical.setContract(contract);
        riskAssessmentRepository.save(critical);

        var low = new RiskAssessment();
        low.setType(RiskAssessmentType.GENERAL);
        low.setRiskLevel(RiskLevel.LOW);
        low.setDescription("Minor issue");
        low.setAssessedAt(Instant.now());
        low.setContract(contract);
        riskAssessmentRepository.save(low);

        var criticals = riskAssessmentRepository.findByContractIdAndRiskLevel(
                contract.getId(), RiskLevel.CRITICAL);

        assertThat(criticals).hasSize(1);
        assertThat(criticals.get(0).getDescription()).contains("Critical");
    }

    @Test
    void shouldFindAssessmentsByContractIdAndType() {
        var clauseRisk = new RiskAssessment();
        clauseRisk.setType(RiskAssessmentType.CLAUSE_RISK);
        clauseRisk.setRiskLevel(RiskLevel.HIGH);
        clauseRisk.setDescription("Clause issue");
        clauseRisk.setAssessedAt(Instant.now());
        clauseRisk.setContract(contract);
        riskAssessmentRepository.save(clauseRisk);

        var finRisk = new RiskAssessment();
        finRisk.setType(RiskAssessmentType.FINANCIAL_RISK);
        finRisk.setRiskLevel(RiskLevel.HIGH);
        finRisk.setDescription("Financial issue");
        finRisk.setAssessedAt(Instant.now());
        finRisk.setContract(contract);
        riskAssessmentRepository.save(finRisk);

        var results = riskAssessmentRepository.findByContractIdAndType(
                contract.getId(), RiskAssessmentType.FINANCIAL_RISK);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDescription()).contains("Financial");
    }

    @Test
    void shouldFetchAssociationsInSingleQuery() {
        var assessment = new RiskAssessment();
        assessment.setType(RiskAssessmentType.CLAUSE_RISK);
        assessment.setRiskLevel(RiskLevel.HIGH);
        assessment.setDescription("N+1 test");
        assessment.setAssessedAt(Instant.now());
        assessment.setContract(contract);
        assessment.setContractClause(clause);
        riskAssessmentRepository.saveAndFlush(assessment);
        entityManager.clear();

        var stats = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        var assessments = riskAssessmentRepository.findByContractId(contract.getId());

        assertThat(assessments).hasSize(1);
        assertThat(assessments.get(0).getContract().getTitle()).isEqualTo("Test Agreement");
        assertThat(assessments.get(0).getContractClause().getTitle()).isEqualTo("Indemnification");
        assertThat(stats.getQueryExecutionCount()).isEqualTo(1);
    }
}
