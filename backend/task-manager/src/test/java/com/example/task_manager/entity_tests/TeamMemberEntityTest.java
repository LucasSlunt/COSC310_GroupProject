package com.example.task_manager.entity_tests;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.task_manager.entity.AuthInfo;
import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.IsMemberOf;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.enums.TaskPriority;

import jakarta.persistence.PersistenceException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeamMemberEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private TeamMember createUniqueTeamMember() {
        return new TeamMember("User_" + System.nanoTime(), "user_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    private Team createUniqueTeam() {
        return new Team("Team_" + System.nanoTime(), null);
    }

    private Task createUniqueTask(Team team) {
        return new Task("Task_" + System.nanoTime(), "Description", team, false, "Open", LocalDate.now(), TaskPriority.LOW);
    }

    @Test
    void testTeamMemberPersistence() {
        TeamMember teamMember = createUniqueTeamMember();
        TeamMember savedMember = entityManager.persistFlushFind(teamMember);

        assertNotNull(savedMember.getAccountId());
        assertEquals(teamMember.getUserName(), savedMember.getUserName());
        assertEquals(teamMember.getUserEmail(), savedMember.getUserEmail());
    }

    @Test
    void testUniqueNameConstraint() {
        TeamMember member1 = createUniqueTeamMember();
        entityManager.persist(member1);
        entityManager.flush();

        TeamMember member2 = new TeamMember(member1.getUserName(), "unique_" + System.nanoTime() + "@example.com", "defaultpw");

        Exception e = assertThrows(PersistenceException.class, () -> {
            entityManager.persist(member2);
            entityManager.flush();
        });

        assertNotNull(e);
    }

    @Test
    void testUniqueEmailConstraint() {
        TeamMember member1 = createUniqueTeamMember();
        entityManager.persist(member1);
        entityManager.flush();

        TeamMember member2 = new TeamMember("Different_" + System.nanoTime(), member1.getUserEmail(), "defaultpw");

        Exception e = assertThrows(PersistenceException.class, () -> {
            entityManager.persist(member2);
            entityManager.flush();
        });

        assertNotNull(e);
    }

    @Test
    void testAuthInfoRelation() {
        TeamMember teamMember = createUniqueTeamMember();
        AuthInfo authInfo = new AuthInfo("hashed_password", "random_salt", teamMember);
        teamMember.setAuthInfo(authInfo);

        TeamMember savedMember = entityManager.persistFlushFind(teamMember);

        assertNotNull(savedMember.getAuthInfo());
        assertEquals("hashed_password", savedMember.getAuthInfo().getHashedPassword());
    }

    @Test
    void testIsMemberOfRelation() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        IsMemberOf isMemberOf = new IsMemberOf(teamMember, team);
        entityManager.persist(isMemberOf);
        entityManager.flush();

        teamMember.getTeams().add(isMemberOf);

        TeamMember savedMember = entityManager.persistFlushFind(teamMember);
        assertEquals(1, savedMember.getTeams().size());
    }

    @Test
    void testIsAssignedTasksRelation() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        Task task = createUniqueTask(team);
        entityManager.persist(task);
        entityManager.flush();

        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        IsAssigned assignedTask = new IsAssigned(task, teamMember, team);
        entityManager.persist(assignedTask);
        entityManager.flush();

        teamMember.getAssignedTasks().add(assignedTask);
        TeamMember savedMember = entityManager.persistFlushFind(teamMember);

        assertEquals(1, savedMember.getAssignedTasks().size());
    }

    @Test
    void testDeleteTeamMemberRemovesAuthInfoButNotTeamsOrTasks() {
        Team team = new Team("Team_" + System.nanoTime(), null);
        entityManager.persist(team);
        entityManager.flush();

        Task task = new Task("Task_" + System.nanoTime(), "Description", team, false, "Open", LocalDate.now(), TaskPriority.LOW);
        entityManager.persist(task);
        entityManager.flush();

        TeamMember teamMember = new TeamMember(
            "User_" + System.nanoTime(),
            "user_" + System.nanoTime() + "@example.com",
            "defaultpw"
        );
        entityManager.persist(teamMember);
        entityManager.flush();

        AuthInfo authInfo = teamMember.getAuthInfo();
        assertNotNull(authInfo);
        assertEquals(teamMember.getAccountId(), authInfo.getAccountId());

        IsMemberOf isMember = new IsMemberOf(teamMember, team);
        IsAssigned isAssigned = new IsAssigned(task, teamMember, team);

        entityManager.persist(isMember);
        entityManager.persist(isAssigned);
        entityManager.flush();

        entityManager.remove(teamMember);

        assertNull(entityManager.find(AuthInfo.class, teamMember.getAccountId()));
        assertNotNull(entityManager.find(Task.class, task.getTaskId()));
        assertNotNull(entityManager.find(Team.class, team.getTeamId()));
    }

    @Test
    void testTeamMemberLeavesTeamWithoutDeletion() {
        Team team = createUniqueTeam();
        entityManager.persist(team);
        entityManager.flush();

        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        IsMemberOf isMember = new IsMemberOf(teamMember, team);
        entityManager.persist(isMember);
        entityManager.flush();

        entityManager.remove(isMember);
        entityManager.flush();

        assertNull(entityManager.find(IsMemberOf.class, isMember.getId()));
        assertNotNull(entityManager.find(TeamMember.class, teamMember.getAccountId()));
    }

    @Test
    void testDeletingTeamMemberRemovesAssignedTasks() {
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
        teamMember.getAssignedTasks().add(isAssigned);
        
        entityManager.persist(isAssigned);
        entityManager.flush();

        entityManager.remove(teamMember);
        entityManager.flush();

        assertNull(entityManager.find(IsAssigned.class, isAssigned.getId()));
    }
}
