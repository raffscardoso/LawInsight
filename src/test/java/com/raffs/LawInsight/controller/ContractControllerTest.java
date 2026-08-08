package com.raffs.LawInsight.controller;

import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import com.raffs.LawInsight.dto.ContractRequest;
import com.raffs.LawInsight.dto.ContractResponse;
import com.raffs.LawInsight.dto.ErrorResponse;
import com.raffs.LawInsight.exception.ResourceNotFoundException;
import com.raffs.LawInsight.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.raffs.LawInsight.service.ContractProcessingService;
import static org.mockito.Mockito.verify;

import com.raffs.LawInsight.service.SseService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import static org.mockito.Mockito.verify;

class ContractControllerTest {

    private final ContractService contractService = mock(ContractService.class);
    private final ContractProcessingService contractProcessingService = mock(ContractProcessingService.class);
    private final SseService sseService = mock(SseService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ContractController(contractService, contractProcessingService, sseService))
                .setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateContract() throws Exception {
        var request = new ContractRequest();
        request.setTitle("Test");
        request.setOriginalFileName("doc.pdf");
        request.setFileType(FileType.PDF);
        request.setExtractedContent("content");
        request.setFileHash("a".repeat(64));
        request.setUploadedById(1L);
        request.setClientId(1L);

        when(contractService.createContract(any())).thenReturn(new ContractResponse());

        var json = """
                {
                    "title": "Test",
                    "originalFileName": "doc.pdf",
                    "fileType": "PDF",
                    "extractedContent": "content",
                    "fileHash": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    "uploadedById": 1,
                    "clientId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenRequestInvalid() throws Exception {
        var json = "{}";

        mockMvc.perform(post("/api/v1/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindById() throws Exception {
        when(contractService.findById(1L)).thenReturn(new ContractResponse());

        mockMvc.perform(get("/api/v1/contracts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(contractService.findById(99L)).thenThrow(new ResourceNotFoundException("Contract not found with id: 99"));

        mockMvc.perform(get("/api/v1/contracts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldFindAll() throws Exception {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(contractService.findAll(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(), pageable, 0));

        mockMvc.perform(get("/api/v1/contracts"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindByStatus() throws Exception {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(contractService.findByStatus(eq(ContractStatus.UPLOADED), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(), pageable, 0));

        mockMvc.perform(get("/api/v1/contracts").param("status", "UPLOADED"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSearchContracts() throws Exception {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        when(contractService.searchContracts(eq("Service"), eq(ContractStatus.UPLOADED), eq(FileType.PDF), eq(1L), eq(1L), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(), pageable, 0));

        mockMvc.perform(get("/api/v1/contracts/search")
                        .param("title", "Service")
                        .param("status", "UPLOADED")
                        .param("fileType", "PDF")
                        .param("clientId", "1")
                        .param("uploadedById", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUploadContract() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("attorney@law.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(contractService.uploadContract(any(), eq("attorney@law.com"), any(), any())).thenReturn(new ContractResponse());

        var file = new MockMultipartFile("file", "contract.pdf", "application/pdf", "dummy".getBytes());

        mockMvc.perform(multipart("/api/v1/contracts/upload")
                        .file(file)
                        .param("clientId", "1")
                        .param("title", "Uploaded Contract"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDeleteContract() throws Exception {
        mockMvc.perform(delete("/api/v1/contracts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Contract not found with id: 99"))
                .when(contractService).deleteContract(99L);

        mockMvc.perform(delete("/api/v1/contracts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateStatus() throws Exception {
        when(contractService.updateStatus(eq(1L), eq(ContractStatus.PROCESSED)))
                .thenReturn(new ContractResponse());

        mockMvc.perform(patch("/api/v1/contracts/1/status")
                        .param("status", "PROCESSED"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldProcessContractAsync() throws Exception {
        mockMvc.perform(post("/api/v1/contracts/1/process"))
                .andExpect(status().isAccepted());

        verify(contractProcessingService).processContractAsync(1L);
    }

    @Test
    void shouldStreamStatus() throws Exception {
        mockMvc.perform(get("/api/v1/contracts/1/status/stream"))
                .andExpect(status().isOk());
                
        verify(sseService).register(eq(1L), any(SseEmitter.class));
    }
}
