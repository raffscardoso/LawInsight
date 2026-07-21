package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.RiskLevel;
import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractClauseRepository;
import com.raffs.LawInsight.repository.ContractRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ContractClauseTest {

    @Autowired
    private ContractClauseRepository contractClauseRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Contract contract;

    @BeforeEach
    void setUp() {
        contractClauseRepository.deleteAll();
        contractRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        var attorney = userRepository.save(TestDataFactory.createUser("clause-test"));
        var client = clientRepository.save(
                TestDataFactory.createClient("clause-test", "11.111.111/0001-11"));
        contract = contractRepository.save(
                TestDataFactory.createContract(
                        "0000000000000000000000000000000000000000000000000000000000000000",
                        attorney, client));
    }

    @Test
    void shouldPersistContractClauseWithAllFields() {
        var clause = new ContractClause();
        clause.setNumber(1);
        clause.setTitle("Governing Law");
        clause.setContent("This agreement shall be governed by the laws of Brazil.");
        clause.setRiskLevel(RiskLevel.MEDIUM);
        clause.setCategory("Jurisdiction");
        clause.setContract(contract);

        var saved = contractClauseRepository.save(clause);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNumber()).isEqualTo(1);
        assertThat(saved.getTitle()).isEqualTo("Governing Law");
        assertThat(saved.getContent()).contains("governed by the laws of Brazil");
        assertThat(saved.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(saved.getCategory()).isEqualTo("Jurisdiction");
    }

    @Test
    void shouldAssociateClauseWithContract() {
        var clause = new ContractClause();
        clause.setNumber(1);
        clause.setTitle("Test");
        clause.setContent("Test content");
        clause.setRiskLevel(RiskLevel.LOW);
        clause.setContract(contract);

        var saved = contractClauseRepository.save(clause);

        assertThat(saved.getContract()).isNotNull();
        assertThat(saved.getContract().getId()).isEqualTo(contract.getId());
    }

    @Test
    void shouldFindClausesByContractIdOrderedByNumber() {
        var clause2 = new ContractClause();
        clause2.setNumber(2);
        clause2.setTitle("Clause B");
        clause2.setContent("Content B");
        clause2.setRiskLevel(RiskLevel.LOW);
        clause2.setContract(contract);
        contractClauseRepository.save(clause2);

        var clause1 = new ContractClause();
        clause1.setNumber(1);
        clause1.setTitle("Clause A");
        clause1.setContent("Content A");
        clause1.setRiskLevel(RiskLevel.HIGH);
        clause1.setContract(contract);
        contractClauseRepository.save(clause1);

        var clauses = contractClauseRepository.findByContractIdOrderByNumber(contract.getId());

        assertThat(clauses).hasSize(2);
        assertThat(clauses.get(0).getNumber()).isEqualTo(1);
        assertThat(clauses.get(1).getNumber()).isEqualTo(2);
    }

    @Test
    void shouldFindClausesByContractIdAndRiskLevel() {
        var lowClause = new ContractClause();
        lowClause.setNumber(1);
        lowClause.setTitle("Low Risk");
        lowClause.setContent("Content");
        lowClause.setRiskLevel(RiskLevel.LOW);
        lowClause.setContract(contract);
        contractClauseRepository.save(lowClause);

        var highClause = new ContractClause();
        highClause.setNumber(2);
        highClause.setTitle("High Risk");
        highClause.setContent("Content");
        highClause.setRiskLevel(RiskLevel.HIGH);
        highClause.setContract(contract);
        contractClauseRepository.save(highClause);

        var highClauses = contractClauseRepository.findByContractIdAndRiskLevel(
                contract.getId(), RiskLevel.HIGH);

        assertThat(highClauses).hasSize(1);
        assertThat(highClauses.get(0).getTitle()).isEqualTo("High Risk");
    }

    @Test
    void shouldFetchContractAssociationInSingleQuery() {
        var clause = new ContractClause();
        clause.setNumber(1);
        clause.setTitle("N+1 Test");
        clause.setContent("Test content");
        clause.setRiskLevel(RiskLevel.LOW);
        clause.setContract(contract);
        contractClauseRepository.saveAndFlush(clause);
        entityManager.clear();

        var stats = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        var clauses = contractClauseRepository.findByContractIdOrderByNumber(contract.getId());

        assertThat(clauses).hasSize(1);
        assertThat(clauses.get(0).getContract().getTitle()).isEqualTo("Test Agreement");
        assertThat(stats.getQueryExecutionCount()).isEqualTo(1);
    }
}
