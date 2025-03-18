package com.example.task_manager.controller_tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.DTO.PasswordChangeRequestDTO;
import com.example.task_manager.DTO.ResetPasswordRequestDTO;
import com.example.task_manager.controller.TeamMemberController;
import com.example.task_manager.service.TeamMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(TeamMemberController.class)
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
        TaskDTO mockTask = new TaskDTO(uniqueId, "Task Title " + uniqueId, "Description", false, "Open", LocalDate.now(), null, teamId, null);

        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Task Title " + uniqueId,
                "Description",
                false,
                "Open",
                LocalDate.of(2025, 3, 11),
                Arrays.asList(1, 2, 3),
                teamId
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
                .andExpect(jsonPath("$.teamId").value(teamId));
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
                null
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
                .andExpect(jsonPath("$.teamId").value(teamId));
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

        /**
         * Placeholder: Change Password
         */
        @Test
        void testChangePassword() throws Exception {
                PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO(123, "oldPassword", "newPassword");

                doNothing().when(teamMemberService).changePassword(123, "oldPassword", "newPassword");

                mockMvc.perform(post("/api/tasks/team-members/123/change-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isNoContent());

                verify(teamMemberService, times(1)).changePassword(123, "oldPassword", "newPassword");
        }

        //testing thast the change password fails correctly
        @Test
        void testChangePasswordFailure() throws Exception {
                PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO(123, "wrongOldPass", "newPassword");

                doThrow(new RuntimeException("Old Password is incorrect")).when(teamMemberService)
                        .changePassword(123, "wrongOldPass", "newPassword");

                mockMvc.perform(post("/api/tasks/team-members/123/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isBadRequest());

                verify(teamMemberService, times(1)).changePassword(123, "wrongOldPass", "newPassword");
        }
        
        //testing resetting a password, old password not required
        @Test
        void testResetPasswordSuccess() throws Exception {
                ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(123, "newSecurePassword");

                doNothing().when(teamMemberService).resetPassword(123, "newSecurePassword");

                mockMvc.perform(post("/api/tasks/team-members/123/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isNoContent());

                verify(teamMemberService, times(1)).resetPassword(123, "newSecurePassword");
        }
        
        //testing that it fails correctly
        @Test
        void testResetPasswordFailure() throws Exception {
                ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(123, "newSecurePassword");

                doThrow(new RuntimeException("User not found")).when(teamMemberService)
                        .resetPassword(123, "newSecurePassword");

                mockMvc.perform(post("/api/tasks/team-members/123/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isBadRequest());

                verify(teamMemberService, times(1)).resetPassword(123, "newSecurePassword");
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
                new TaskDTO(1, "Task Title 1", "Task 1 description", false, "Open", LocalDate.now(), null, 1, null),
                new TaskDTO(2, "Task Title 2", "Task 2 description", true, "Closed", LocalDate.now(), null, 1, null));

        when(teamMemberService.getAssignedTasks(1)).thenReturn(mockTasks);

        MvcResult result = mockMvc.perform(get("/api/tasks/1/tasks"))
                .andExpect(status().isOk())
                .andReturn();

    }
}
