package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.AdminRequestDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.enums.RoleType;
import com.example.task_manager.service.AdminService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.task_manager.DTO.UpdateEmailRequestDTO;
import com.example.task_manager.DTO.UpdateNameRequestDTO;
import com.example.task_manager.controller.TeamMemberAccountController;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TeamMemberAccountController.class)
@ActiveProfiles("test")
public class TeamMemberAccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create Team Member
     */
    @Test
    void testCreateTeamMember() throws Exception {
        TeamMemberDTO mockMember = new TeamMemberDTO(2, "Member_" + System.nanoTime(), "member_" + System.nanoTime() + "@example.com", RoleType.TEAM_MEMBER);
        AdminRequestDTO requestDTO = new AdminRequestDTO(mockMember.getUserName(), mockMember.getUserEmail(), "securePass");

        when(adminService.createTeamMember(mockMember.getUserName(), mockMember.getUserEmail(), "securePass")).thenReturn(mockMember);

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.userName").value(mockMember.getUserName()))
                .andExpect(jsonPath("$.userEmail").value(mockMember.getUserEmail()));
    }

    /**
     * Modify Team Member Name
     */
    @Test
    void testModifyTeamMemberName() throws Exception {
        TeamMemberDTO updatedMember = new TeamMemberDTO(1, "UpdatedMember_" + System.nanoTime(), "member@example.com", RoleType.TEAM_MEMBER);
        UpdateNameRequestDTO requestDTO = new UpdateNameRequestDTO(updatedMember.getUserName());

        when(adminService.modifyTeamMemberName(1, updatedMember.getUserName())).thenReturn(updatedMember);

        mockMvc.perform(put("/api/members/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(updatedMember.getUserName()));
    }

    /**
     * Modify Team Member Email
     */
    @Test
    void testModifyTeamMemberEmail() throws Exception {
        TeamMemberDTO updatedMember = new TeamMemberDTO(1, "John Doe", "updated_" + System.nanoTime() + "@example.com",
                RoleType.TEAM_MEMBER);
        UpdateEmailRequestDTO requestDTO = new UpdateEmailRequestDTO(updatedMember.getUserEmail());

        when(adminService.modifyTeamMemberEmail(1, updatedMember.getUserEmail())).thenReturn(updatedMember);

        mockMvc.perform(put("/api/members/1/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value(updatedMember.getUserEmail()));
    }

}
