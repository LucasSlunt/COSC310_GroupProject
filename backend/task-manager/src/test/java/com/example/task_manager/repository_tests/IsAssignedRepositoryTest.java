package com.example.task_manager.repository_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.task_manager.Test_setup_methods;
import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.IsAssignedRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IsAssignedRepositoryTest extends Test_setup_methods{

    @Test
    void testSaveAssignment() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        assignment = isAssignedRepository.save(assignment);

        assertNotNull(assignment);
        assertEquals(task.getTaskId(), assignment.getTask().getTaskId());
        assertEquals(teamMember.getAccountId(), assignment.getTeamMember().getAccountId());
    }

    @Test
    void testFindByTeamMemberAndTask() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        isAssignedRepository.save(assignment);

        Optional<IsAssigned> foundAssignment = isAssignedRepository.findByTeamMemberAndTask(teamMember, task);
        assertTrue(foundAssignment.isPresent());
        assertEquals(assignment.getId(), foundAssignment.get().getId());
    }

    @Test
    void testExistsByTeamMemberAndTask() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        isAssignedRepository.save(assignment);

        boolean exists = isAssignedRepository.existsByTeamMember_AccountIdAndTask_TaskId(teamMember.getAccountId(), task.getTaskId());
        assertTrue(exists);
    }

    @Test
    void testFindAssignmentsByTeamMember() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        isAssignedRepository.save(assignment);

        Collection<IsAssigned> assignments = isAssignedRepository.findByTeamMember_AccountId(teamMember.getAccountId());
        assertNotNull(assignments);
        assertEquals(1, assignments.size());
        assertEquals(task.getTaskId(), assignments.iterator().next().getTask().getTaskId());
    }

    @Test
    void testDeleteAssignment() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        assignment = isAssignedRepository.save(assignment);

        isAssignedRepository.delete(assignment);
        boolean exists = isAssignedRepository.existsByTeamMember_AccountIdAndTask_TaskId(teamMember.getAccountId(), task.getTaskId());
        assertFalse(exists);
    }

    @Test
    void testFindNonExistentAssignment() {
        TeamMember fakeTeamMember = new TeamMember();
        fakeTeamMember.setAccountId(9999);

        Task fakeTask = new Task();
        fakeTask.setTaskId(9999);

        Optional<IsAssigned> foundAssignment = isAssignedRepository.findByTeamMemberAndTask(fakeTeamMember, fakeTask);
        assertFalse(foundAssignment.isPresent());
    }

    @Test
    void testExistsByNonExistentAssignment() {
        boolean exists = isAssignedRepository.existsByTeamMember_AccountIdAndTask_TaskId(9999, 9999);
        assertFalse(exists);
    }

    @Test
    void testFindAssignmentsByTask() {
        Team team = createAndSaveUniqueTeam();
        Task task = createAndSaveUniqueTask(team);

        TeamMember teamMember1 = createAndSaveUniqueTeamMember();
        TeamMember teamMember2 = createAndSaveUniqueTeamMember();

        IsAssigned assignment1 = new IsAssigned(task, teamMember1, team);
        isAssignedRepository.save(assignment1);

        IsAssigned assignment2 = new IsAssigned(task, teamMember2, team);
        isAssignedRepository.save(assignment2);

        Collection<IsAssigned> assignmentsForTask = isAssignedRepository.findByTask(task);

        assertNotNull(assignmentsForTask);
        assertEquals(2, assignmentsForTask.size()); // Since two team members were assigned to the task
        assertTrue(assignmentsForTask.stream().anyMatch(a -> a.getTeamMember().getAccountId() == teamMember1.getAccountId()));
        assertTrue(assignmentsForTask.stream().anyMatch(a -> a.getTeamMember().getAccountId() == teamMember2.getAccountId()));
    }
}
