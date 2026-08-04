package com.raffs.LawInsight.security;

import com.raffs.LawInsight.controller.ClientController;
import com.raffs.LawInsight.controller.ContractController;
import com.raffs.LawInsight.controller.UserController;
import com.raffs.LawInsight.service.ClientService;
import com.raffs.LawInsight.service.ContractProcessingService;
import com.raffs.LawInsight.service.ContractService;
import com.raffs.LawInsight.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class RoleAuthorizationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ClientService clientService;

    @MockBean
    private ContractService contractService;

    @MockBean
    private ContractProcessingService contractProcessingService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ASSISTANT")
    void shouldDenyAssistantFromAccessingUserManagement() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminToAccessUserManagement() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PARALEGAL")
    void shouldDenyParalegalFromDeletingClient() throws Exception {
        mockMvc.perform(delete("/api/v1/clients/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ATTORNEY")
    void shouldAllowAttorneyToAccessClients() throws Exception {
        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk());
    }
}
