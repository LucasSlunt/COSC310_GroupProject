package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.IsMemberOfDTO;
import com.example.task_manager.controller.IsMemberOfController;
import com.example.task_manager.service.IsMemberOfService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IsMemberOfController.class)
@ActiveProfiles("test")
public class IsMemberOfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IsMemberOfService isMemberOfService;

    /**
     * Test Add Member to a Team
     */
    @Test
    void testAddMemberToTeam() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int teamMemberId = uniqueId + 1;
        int teamId = uniqueId + 2;
        IsMemberOfDTO mockMembership = new IsMemberOfDTO(uniqueId, teamMemberId, teamId);

        when(isMemberOfService.addMemberToTeam(teamMemberId, teamId)).thenReturn(mockMembership);

        mockMvc.perform(post("/api/memberships/" + teamMemberId + "/team/" + teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isMemberOfId").value(uniqueId))
                .andExpect(jsonPath("$.teamMemberId").value(teamMemberId))
                .andExpect(jsonPath("$.teamId").value(teamId));
    }

    /**
     * Test Remove Member from Team
     */
    @Test
    void testRemoveMemberFromTeam() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int teamMemberId = uniqueId + 1;
        int teamId = uniqueId + 2;
        IsMemberOfDTO mockMembership = new IsMemberOfDTO(uniqueId, teamMemberId, teamId);

        when(isMemberOfService.removeMemberFromTeam(teamMemberId, teamId)).thenReturn(mockMembership);

        mockMvc.perform(delete("/api/memberships/" + teamMemberId + "/team/" + teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isMemberOfId").value(uniqueId));
    }

    /**
     * Test Check if Member of Team
     */
    @Test
    void testIsMemberOfTeam() throws Exception {
        int teamMemberId = (int) System.nanoTime();
        int teamId = teamMemberId + 1;

        when(isMemberOfService.isMemberOfTeam(teamMemberId, teamId)).thenReturn(true);

        mockMvc.perform(get("/api/memberships/" + teamMemberId + "/team/" + teamId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
