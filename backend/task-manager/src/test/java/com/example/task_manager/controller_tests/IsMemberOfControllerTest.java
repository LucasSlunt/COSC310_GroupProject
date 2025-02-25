package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.example.task_manager.DTO.IsMemberOfDTO;
import com.example.task_manager.controller.IsMemberOfController;
import com.example.task_manager.service.IsMemberOfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IsMemberOfController.class)
public class IsMemberOfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IsMemberOfService isMemberOfService;

    @InjectMocks
    private IsMemberOfController isMemberOfController;

    private IsMemberOfDTO mockMembership;

    @BeforeEach
    void setUp() {
        mockMembership = new IsMemberOfDTO(1, 202, 303);
    }

    // Add Member to a Team
    @Test
    void testAddMemberToTeam() throws Exception {
        when(isMemberOfService.addMemberToTeam(202, 303)).thenReturn(mockMembership);

        mockMvc.perform(post("/api/memberships/202/team/303"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isMemberOfId").value(1))
                .andExpect(jsonPath("$.teamMemberId").value(202))
                .andExpect(jsonPath("$.teamId").value(303));
    }

    // Remove Member from Team
    @Test
    void testRemoveMemberFromTeam() throws Exception {
        when(isMemberOfService.removeMemberFromTeam(202, 303)).thenReturn(mockMembership);

        mockMvc.perform(delete("/api/memberships/202/team/303"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isMemberOfId").value(1));
    }

    // Check if Member of Team
    @Test
    void testIsMemberOfTeam() throws Exception {
        when(isMemberOfService.isMemberOfTeam(202, 303)).thenReturn(true);

        mockMvc.perform(get("/api/memberships/202/team/303"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
