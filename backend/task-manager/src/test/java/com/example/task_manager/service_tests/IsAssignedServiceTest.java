package com.example.task_manager.service_tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.test_helpers.ServiceTestHelper;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
public class IsAssignedServiceTest extends ServiceTestHelper {

    @Test
    void testAssignToTask() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        IsAssignedDTO isAssignedDTO = isAssignedService.assignToTask(teamMember.getAccountId(), task.getTaskId());

        assertNotNull(isAssignedDTO);
        assertEquals(teamMember.getAccountId(), isAssignedDTO.getTeamMemberId());
        assertEquals(task.getTaskId(), isAssignedDTO.getTaskId());
    }

    @Test
    void testUnassignFromTask() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        teamMemberRepository.save(teamMember);
        teamRepository.save(team);
        taskRepository.save(task);

        taskRepository.flush();
        teamRepository.flush();
        teamMemberRepository.flush();

        isAssignedService.assignToTask(teamMember.getAccountId(), task.getTaskId());

        isAssignedService.unassignFromTask(teamMember.getAccountId(), task.getTaskId());

        assertFalse(isAssignedService.isAssignedToTask(teamMember.getAccountId(), task.getTaskId()));
    }

    @Test
    void testAssignToNonExistentTask() {
        TeamMember teamMember = createUniqueTeamMember();

        Exception exception = assertThrows(RuntimeException.class,
            () -> isAssignedService.assignToTask(teamMember.getAccountId(), 9999));

        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    void testAssignNonExistentTeamMemberToTask() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        Exception exception = assertThrows(RuntimeException.class,
            () -> isAssignedService.assignToTask(9999, task.getTaskId()));

        assertTrue(exception.getMessage().contains("Team Member not found"));
    }

    @Test
    void testAssignTeamMemberToSameTaskTwice() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        isAssignedService.assignToTask(teamMember.getAccountId(), task.getTaskId());

        Exception exception = assertThrows(RuntimeException.class,
            () -> isAssignedService.assignToTask(teamMember.getAccountId(), task.getTaskId()));

        assertTrue(exception.getMessage().contains("Team Member is already assigned"));
    }

    @Test
    void testUnassignTeamMemberNotAssignedToTask() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        Exception exception = assertThrows(RuntimeException.class,
            () -> isAssignedService.unassignFromTask(teamMember.getAccountId(), task.getTaskId()));

        assertTrue(exception.getMessage().contains("Assignment not found"));
    }

    @Test
    void testUnassignFromNonExistentTask() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        isAssignedService.assignToTask(teamMember.getAccountId(), task.getTaskId());

        Exception exception = assertThrows(RuntimeException.class,
            () -> isAssignedService.unassignFromTask(teamMember.getAccountId(), 9999));

        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    void testIsAssignedToTask() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        isAssignedService.assignToTask(teamMember.getAccountId(), task.getTaskId());
        assertTrue(isAssignedService.isAssignedToTask(teamMember.getAccountId(), task.getTaskId()));
    }

    @Test
    void testIsNotAssignedToTask() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        assertFalse(isAssignedService.isAssignedToTask(teamMember.getAccountId(), task.getTaskId()));
    }

    @Test
    void testIsAssignedForNonExistentTask() {
        TeamMember teamMember = createUniqueTeamMember();
        assertFalse(isAssignedService.isAssignedToTask(teamMember.getAccountId(), 9999));
    }

    @Test
    void testIsAssignedForNonExistentTeamMember() {
        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Task task = createUniqueTask(team);

        assertFalse(isAssignedService.isAssignedToTask(9999, task.getTaskId()));
    }
}
