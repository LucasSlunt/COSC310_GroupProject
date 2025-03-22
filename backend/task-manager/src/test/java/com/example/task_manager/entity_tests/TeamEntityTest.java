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
import com.example.task_manager.entity.IsMemberOf;
import com.example.task_manager.enums.TaskPriority;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TeamEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private TeamMember createUniqueTeamMember() {
        return new TeamMember("User_" + System.nanoTime(), "user_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    private Team createUniqueTeam(TeamMember teamLead) {
        return new Team("Team_" + System.nanoTime(), teamLead);
    }

    private Task createUniqueTask(Team team) {
        return new Task("Task_" + System.nanoTime(), "Description", team, false, "Open", LocalDate.now(), TaskPriority.LOW);
    }

    @Test
    void testTeamPersistence() {
        TeamMember teamLead = createUniqueTeamMember();
        entityManager.persist(teamLead);
        entityManager.flush();

        Team team = createUniqueTeam(teamLead);
        entityManager.persist(team);
        entityManager.flush();

        Team savedTeam = entityManager.find(Team.class, team.getTeamId());

        assertNotNull(savedTeam);
        assertEquals(team.getTeamName(), savedTeam.getTeamName());
        assertEquals(teamLead.getUserName(), savedTeam.getTeamLead().getUserName());
    }

    @Test
    void testTeamWithMembers() {
        TeamMember teamLead = createUniqueTeamMember();
        entityManager.persist(teamLead);
        entityManager.flush();

        Team team = createUniqueTeam(teamLead);
        entityManager.persist(team);
        entityManager.flush();

        TeamMember member1 = createUniqueTeamMember();
        TeamMember member2 = createUniqueTeamMember();
        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.flush();

        IsMemberOf isMember1 = new IsMemberOf(member1, team);
        IsMemberOf isMember2 = new IsMemberOf(member2, team);

        team.getMembers().add(isMember1);
        team.getMembers().add(isMember2);
        entityManager.persist(team);
        entityManager.persist(isMember1);
        entityManager.persist(isMember2);
        entityManager.flush();

        Team savedTeam = entityManager.find(Team.class, team.getTeamId());
        entityManager.refresh(savedTeam);

        assertNotNull(savedTeam);
        assertEquals(2, savedTeam.getMembers().size());
    }

    @Test
    void testCascadeDeleteWithTeam() {
        TeamMember teamLead = createUniqueTeamMember();
        entityManager.persist(teamLead);
    
        Team team = createUniqueTeam(teamLead);
        entityManager.persist(team);
    
        Task task = createUniqueTask(team);
        entityManager.persist(task);
    
        TeamMember devMember = createUniqueTeamMember();
        entityManager.persist(devMember);
    
        IsAssigned isAssigned = new IsAssigned(task, devMember, team);
        entityManager.persist(isAssigned);
    
        entityManager.flush();
    
        task.getAssignedMembers().remove(isAssigned);
        team.getAssignedTasks().remove(isAssigned);
        devMember.getAssignedTasks().remove(isAssigned);
        entityManager.flush();
    
        entityManager.remove(isAssigned);
        entityManager.flush();
    
        IsAssigned foundAssignment = entityManager.find(IsAssigned.class, isAssigned.getId());
        assertNull(foundAssignment);
    }

    @Test
    void testUpdateTeamLead() {
        TeamMember oldLead = createUniqueTeamMember();
        TeamMember newLead = createUniqueTeamMember();
        entityManager.persist(oldLead);
        entityManager.persist(newLead);
        entityManager.flush();

        Team team = createUniqueTeam(oldLead);
        entityManager.persist(team);
        entityManager.flush();

        team.setTeamLead(newLead);
        entityManager.persist(team);
        entityManager.flush();

        Team updatedTeam = entityManager.find(Team.class, team.getTeamId());

        assertNotNull(updatedTeam);
        assertEquals(newLead.getAccountId(), updatedTeam.getTeamLead().getAccountId());
    }

    @Test
    void testTeamWithoutTeamLead() {
        Team team = new Team("TeamWithoutLead", null);
        entityManager.persist(team);
        entityManager.flush();

        Team savedTeam = entityManager.find(Team.class, team.getTeamId());

        assertNotNull(savedTeam);
        assertNull(savedTeam.getTeamLead());
    }

    @Test
    void testDeleteTeamDoesNotDeleteMembers() {
        TeamMember teamMember1 = createUniqueTeamMember();
        TeamMember teamMember2 = createUniqueTeamMember();
        entityManager.persist(teamMember1);
        entityManager.persist(teamMember2);
        entityManager.flush();

        Team team = createUniqueTeam(null);
        entityManager.persist(team);
        entityManager.flush();

        IsMemberOf isMember1 = new IsMemberOf(teamMember1, team);
        IsMemberOf isMember2 = new IsMemberOf(teamMember2, team);
        entityManager.persist(isMember1);
        entityManager.persist(isMember2);
        entityManager.flush();

        team.getMembers().clear();
        team.getAssignedTasks().clear();
        entityManager.flush();

        entityManager.remove(team);

        assertNotNull(entityManager.find(TeamMember.class, teamMember1.getAccountId()));
        assertNotNull(entityManager.find(TeamMember.class, teamMember2.getAccountId()));
    }

    @Test
    void testUpdateTeamName() {
        Team team = new Team("Old Team Name", null);
        entityManager.persist(team);
        entityManager.flush();

        team.setTeamName("New Team Name");
        entityManager.persist(team);
        entityManager.flush();

        Team updatedTeam = entityManager.find(Team.class, team.getTeamId());

        assertNotNull(updatedTeam);
        assertEquals("New Team Name", updatedTeam.getTeamName());
    }
}
