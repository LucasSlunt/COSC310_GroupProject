package com.example.task_manager.controller_tests;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.ChangeRoleRequestDTO;
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

import com.example.task_manager.controller.AdminController;
import com.example.task_manager.service.TeamMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminController.class)
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private TeamMemberService teamMemberService;

    @Autowired
    private ObjectMapper objectMapper;
    
    //changes role to team member
    @Test
    void testChangeRoleToTeamMember() throws Exception {
        TeamMemberDTO updatedTeamMember = new TeamMemberDTO(1, "Stranglehold", "TedNugent@rock.com",
                RoleType.TEAM_MEMBER);

        when(adminService.changeRole(1, RoleType.ADMIN)).thenReturn(updatedTeamMember);

        String request = objectMapper.writeValueAsString(new ChangeRoleRequestDTO(RoleType.ADMIN));

        mockMvc.perform(post("/api/admins/actions/1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value("Stranglehold"))
                .andExpect(jsonPath("$.role").value("TEAM_MEMBER"));
    }
    
    //changes role to admin
    @Test
    void testChangeRoleToAdmin() throws Exception {
        AdminDTO admin = new AdminDTO(1, "HighwayTune", "GretaVanFleet@rock.com", RoleType.ADMIN);

        when(adminService.changeRole(1, RoleType.TEAM_MEMBER)).thenReturn(admin);

        String request = objectMapper.writeValueAsString(new ChangeRoleRequestDTO(RoleType.TEAM_MEMBER));

        mockMvc.perform(post(
                "/api/admins/actions/1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value("HighwayTune"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    /**
     * Assign Team Member to Team
     */
    @Test
    void testAssignToTeam() throws Exception {
        TeamMemberDTO updatedMember = new TeamMemberDTO(2, "John Doe", "john.doe@example.com", RoleType.TEAM_MEMBER);

        when(adminService.assignToTeam(2, 1)).thenReturn(updatedMember);

        mockMvc.perform(post("/api/admins/actions/team-member/2/assign-to-team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.userName").value("John Doe"))
                .andExpect(jsonPath("$.userEmail").value("john.doe@example.com"));
    }

    /**
     * Lock Task
     */
    @Test
    void testLockTask() throws Exception {
        doNothing().when(adminService).lockTask(1);

        mockMvc.perform(put("/api/admins/actions/tasks/1/lock"))
                .andExpect(status().isOk());
    }

    /**
     * Unlock Task
     */
    @Test
    void testUnlockTask() throws Exception {
        doNothing().when(adminService).unlockTask(1);

        mockMvc.perform(put("/api/admins/actions/tasks/1/unlock"))
                .andExpect(status().isOk());
    }

    // Getting all admins
    @Test
    void testGetAllAdmins() throws Exception {
        List<AdminDTO> mockAdmins = Arrays.asList(
                new AdminDTO(1, "Alice Johnson", "alice@example.com", RoleType.ADMIN),
                new AdminDTO(2, "Bob Smith", "bob@example.com", RoleType.ADMIN));

        when(adminService.getAllAdmins()).thenReturn(mockAdmins);

        mockMvc.perform(get("/api/admins/actions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[0].userName").value("Alice Johnson"))
                .andExpect(jsonPath("$[1].userEmail").value("bob@example.com"));
    }

    @Test
    void testResetPassword() throws Exception {
            int teamMemberId = 1;
            String newPassword = "trustmethisissecure";
        
        String request = objectMapper.writeValueAsString(new Object() {
                public final String newPassword = "BrainStew_GreenDay";
        });

        doNothing().when(teamMemberService).resetPassword(eq(teamMemberId), eq("BrainStew_GreenDay"));

        mockMvc.perform(post("/api/admins/actions/{teamMemberId}/reset-password", teamMemberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isNoContent());
    }
}
