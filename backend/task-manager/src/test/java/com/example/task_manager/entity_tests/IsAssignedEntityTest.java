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
public class IsAssignedEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private Team createUniqueTeam() {
        return new Team("Team_" + System.nanoTime(), null);
    }

    private TeamMember createUniqueTeamMember() {
        return new TeamMember("Member_" + System.nanoTime(), "user_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    private Task createUniqueTask(Team team) {
        return new Task("Task_" + System.nanoTime(), "Description", team, false, "Open", LocalDate.now(), TaskPriority.LOW);
    }

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
    entityManager.refresh(task);
    entityManager.refresh(isAssigned);

    // Remove Task and ensure IsAssigned is also deleted
    entityManager.remove(isAssigned.getTask());

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
    entityManager.refresh(teamMember);
    entityManager.refresh(isAssigned);

    // Remove TeamMember and ensure IsAssigned is also deleted
    entityManager.remove(isAssigned.getTeamMember());

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
        entityManager.refresh(team);
        entityManager.refresh(isAssigned);

        // Remove Team and ensure IsAssigned is also deleted
        entityManager.remove(isAssigned.getTeam());

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
        entityManager.remove(isAssigned);
        entityManager.flush();

        //code snippet to debug.
        //this test is throwing a true negative
        //System.out.println("\n(end) taskId is: "+task.getTaskId());
        //System.out.println("EntMan get id of task: "+ entityManager.getId(task));
        //System.out.println("EntMan find task: "+ entityManager.find(Task.class, entityManager.getId(task)) +"\n");

        assertNotNull(entityManager.find(Task.class, entityManager.getId(task)));
        assertNotNull(entityManager.find(Team.class, entityManager.getId(team)));
        assertNotNull(entityManager.find(TeamMember.class, entityManager.getId(teamMember)));
    }
}
