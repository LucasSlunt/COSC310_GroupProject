package com.example.task_manager.entity_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.enums.TaskPriority;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private Team createUniqueTeam() {
        return new Team("Team_" + System.nanoTime(), null);
    }

    private TeamMember createUniqueTeamMember() {
        return new TeamMember("Member_" + System.nanoTime(), "user_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    private Task createUniqueTask(Team team) {
        return new Task("Task_" + System.nanoTime(), "Task description", team, false, "Open", LocalDate.now(), TaskPriority.LOW);
    }

    @Test
    void testTaskPersistence() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        Task savedTask = entityManager.find(Task.class, task.getTaskId());

        assertNotNull(savedTask);
        assertEquals(task.getTitle(), savedTask.getTitle());
        assertEquals(team.getTeamName(), savedTask.getTeam().getTeamName());
    }

    @Test
    void testTaskWithAssignment() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        IsAssigned isAssigned = new IsAssigned(task, teamMember, team);
        entityManager.persist(isAssigned);
        entityManager.flush();

        task.getAssignedMembers().add(isAssigned);
        entityManager.persist(task);
        entityManager.flush();

        Task savedTask = entityManager.find(Task.class, task.getTaskId());

        assertNotNull(savedTask);
        assertEquals(1, savedTask.getAssignedMembers().size());
    }
    
    @Test
    void testCascadeDeleteWithTask() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        team.getTasks().add(task); 

        TeamMember teamMember = createUniqueTeamMember();
        //entityManager.persist(teamMember);

        IsAssigned isAssigned = new IsAssigned(task, teamMember, team);
        //entityManager.persist(isAssigned);

        team.getTasks().remove(task);
        entityManager.remove(isAssigned.getTask());

        assertNull(entityManager.find(Task.class, task.getTaskId()));
        assertNull(entityManager.find(IsAssigned.class, isAssigned.getId()));
    }


    @Test
    void testUpdateTaskStatus() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        task.setStatus("Completed");
        entityManager.persist(task);
        entityManager.flush();

        Task updatedTask = entityManager.find(Task.class, task.getTaskId());

        assertNotNull(updatedTask);
        assertEquals("Completed", updatedTask.getStatus());
    }

    @Test
    void testToggleTaskLock() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        task.setIsLocked(true);
        entityManager.persist(task);
        entityManager.flush();

        Task lockedTask = entityManager.find(Task.class, task.getTaskId());
        assertNotNull(lockedTask);
        assertTrue(lockedTask.isLocked());

        lockedTask.setIsLocked(false);
        entityManager.persist(lockedTask);
        entityManager.flush();

        Task unlockedTask = entityManager.find(Task.class, task.getTaskId());
        assertNotNull(unlockedTask);
        assertFalse(unlockedTask.isLocked());
    }

    @Test
    void testUpdateTaskTitle() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        task.setTitle("Updated Task Title");
        entityManager.persist(task);
        entityManager.flush();

        Task updatedTask = entityManager.find(Task.class, task.getTaskId());

        assertNotNull(updatedTask);
        assertEquals("Updated Task Title", updatedTask.getTitle());
    }

    @Test
    void testDeleteTaskDoesNotDeleteTeamOrTeamMember() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        IsAssigned isAssigned = new IsAssigned(task, teamMember, team);
        entityManager.persist(isAssigned);
        entityManager.flush();

        entityManager.remove(task);
        entityManager.flush();

        assertNotNull(entityManager.find(Team.class, team.getTeamId()));
        assertNotNull(entityManager.find(TeamMember.class, teamMember.getAccountId()));
    }
}
