package com.example.task_manager.controller_tests;

import java.awt.PageAttributes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.controller.TeamMemberController;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.service.TeamMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.example.task_manager.enums.TaskPriority;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(TeamMemberController.class)
@ActiveProfiles("test")
public class TeamMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamMemberService teamMemberService;

    @InjectMocks
    private TeamMemberController teamMemberController;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test Create Task
     */
    @Test
    void testCreateTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int teamId = uniqueId + 1;
        TaskDTO mockTask = new TaskDTO(uniqueId, "Task Title " + uniqueId, "Description", false, "Open", LocalDate.now(), null, teamId, null,  TaskPriority.LOW);

        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Task Title " + uniqueId,
                "Description",
                false,
                "Open",
                LocalDate.of(2025, 3, 11),
                Arrays.asList(1, 2, 3),
                teamId,
                TaskPriority.LOW
        );

        when(teamMemberService.createTask(any(TaskRequestDTO.class))).thenReturn(mockTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task Title " + uniqueId))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.isLocked").value(false))
                .andExpect(jsonPath("$.status").value("Open"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.priority").value("LOW"));
    }

    /**
     * Test Delete Task
     */
    @Test
    void testDeleteTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        doNothing().when(teamMemberService).deleteTask(uniqueId);

        mockMvc.perform(delete("/api/tasks/" + uniqueId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test Edit Task
     */
    @Test
    void testEditTask() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int teamId = uniqueId + 1;

        TaskDTO requestDTO = new TaskDTO(
                uniqueId,
                "Updated Title " + uniqueId,
                "Updated Description",
                false,
                "In Progress",
                LocalDate.now().plusDays(3),
                null,
                teamId,
                null,
                TaskPriority.HIGH
        );

        when(teamMemberService.editTask(eq(uniqueId), any(TaskDTO.class))).thenReturn(requestDTO);

        mockMvc.perform(put("/api/tasks/" + uniqueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title " + uniqueId))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.isLocked").value(false))
                .andExpect(jsonPath("$.status").value("In Progress"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

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
    void testResetPassword() throws Exception {
            int teamMemberId = 1;
            String newPassword = "trustmethisissecure";
        
        String request = objectMapper.writeValueAsString(new Object() {
                public final String newPassword = "BrainStew_GreenDay";
        });

        doNothing().when(teamMemberService).resetPassword(teamMemberId, newPassword);

        mockMvc.perform(post("/api/tasks/team-members/{teamMemberId}/reset-password", teamMemberId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isNoContent());
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
                new TaskDTO(1, "Task Title 1", "Task 1 description", false, "Open", LocalDate.now(), null, 1, null, TaskPriority.MEDIUM),
                new TaskDTO(2, "Task Title 2", "Task 2 description", true, "Closed", LocalDate.now(), null, 1, null, TaskPriority.MEDIUM));

        when(teamMemberService.getAssignedTasks(1)).thenReturn(mockTasks);

        MvcResult result = mockMvc.perform(get("/api/tasks/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].priority").value("MEDIUM"))
                .andExpect(jsonPath("$[1].priority").value("MEDIUM"))
                .andReturn();

    }
}
