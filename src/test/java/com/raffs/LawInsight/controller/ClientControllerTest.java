package com.raffs.LawInsight.controller;

import com.raffs.LawInsight.dto.ClientResponse;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClientControllerTest {

    private final ClientService clientService = mock(ClientService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ClientController(clientService))
                .setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreate() throws Exception {
        when(clientService.create(any())).thenReturn(new ClientResponse());

        var json = """
                {
                    "name": "New Client",
                    "clientType": "PERSON",
                    "documentNumber": "123.456.789-00"
                }
                """;

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindById() throws Exception {
        when(clientService.findById(1L)).thenReturn(new ClientResponse());

        mockMvc.perform(get("/api/v1/clients/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(clientService.findById(99L)).thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(get("/api/v1/clients/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindAll() throws Exception {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(clientService.findAll(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(), pageable, 0));

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdate() throws Exception {
        when(clientService.update(eq(1L), any())).thenReturn(new ClientResponse());

        var json = """
                {
                    "name": "Updated",
                    "clientType": "COMPANY",
                    "documentNumber": "00.000.000/0001-00"
                }
                """;

        mockMvc.perform(put("/api/v1/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/clients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("not found"))
                .when(clientService).delete(99L);

        mockMvc.perform(delete("/api/v1/clients/99"))
                .andExpect(status().isNotFound());
    }
}
