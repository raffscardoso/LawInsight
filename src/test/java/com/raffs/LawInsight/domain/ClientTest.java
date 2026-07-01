package com.raffs.LawInsight.domain;

import com.raffs.LawInsight.domain.enumeration.ClientType;
import com.raffs.LawInsight.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ClientTest {

    @Autowired
    private ClientRepository repository;

    private Client createClient(String documentNumber, String email) {
        var client = new Client();
        client.setName("Acme Corp");
        client.setClientType(ClientType.COMPANY);
        client.setEmail(email);
        client.setPhone("+55 11 99999-0001");
        client.setDocumentNumber(documentNumber);
        client.setAddress("Av. Paulista, 1000, Sao Paulo, SP");
        return client;
    }

    @Test
    void shouldPersistClientWithAllFields() {
        var client = createClient("12.345.678/0001-90", "contato@acme.com");
        var saved = repository.save(client);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Acme Corp");
        assertThat(saved.getClientType()).isEqualTo(ClientType.COMPANY);
        assertThat(saved.getEmail()).isEqualTo("contato@acme.com");
        assertThat(saved.getPhone()).isEqualTo("+55 11 99999-0001");
        assertThat(saved.getDocumentNumber()).isEqualTo("12.345.678/0001-90");
        assertThat(saved.getAddress()).isEqualTo("Av. Paulista, 1000, Sao Paulo, SP");
    }

    @Test
    void shouldPersistClientWithoutOptionalFields() {
        var client = new Client();
        client.setName("Individual Client");
        client.setClientType(ClientType.PERSON);
        client.setDocumentNumber("123.456.789-00");
        client.setEmail("person@email.com");

        var saved = repository.save(client);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNotes()).isNull();
    }

    @Test
    void shouldRejectDuplicateDocumentNumber() {
        var client1 = createClient("00.000.000/0001-91", "dup@test.com");
        repository.saveAndFlush(client1);

        var client2 = createClient("00.000.000/0001-91", "other@test.com");
        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(client2));
    }

    @Test
    void shouldStoreClientTypeAsString() {
        var client = createClient("11.111.111/0001-92", "type-test@test.com");
        client.setClientType(ClientType.PERSON);
        var saved = repository.save(client);

        assertThat(saved.getClientType()).isEqualTo(ClientType.PERSON);
        assertThat(saved.getClientType().name()).isEqualTo("PERSON");
    }
}
