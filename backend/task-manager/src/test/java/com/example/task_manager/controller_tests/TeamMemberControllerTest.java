package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberWithTeamLeadDTO;
import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.service.TeamMemberService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.task_manager.enums.RoleType;
import com.example.task_manager.enums.TaskPriority;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.example.task_manager.controller.TeamMemberController;
import com.example.task_manager.service.AdminService;

@WebMvcTest(TeamMemberController.class)
@ActiveProfiles("test")
public class TeamMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamMemberService teamMemberService;

    @MockBean
    private AdminService adminService;

    /**
     * Test Assign Member to Task
     */
    @Test
    void testAssignToTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int taskId = uniqueId + 1;
        int teamMemberId = uniqueId + 2;

        IsAssignedDTO assignedDTO = new IsAssignedDTO(uniqueId, taskId, teamMemberId, uniqueId);
        when(teamMemberService.assignToTask(taskId, teamMemberId)).thenReturn(assignedDTO);

        mockMvc.perform(post("/api/tasks/" + taskId + "/assign/" + teamMemberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(taskId));
    }

    /*
     * Test mass assign members to task
     */
    @Test
    void testMassAssignToTask() throws Exception {
        int uniqueId = 1;
        int taskId = 2;

        List<Integer> teamMemberIds = List.of(4, 5, 6);

        List<IsAssignedDTO> mockAssignments = List.of(
            new IsAssignedDTO(uniqueId, taskId, 4, uniqueId),
            new IsAssignedDTO(uniqueId, taskId, 5, uniqueId),
            new IsAssignedDTO(uniqueId, taskId, 6, uniqueId)
        );
        when(teamMemberService.massAssignToTask(taskId, teamMemberIds)).thenReturn(mockAssignments);

        mockMvc.perform(post("/api/tasks/2/mass-assign"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(taskId));
    }

    /**
     * Placeholder: Change Password
     */
    @Test
    void testChangePassword() throws Exception {
            // TODO: Implement Change Password Test
    }
    
    

     @Test
    void testGetTeamsForMember() throws Exception {
        List<TeamDTO> mockTeams = Arrays.asList(
                new TeamDTO(1, "Team 1", 1),
                new TeamDTO(2, "Team 2", 1));

        when(teamMemberService.getTeamsForMember(1)).thenReturn(mockTeams);

        MvcResult result = mockMvc.perform(get("/api/tasks/1/teams"))
                .andExpect(status().isOk())
                .andReturn();

        // System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    void testGetAssignedTasks() throws Exception {
        List<TaskDTO> mockTasks = Arrays.asList(
                new TaskDTO(1, "Task Title 1", "Task 1 description", false, "Open", LocalDate.now(), null, 1, null,
                        TaskPriority.MEDIUM),
                new TaskDTO(2, "Task Title 2", "Task 2 description", true, "Closed", LocalDate.now(), null, 1, null,
                        TaskPriority.MEDIUM));

        when(teamMemberService.getAssignedTasks(1)).thenReturn(mockTasks);

        MvcResult result = mockMvc.perform(get("/api/tasks/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("MEDIUM"))
                .andExpect(jsonPath("$[1].priority").value("MEDIUM"))
                .andReturn();

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
}
