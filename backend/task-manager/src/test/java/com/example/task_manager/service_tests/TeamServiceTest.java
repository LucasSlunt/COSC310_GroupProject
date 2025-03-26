package com.example.task_manager.service_tests;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.task_manager.DTO.TaskDTO;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.repository.AdminRepository;
import com.example.task_manager.repository.AuthInfoRepository;
import com.example.task_manager.repository.IsAssignedRepository;
import com.example.task_manager.repository.IsMemberOfRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.service.IsMemberOfService;
import com.example.task_manager.service.TeamService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
public class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private IsMemberOfService isMemberOfService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private IsAssignedRepository isAssignedRepository;

    @Autowired
    private IsMemberOfRepository isMemberOfRepository;

    @Autowired
    private AuthInfoRepository authInfoRepository;

    private TeamMember createUniqueTeamMember(String role) {
        return teamMemberRepository.save(new TeamMember(
                role + "_" + System.nanoTime(),
                role.toLowerCase() + System.nanoTime() + "@example.com",
                "defaultpw"
        ));
    }

    private Team createUniqueTeam(TeamMember teamLead) {
        return teamRepository.save(new Team(
                "Team_" + System.nanoTime(),
                teamLead
        ));
    }

    @Test
    void testCreateTeam() {
        TeamMember teamLead = createUniqueTeamMember("Lead");
        String teamName = "QA Team " + System.nanoTime();

        TeamDTO newTeam = teamService.createTeam(teamName, teamLead.getAccountId());

        List<TeamMemberDTO> members = teamService.getTeamMembers(newTeam.getTeamId());

        assertTrue(members.stream().anyMatch(member -> member.getAccountId() == teamLead.getAccountId()),
            "Team lead should be included in the team's member list."
        );

        assertNotNull(newTeam);
        assertEquals(teamName, newTeam.getTeamName());
        assertEquals(teamLead.getAccountId(), newTeam.getTeamLeadId());
    }

    @Test
    void testCreateTeamWithEmptyName() {
        TeamMember teamLead = createUniqueTeamMember("Lead");

        Exception exception = assertThrows(RuntimeException.class,
                () -> teamService.createTeam("", teamLead.getAccountId()));

        assertTrue(exception.getMessage().contains("Team name cannot be empty"));
    }

    @Test
    void testCreateTeamWithNonExistentLead() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> teamService.createTeam("New Team", 9999));

        assertTrue(exception.getMessage().contains("Team Lead not found"));
    }

    @Test
    void testDeleteTeam() {
        TeamMember teamLead = createUniqueTeamMember("Lead");
        Team team = createUniqueTeam(teamLead);

        teamService.deleteTeam(team.getTeamId());

        Optional<Team> deletedTeam = teamRepository.findById(team.getTeamId());
        assertFalse(deletedTeam.isPresent());
    }

    @Test
    void testDeleteNonExistentTeam() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> teamService.deleteTeam(9999));

        assertTrue(exception.getMessage().contains("Team not found"));
    }

    @Test
    void testChangeTeamLead() {
        TeamMember oldLead = createUniqueTeamMember("OldLead");
        TeamMember newLead = createUniqueTeamMember("NewLead");
        Team team = createUniqueTeam(oldLead);

        String newTeamName = "Updated Team Name";

        TeamDTO updatedTeamDTO = teamService.changeTeamLead(team.getTeamId(), newTeamName, newLead.getAccountId());

        assertNotNull(updatedTeamDTO);
        assertEquals(newTeamName, updatedTeamDTO.getTeamName());
        assertEquals(newLead.getAccountId(), updatedTeamDTO.getTeamLeadId());

        Team updatedTeam = teamRepository.findById(team.getTeamId()).orElseThrow();
        assertEquals(newTeamName, updatedTeam.getTeamName());
        assertEquals(newLead.getAccountId(), updatedTeam.getTeamLead().getAccountId());
    }

    @Test
    void testChangeTeamLeadToNonExistentMember() {
        TeamMember oldLead = createUniqueTeamMember("OldLead");
        Team team = createUniqueTeam(oldLead);

        String newTeamName = "Updated Team Name";

        Exception exception = assertThrows(RuntimeException.class,
                () -> teamService.changeTeamLead(team.getTeamId(), newTeamName, 9999));

        assertTrue(exception.getMessage().contains("Team Lead not found"));
    }

    @Test
    void testGetTeamMembers() {
        TeamMember teamLead = createUniqueTeamMember("Lead");
        TeamMember member = createUniqueTeamMember("Member");
        Team team = createUniqueTeam(teamLead);

        isMemberOfService.addMemberToTeam(member.getAccountId(), team.getTeamId());

        List<TeamMemberDTO> teamMembers = teamService.getTeamMembers(team.getTeamId());

        assertNotNull(teamMembers);
        assertFalse(teamMembers.isEmpty());

        assertTrue(teamMembers.stream()
                .anyMatch(t -> t.getAccountId() == member.getAccountId()));
    }

    @Test
    void testGetMembersOfNonExistentTeam() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> teamService.getTeamMembers(9999));

        assertTrue(exception.getMessage().contains("Team not found"));
    }

    @Test
    void testAddMemberToNonExistentTeam() {
        TeamMember member = createUniqueTeamMember("Member");

        Exception exception = assertThrows(RuntimeException.class,
                () -> isMemberOfService.addMemberToTeam(member.getAccountId(), 9999));

        assertTrue(exception.getMessage().contains("Team not found"));
    }

    @Test
    void testGetMembersOfTeamWithNoMembers() {
        TeamMember teamLead = createUniqueTeamMember("Lead");
        Team team = createUniqueTeam(teamLead);

        List<TeamMemberDTO> members = teamService.getTeamMembers(team.getTeamId());
        assertTrue(members.isEmpty());
    }

    @Test
    void testGetTeamTasks() {
        TeamMember teamMember = createUniqueTeamMember("ADMIN");
        Team team = createUniqueTeam(teamMember);

        Task task = new Task("Dire Straits", "Die Straits fan club.", team, false, "Open", LocalDate.now(), TaskPriority.HIGH);

        taskRepository.save(task);

        IsAssigned isAssigned = new IsAssigned(task, teamMember, team);

        isAssignedRepository.save(isAssigned);

        task.getAssignedMembers().add(isAssigned);
        taskRepository.save(task);

        List<TaskDTO> teamTasks = teamService.getTeamTasks(team.getTeamId());

        TaskDTO taskDTO = teamTasks.get(0);
        assertEquals(task.getTaskId(), taskDTO.getTaskId());
        assertEquals("Dire Straits", taskDTO.getTitle());
        assertEquals(team.getTeamId(), taskDTO.getTeamId());
        assertEquals(1, taskDTO.getAssignedMembers().size());

        TeamMemberDTO teamMemberDTO = taskDTO.getAssignedMembers().get(0);
        assertEquals(teamMember.getAccountId(), teamMemberDTO.getAccountId());
        assertEquals(teamMember.getUserName(), teamMemberDTO.getUserName());

    }

}
