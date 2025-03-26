package com.example.task_manager.controller_tests;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.DTO.TeamRequestDTO;
import com.example.task_manager.controller.TeamController;
import com.example.task_manager.enums.RoleType;
import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.example.task_manager.entity.TeamMember;

@WebMvcTest(TeamController.class)
@ActiveProfiles("test")
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * Test Creating a Team
     */
    @Test
    void testCreateTeam() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int teamLeadId = uniqueId + 1;
        String teamName = "Development Team " + uniqueId;

        TeamRequestDTO requestDTO = new TeamRequestDTO(uniqueId, teamName, teamLeadId);
        TeamDTO mockTeam = new TeamDTO(uniqueId, teamName, teamLeadId);

        when(teamService.createTeam(anyString(), anyInt())).thenReturn(mockTeam);

        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamName").value(teamName));
    }

    /**
     * Test Deleting a Team
     */
    @Test
    void testDeleteTeam() throws Exception {
        int teamId = (int) System.nanoTime();

        doNothing().when(teamService).deleteTeam(teamId);

        mockMvc.perform(delete("/api/teams/" + teamId))
                .andExpect(status().isNoContent());
    }

    /**
     * Test Changing a Team Lead
     */
    @Test
    void testChangeTeamLead() throws Exception {
        int uniqueId = (int) System.nanoTime();
        int newLeadId = uniqueId + 1;
        String newTeamName = "Engineering Team " + uniqueId;

        TeamRequestDTO requestDTO = new TeamRequestDTO(uniqueId, newTeamName, newLeadId);
        TeamDTO mockResponse = new TeamDTO(uniqueId, newTeamName, newLeadId);

        when(teamService.changeTeamLead(uniqueId, newTeamName, newLeadId)).thenReturn(mockResponse);

        mockMvc.perform(put("/api/teams/" + uniqueId + "/change-lead")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(uniqueId))
                .andExpect(jsonPath("$.teamName").value(newTeamName))
                .andExpect(jsonPath("$.teamLeadId").value(newLeadId));
    }

    /**
     * Test Getting Team Members
     */
    @Test
    void testGetTeamMembers() throws Exception {
        int teamId = (int) System.nanoTime();
        int teamMemberId = teamId + 1;
        String memberName = "John Doe " + teamId;
        String memberEmail = "john" + teamId + "@example.com";
        RoleType role = RoleType.TEAM_MEMBER;

        TeamMemberDTO mockTeamMember = new TeamMemberDTO(teamMemberId, memberName, memberEmail, role);
        List<TeamMemberDTO> teamMembers = Collections.singletonList(mockTeamMember);

        when(teamService.getTeamMembers(teamId)).thenReturn(teamMembers);

        mockMvc.perform(get("/api/teams/" + teamId + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value(memberName));
    }

    /*
     * test getting all tasks connected to a team
     */
    @Test
    void testGetTeamTasks() throws Exception {
        int teamId = 1;

        List<TaskDTO> mockTasks = Arrays.asList(
                new TaskDTO(1, "Task 1", "Thing 1", false, "Open", LocalDate.now(), null, teamId, null, TaskPriority.HIGH),
                new TaskDTO(2, "Task 2", "Thing 2", false, "Open", LocalDate.now(), null, teamId, null, TaskPriority.HIGH)
        );

        when(teamService.getTeamTasks(teamId)).thenReturn(mockTasks);

        mockMvc.perform(get("/api/teams/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].taskId").value(1))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].status").value("Open"))
                .andExpect(jsonPath("$[1].teamId").value(teamId));
    }
}
