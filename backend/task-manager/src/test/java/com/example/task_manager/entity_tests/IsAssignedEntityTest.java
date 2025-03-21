package com.example.task_manager.entity_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.task_manager.Test_setup_methods;
import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IsAssignedEntityTest extends Test_setup_methods{

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testIsAssignedDefaultConstructor() {
        IsAssigned isAssigned = new IsAssigned();
        assertNotNull(isAssigned);
    }

    @Test
    void testIsAssignedPersistence() {
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

        IsAssigned savedAssignment = entityManager.find(IsAssigned.class, isAssigned.getId());

        assertNotNull(savedAssignment);
        assertEquals(task.getTitle(), savedAssignment.getTask().getTitle());
        assertEquals(teamMember.getUserName(), savedAssignment.getTeamMember().getUserName());
        assertEquals(team.getTeamName(), savedAssignment.getTeam().getTeamName());
    }

    @Test
void testCascadeDeleteWithTask() {
    // Save Team first
    Team team = createUniqueTeam();
    entityManager.persist(team);
    entityManager.flush(); 

    // Save Task, ensuring it has a valid team reference
    Task task = createUniqueTask(team);
    entityManager.persist(task);
    entityManager.flush();

    // Save TeamMember
    TeamMember teamMember = createUniqueTeamMember();
    entityManager.persist(teamMember);
    entityManager.flush();

    // Now that everything is saved, create IsAssigned
    IsAssigned isAssigned = new IsAssigned(task, teamMember, team);
    entityManager.persist(isAssigned);
    entityManager.flush(); 

    // Remove Task and ensure IsAssigned is also deleted
    entityManager.remove(task);
    entityManager.flush();

    assertNull(entityManager.find(IsAssigned.class, isAssigned.getId()));
}

    
@Test
void testCascadeDeleteWithTeamMember() {
    // Save Team first
    Team team = createUniqueTeam();
    entityManager.persist(team);
    entityManager.flush();

    // Save Task, ensuring it has a valid team reference
    Task task = createUniqueTask(team);
    entityManager.persist(task);
    entityManager.flush();

    // Save TeamMember before using it
    TeamMember teamMember = createUniqueTeamMember();
    entityManager.persist(teamMember);
    entityManager.flush();

    // Now create and persist IsAssigned
    IsAssigned isAssigned = new IsAssigned(task, teamMember, team);
    entityManager.persist(isAssigned);
    entityManager.flush();

    // Remove TeamMember and ensure IsAssigned is also deleted
    entityManager.remove(teamMember);
    entityManager.flush();

    assertNull(entityManager.find(IsAssigned.class, isAssigned.getId()));
}


    @Test
    void testCascadeDeleteWithTeam() {
        // Save Team first
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        // Save Task, ensuring it has a valid team reference
        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        // Save TeamMember before using it
        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        // Now create and persist IsAssigned
        IsAssigned isAssigned = new IsAssigned(task, teamMember, team);
        entityManager.persist(isAssigned);
        entityManager.flush();

        // Remove Team and ensure IsAssigned is also deleted
        entityManager.remove(team);
        entityManager.flush();

        assertNull(entityManager.find(IsAssigned.class, isAssigned.getId()));
    }


    @Test
    void testRemoveIsAssignedWithoutAffectingEntities() {
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

        entityManager.remove(isAssigned);
        entityManager.flush();

        assertNotNull(entityManager.find(Task.class, task.getTaskId()));
        assertNotNull(entityManager.find(Team.class, team.getTeamId()));
        assertNotNull(entityManager.find(TeamMember.class, teamMember.getAccountId()));
    }
}
