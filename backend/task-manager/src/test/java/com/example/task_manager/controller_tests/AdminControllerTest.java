package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.controller.AdminController;
import com.example.task_manager.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private AdminDTO mockAdmin;
    private TeamMemberDTO mockMember;

    @BeforeEach
    void setUp() {
        mockAdmin = new AdminDTO(1, "Admin User", "admin@example.com");
        mockMember = new TeamMemberDTO(2, "John Doe", "john.doe@example.com");
    }

    // Create an Admin
    @Test
    void testCreateAdmin() throws Exception {
        when(adminService.createAdmin("Admin User", "admin@example.com")).thenReturn(mockAdmin);

        mockMvc.perform(post("/api/admin")
                .param("name", "Admin User")
                .param("email", "admin@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value("Admin User"));
    }

    // Delete Admin
    @Test
    void testDeleteAdmin() throws Exception {
        doNothing().when(adminService).deleteAdmin(1);

        mockMvc.perform(delete("/api/admin/1"))
                .andExpect(status().isNoContent());
    }

    // Modify Admin Name
    @Test
    void testModifyAdminName() throws Exception {
        when(adminService.modifyAdminName(1, "New Admin Name")).thenReturn(new AdminDTO(1, "New Admin Name", "admin@example.com"));

        mockMvc.perform(put("/api/admin/1/update-name")
                .param("newName", "New Admin Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("New Admin Name"));
    }

    // Modify Admin Email
    @Test
    void testModifyAdminEmail() throws Exception {
        when(adminService.modifyAdminEmail(1, "newadmin@example.com"))
                .thenReturn(new AdminDTO(1, "Admin User", "newadmin@example.com"));

        mockMvc.perform(put("/api/admin/1/update-email")
                .param("newEmail", "newadmin@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("newadmin@example.com"));
    }

    // Create Team Member
    @Test
    void testCreateTeamMember() throws Exception {
        when(adminService.createTeamMember("John Doe", "john.doe@example.com")).thenReturn(mockMember);

        mockMvc.perform(post("/api/admin/team-member")
                .param("name", "John Doe")
                .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.userName").value("John Doe"));
    }

    // Modify Team Member Name 
    @Test
    void testModifyTeamMemberName() throws Exception {
        when(adminService.modifyTeamMemberName(1, "Jane Doe"))
                .thenReturn(new TeamMemberDTO(1, "Jane Doe", "john.doe@example.com"));

        mockMvc.perform(put("/api/admin/team-member/1/update-name")
                .param("newName", "Jane Doe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("Jane Doe"));
    }

    // Modify Team Member Email 
    @Test
    void testModifyTeamMemberEmail() throws Exception {
        when(adminService.modifyTeamMemberEmail(1, "jane.doe@example.com"))
                .thenReturn(new TeamMemberDTO(1, "John Doe", "jane.doe@example.com"));

        mockMvc.perform(put("/api/admin/team-member/1/update-email")
                .param("newEmail", "jane.doe@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("jane.doe@example.com"));
    }

    // Modify Team Member Email (Invalid Format) 
    @Test
    void testModifyTeamMemberEmailInvalidFormat() throws Exception {
        when(adminService.modifyTeamMemberEmail(1, "invalid-email"))
                .thenThrow(new RuntimeException("Invalid email format"));

        mockMvc.perform(put("/api/admin/team-member/1/update-email")
                .param("newEmail", "invalid-email")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email format"));
    }

    // Delete Team Member 
    @Test
    void testDeleteTeamMember() throws Exception {
        doNothing().when(adminService).deleteTeamMember(1);

        mockMvc.perform(delete("/api/admin/team-member/1"))
                .andExpect(status().isNoContent());
    }

    // Delete Team Member (Not Found) 
    @Test
    void testDeleteTeamMember_NotFound() throws Exception {
        doThrow(new RuntimeException("Team member not found")).when(adminService).deleteTeamMember(999);

        mockMvc.perform(delete("/api/admin/team-member/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Team member not found"));
    }

    // Promote to Admin
    @Test
    void testPromoteToAdmin() throws Exception {
        when(adminService.promoteToAdmin(2)).thenReturn(new AdminDTO(2, "John Doe", "john.doe@example.com"));

        mockMvc.perform(post("/api/admin/team-member/2/promote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.userName").value("John Doe"));
    }

    // Assign to Team
    @Test
    void testAssignToTeam() throws Exception {
        TeamMemberDTO mockTeamMember = new TeamMemberDTO(2, "John Doe", "john.doe@example.com"); // Mock return value
        
        when(adminService.assignToTeam(2, 1)).thenReturn(mockTeamMember);

        mockMvc.perform(post("/api/admin/team-member/2/assign-to-team/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.userName").value("John Doe"))
                .andExpect(jsonPath("$.userEmail").value("john.doe@example.com"));
    }

    // Lock Task
    @Test
    void testLockTask() throws Exception {
        doNothing().when(adminService).lockTask(1);

        mockMvc.perform(put("/api/admin/tasks/1/lock"))
                .andExpect(status().isOk());
    }

    // Unlock Task
    @Test
    void testUnlockTask() throws Exception {
        doNothing().when(adminService).unlockTask(1);

        mockMvc.perform(put("/api/admin/tasks/1/unlock"))
                .andExpect(status().isOk());
    }
}
