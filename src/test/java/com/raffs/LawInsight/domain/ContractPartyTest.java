package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.repository.ClientRepository;
import com.raffs.LawInsight.repository.ContractPartyRepository;
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
class ContractPartyTest {

    @Autowired
    private ContractPartyRepository contractPartyRepository;

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
        contractPartyRepository.deleteAll();
        contractRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();

        var attorney = userRepository.save(TestDataFactory.createUser("party-test"));
        var client = clientRepository.save(
                TestDataFactory.createClient("party-test", "22.222.222/0001-22"));
        contract = contractRepository.save(
                TestDataFactory.createContract(
                        "1111111111111111111111111111111111111111111111111111111111111111",
                        attorney, client));
    }

    @Test
    void shouldPersistContractPartyWithAllFields() {
        var party = new ContractParty();
        party.setName("Acme Corp");
        party.setRoleInContract("Service Provider");
        party.setDocumentNumber("12.345.678/0001-90");
        party.setEmail("contact@acmecorp.com");
        party.setIsSignatory(true);
        party.setContract(contract);

        var saved = contractPartyRepository.save(party);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Acme Corp");
        assertThat(saved.getRoleInContract()).isEqualTo("Service Provider");
        assertThat(saved.getDocumentNumber()).isEqualTo("12.345.678/0001-90");
        assertThat(saved.getEmail()).isEqualTo("contact@acmecorp.com");
        assertThat(saved.getIsSignatory()).isTrue();
    }

    @Test
    void shouldAssociatePartyWithContract() {
        var party = new ContractParty();
        party.setName("Test Party");
        party.setRoleInContract("Provider");
        party.setContract(contract);

        var saved = contractPartyRepository.save(party);

        assertThat(saved.getContract()).isNotNull();
        assertThat(saved.getContract().getId()).isEqualTo(contract.getId());
    }

    @Test
    void shouldFindPartiesByContractId() {
        var party1 = new ContractParty();
        party1.setName("Party A");
        party1.setRoleInContract("Provider");
        party1.setContract(contract);
        contractPartyRepository.save(party1);

        var party2 = new ContractParty();
        party2.setName("Party B");
        party2.setRoleInContract("Client");
        party2.setContract(contract);
        contractPartyRepository.save(party2);

        var parties = contractPartyRepository.findByContractId(contract.getId());

        assertThat(parties).hasSize(2);
        assertThat(parties).extracting(ContractParty::getName)
                .containsExactlyInAnyOrder("Party A", "Party B");
    }

    @Test
    void shouldFindSignatoryParties() {
        var signatory = new ContractParty();
        signatory.setName("Signatory Inc");
        signatory.setRoleInContract("Provider");
        signatory.setIsSignatory(true);
        signatory.setContract(contract);
        contractPartyRepository.save(signatory);

        var nonSignatory = new ContractParty();
        nonSignatory.setName("Observer Ltd");
        nonSignatory.setRoleInContract("Observer");
        nonSignatory.setIsSignatory(false);
        nonSignatory.setContract(contract);
        contractPartyRepository.save(nonSignatory);

        var signatories = contractPartyRepository.findByContractIdAndIsSignatoryTrue(
                contract.getId());

        assertThat(signatories).hasSize(1);
        assertThat(signatories.get(0).getName()).isEqualTo("Signatory Inc");
    }

    @Test
    void shouldFetchContractAssociationInSingleQuery() {
        var party = new ContractParty();
        party.setName("N+1 Corp");
        party.setRoleInContract("Provider");
        party.setContract(contract);
        contractPartyRepository.saveAndFlush(party);
        entityManager.clear();

        var stats = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        var parties = contractPartyRepository.findByContractId(contract.getId());

        assertThat(parties).hasSize(1);
        assertThat(parties.get(0).getContract().getTitle()).isEqualTo("Test Agreement");
        assertThat(stats.getQueryExecutionCount()).isEqualTo(1);
    }
}
