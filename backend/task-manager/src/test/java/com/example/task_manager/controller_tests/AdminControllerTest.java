package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.AdminRequestDTO;
import com.example.task_manager.DTO.ChangeRoleRequestDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.controller.AdminController;
import com.example.task_manager.enums.RoleType;
import com.example.task_manager.repository.AdminRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.service.AdminService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.task_manager.DTO.TeamMemberWithTeamLeadDTO;
import com.example.task_manager.DTO.UpdateEmailRequestDTO;
import com.example.task_manager.DTO.UpdateNameRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private AdminRepository adminRepository;

    @MockBean
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create an Admin
     */
    @Test
    void testCreateAdmin() throws Exception {
        AdminDTO mockAdmin = new AdminDTO(1, "Admin_" + System.nanoTime(), "admin_" + System.nanoTime() + "@example.com", RoleType.ADMIN);
        AdminRequestDTO requestDTO = new AdminRequestDTO(mockAdmin.getUserName(), mockAdmin.getUserEmail(), "securePass");

        when(adminService.createAdmin(mockAdmin.getUserName(), mockAdmin.getUserEmail(), "securePass")).thenReturn(mockAdmin);

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value(mockAdmin.getUserName()))
                .andExpect(jsonPath("$.userEmail").value(mockAdmin.getUserEmail()));
    }

    /**
     * Delete Admin
     */
    @Test
    void testDeleteAdmin() throws Exception {
        doNothing().when(adminService).deleteAdmin(1);

        mockMvc.perform(delete("/api/admin/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Modify Admin Name
     */
    @Test
    void testModifyAdminName() throws Exception {
        AdminDTO updatedAdmin = new AdminDTO(1, "NewAdmin_" + System.nanoTime(), "admin@example.com", RoleType.ADMIN);
        UpdateNameRequestDTO requestDTO = new UpdateNameRequestDTO(updatedAdmin.getUserName());

        when(adminService.modifyAdminName(1, updatedAdmin.getUserName())).thenReturn(updatedAdmin);

        mockMvc.perform(put("/api/admin/1/update-name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(updatedAdmin.getUserName()));
    }

    /**
     * Modify Admin Email
     */
    @Test
    void testModifyAdminEmail() throws Exception {
        AdminDTO updatedAdmin = new AdminDTO(1, "Admin User", "updated_" + System.nanoTime() + "@example.com", RoleType.ADMIN);
        UpdateEmailRequestDTO requestDTO = new UpdateEmailRequestDTO(updatedAdmin.getUserEmail());

        when(adminService.modifyAdminEmail(1, updatedAdmin.getUserEmail())).thenReturn(updatedAdmin);

        mockMvc.perform(put("/api/admin/1/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value(updatedAdmin.getUserEmail()));
    }

    /**
     * Create Team Member
     */
    @Test
    void testCreateTeamMember() throws Exception {
        TeamMemberDTO mockMember = new TeamMemberDTO(2, "Member_" + System.nanoTime(), "member_" + System.nanoTime() + "@example.com", RoleType.TEAM_MEMBER);
        AdminRequestDTO requestDTO = new AdminRequestDTO(mockMember.getUserName(), mockMember.getUserEmail(), "securePass");

        when(adminService.createTeamMember(mockMember.getUserName(), mockMember.getUserEmail(), "securePass")).thenReturn(mockMember);

        mockMvc.perform(post("/api/admin/team-member")
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

        mockMvc.perform(put("/api/admin/team-member/1/update-name")
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

        mockMvc.perform(put("/api/admin/team-member/1/update-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value(updatedMember.getUserEmail()));
    }

    //changes role to team member
    @Test
    void testChangeRoleToTeamMember() throws Exception {
        TeamMemberDTO updatedTeamMember = new TeamMemberDTO(1, "Stranglehold", "TedNugent@rock.com",
                RoleType.TEAM_MEMBER);

        when(adminService.changeRole(1, RoleType.ADMIN)).thenReturn(updatedTeamMember);

        String request = objectMapper.writeValueAsString(new ChangeRoleRequestDTO(RoleType.ADMIN));

        mockMvc.perform(post("/api/admin/team-member/1/change-role")
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
                "/api/admin/team-member/1/change-role")
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

        mockMvc.perform(post("/api/admin/team-member/2/assign-to-team/1"))
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

        mockMvc.perform(put("/api/admin/tasks/1/lock"))
                .andExpect(status().isOk());
    }

    /**
     * Unlock Task
     */
    @Test
    void testUnlockTask() throws Exception {
        doNothing().when(adminService).unlockTask(1);

        mockMvc.perform(put("/api/admin/tasks/1/unlock"))
                .andExpect(status().isOk());
    }

    // Getting all admins
    @Test
    void testGetAllAdmins() throws Exception {
        List<AdminDTO> mockAdmins = Arrays.asList(
                new AdminDTO(1, "Alice Johnson", "alice@example.com", RoleType.ADMIN),
                new AdminDTO(2, "Bob Smith", "bob@example.com", RoleType.ADMIN));

        when(adminService.getAllAdmins()).thenReturn(mockAdmins);

        mockMvc.perform(get("/api/admin/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[0].userName").value("Alice Johnson"))
                .andExpect(jsonPath("$[1].userEmail").value("bob@example.com"));
    }

    // Getting all admins
    @Test
    void getAllTeamMembers() throws Exception {
        List<TeamMemberWithTeamLeadDTO> mockTMs = Arrays.asList(
                new TeamMemberWithTeamLeadDTO(1, "Alice Johnson", "alice@example.com", RoleType.TEAM_MEMBER, false, null, null),
                new TeamMemberWithTeamLeadDTO(2, "Bob Smith", "bob@example.com", RoleType.TEAM_MEMBER, false, null, null));

        when(adminService.getAllTeamMembers()).thenReturn(mockTMs);

        mockMvc.perform(get("/api/admin/team-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[0].userName").value("Alice Johnson"))
                .andExpect(jsonPath("$[1].userEmail").value("bob@example.com"));
    }

    // Getting all teams
    @Test
    void getAllTeams() throws Exception {
        List<TeamDTO> mockTeams = Arrays.asList(
                new TeamDTO(1, "Team 1", 1),
                new TeamDTO(2, "Team 2", 2));

        when(adminService.getAllTeams()).thenReturn(mockTeams);

        mockMvc.perform(get("/api/admin/all-teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].teamId").value(1))
                .andExpect(jsonPath("$[0].teamName").value("Team 1"))
                .andExpect(jsonPath("$[1].teamLeadId").value("2"));
    }

    @Test
    void testGetAdminById() throws Exception {
        AdminDTO mockAdmin = new AdminDTO(1, "Admin Sandler", "adam_sandler@example.com", RoleType.ADMIN);

        when(adminService.getAdminById(1)).thenReturn(mockAdmin);

        mockMvc.perform(get("/api/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value("Admin Sandler"))
                .andExpect(jsonPath("$.userEmail").value("adam_sandler@example.com"));
    }

    @Test
    void testGetAdminById_NotFound() throws Exception {
        when(adminService.getAdminById(999)).thenThrow(new RuntimeException("Admin not found"));

        mockMvc.perform(get("/api/admin/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Admin not found"));
    }
}
