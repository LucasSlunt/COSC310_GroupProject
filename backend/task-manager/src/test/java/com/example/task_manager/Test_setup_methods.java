package com.example.task_manager;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.task_manager.entity.Admin;
import com.example.task_manager.entity.IsMemberOf;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.IsAssignedRepository;
import com.example.task_manager.repository.IsMemberOfRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.Task;

public abstract class Test_setup_methods {

    @Autowired
    public TestEntityManager entityManager;

    @Autowired
    public IsMemberOfRepository isMemberOfRepository;

    @Autowired
    public IsAssignedRepository isAssignedRepository;

    @Autowired
    public TaskRepository taskRepository;

    @Autowired
    public TeamMemberRepository teamMemberRepository;

    @Autowired
    public TeamRepository teamRepository;

    public Admin createUniqueAdmin() {
        return new Admin("Admin_" + System.nanoTime(), "admin_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    public TeamMember createUniqueTeamMember() {
        return new TeamMember("TeamUser_" + System.nanoTime(), "team_user_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    public Team createUniqueTeam() {
        return new Team("Team_" + System.nanoTime(), null);
    }

    public Team createUniqueTeam(TeamMember teamLead) {
        return new Team("Team_" + System.nanoTime(), teamLead);
    }

    public Task createUniqueTask(Team team) {
        return new Task("Task_" + System.nanoTime(), "Description", team, false, "Open", LocalDate.now());
    }

    //REPOSITORY CLASS METHODS

    public Team createAndSaveUniqueTeam() {
        return teamRepository.save(createUniqueTeam());
    }

    public TeamMember createAndSaveUniqueTeamMember() {
        return teamMemberRepository.save(createUniqueTeamMember());
    }

    public Task createAndSaveUniqueTask(Team team) {
        return taskRepository.save(createUniqueTask(team));
    }

    /**
     * Creates and persists a unique Team.
     */
    public Team createAndPersistUniqueTeam() {
        Team team = new Team();
        team.setTeamName("Test Team " + System.nanoTime());
        entityManager.persist(team);
        entityManager.flush();
        return team;
    }

    /**
     * Creates and persists a unique TeamMember.
     */
    public TeamMember createAndPersistUniqueTeamMember() {
        TeamMember teamMember = new TeamMember("TestUser" + System.nanoTime(),
                "test" + System.nanoTime() + "@example.com", "defaultpw");
        entityManager.persist(teamMember);
        entityManager.flush();
        return teamMember;
    }

    /**
     * Creates and persists a unique IsMemberOf relationship.
     */
    public IsMemberOf createAndPersistUniqueMembership(Team team, TeamMember teamMember) {
        IsMemberOf isMemberOf = new IsMemberOf();
        isMemberOf.setTeam(team);
        isMemberOf.setTeamMember(teamMember);
        entityManager.persist(isMemberOf);
        entityManager.flush();
        return isMemberOf;
    }

}
