package com.example.task_manager.service_tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;

import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.repository.AuthInfoRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.service.AdminService;
import com.example.task_manager.service.IsMemberOfService;
import com.example.task_manager.service.TeamService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class IsMemberOfServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private AdminService adminService;

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

    private TeamMemberDTO createUniqueTeamMember() {
        return adminService.createTeamMember(
            "TeamMember_" + System.nanoTime(),
            "team_member" + System.nanoTime() + "@example.com",
            "defaultpw"
        );
    }

    private TeamDTO createUniqueTeam(TeamMemberDTO teamLead) {
        return teamService.createTeam(
            "Team_" + System.nanoTime(),
            teamLead.getAccountId()
        );
    private TeamDTO team;
    private TeamMemberDTO teamLead;
    private TeamMemberDTO teamMember;

    @BeforeEach
    void setUp() {
        // Clear DB to avoid conflicts
        taskRepository.deleteAllInBatch();
        teamRepository.deleteAllInBatch();
        authInfoRepository.deleteAllInBatch();
        teamMemberRepository.deleteAllInBatch();

        teamLead = adminService.createTeamMember("Team Lead", "team_lead" + System.nanoTime() + "@example.com","defaultpw");
        team = teamService.createTeam("Development Team " + System.nanoTime(), teamLead.getAccountId());
        teamMember = adminService.createTeamMember("Team Member", "team_member" + System.nanoTime() + "@example.com","defaultpw");
    }

    @Test
    void testAddMemberToTeam() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);
        TeamMemberDTO teamMember = createUniqueTeamMember();

        isMemberOfService.addMemberToTeam(teamMember.getAccountId(), team.getTeamId());

        assertTrue(isMemberOfService.isMemberOfTeam(teamMember.getAccountId(), team.getTeamId()));
    }

    @Test
    void testAddMemberToNonExistentTeam() {
        TeamMemberDTO teamMember = createUniqueTeamMember();

        Exception exception = assertThrows(RuntimeException.class,
            () -> isMemberOfService.addMemberToTeam(teamMember.getAccountId(), 9999));

        assertTrue(exception.getMessage().contains("Team not found"));
    }

    @Test
    void testAddNonExistentMemberToTeam() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);

        Exception exception = assertThrows(RuntimeException.class,
            () -> isMemberOfService.addMemberToTeam(9999, team.getTeamId()));

        assertTrue(exception.getMessage().contains("Team Member not found"));
    }

    @Test
    void testAddMemberToSameTeamTwice() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);
        TeamMemberDTO teamMember = createUniqueTeamMember();

        isMemberOfService.addMemberToTeam(teamMember.getAccountId(), team.getTeamId());

        Exception exception = assertThrows(RuntimeException.class,
            () -> isMemberOfService.addMemberToTeam(teamMember.getAccountId(), team.getTeamId()));

        assertTrue(exception.getMessage().contains("Team Member is already in this team"));
    }

    @Test
    void testRemoveMemberFromTeam() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);
        TeamMemberDTO teamMember = createUniqueTeamMember();

        isMemberOfService.addMemberToTeam(teamMember.getAccountId(), team.getTeamId());
        isMemberOfService.removeMemberFromTeam(teamMember.getAccountId(), team.getTeamId());

        assertFalse(isMemberOfService.isMemberOfTeam(teamMember.getAccountId(), team.getTeamId()));
    }

    @Test
    void testRemoveNonExistentMemberFromTeam() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);

        Exception exception = assertThrows(RuntimeException.class,
            () -> isMemberOfService.removeMemberFromTeam(9999, team.getTeamId()));

        assertTrue(exception.getMessage().contains("Team Member not found"));
    }

    @Test
    void testRemoveMemberFromNonExistentTeam() {
        TeamMemberDTO teamMember = createUniqueTeamMember();

        Exception exception = assertThrows(RuntimeException.class,
            () -> isMemberOfService.removeMemberFromTeam(teamMember.getAccountId(), 9999));

        assertTrue(exception.getMessage().contains("Team not found"));
    }

    @Test
    void testRemoveMemberNotInTeam() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);
        TeamMemberDTO teamMember = createUniqueTeamMember();

        Exception exception = assertThrows(RuntimeException.class,
            () -> isMemberOfService.removeMemberFromTeam(teamMember.getAccountId(), team.getTeamId()));

        assertTrue(exception.getMessage().contains("Membership not found"));
    }

    @Test
    void testIsMemberOfTeam() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);
        TeamMemberDTO teamMember = createUniqueTeamMember();

        isMemberOfService.addMemberToTeam(teamMember.getAccountId(), team.getTeamId());

        assertTrue(isMemberOfService.isMemberOfTeam(teamMember.getAccountId(), team.getTeamId()));

        isMemberOfService.removeMemberFromTeam(teamMember.getAccountId(), team.getTeamId());

        assertFalse(isMemberOfService.isMemberOfTeam(teamMember.getAccountId(), team.getTeamId()));
    }

    @Test
    void testIsMemberOfNonExistentTeam() {
        TeamMemberDTO teamMember = createUniqueTeamMember();
        assertFalse(isMemberOfService.isMemberOfTeam(teamMember.getAccountId(), 9999));
    }

    @Test
    void testIsMemberOfNonExistentMember() {
        TeamMemberDTO teamLead = createUniqueTeamMember();
        TeamDTO team = createUniqueTeam(teamLead);
        assertFalse(isMemberOfService.isMemberOfTeam(9999, team.getTeamId()));
    }
}
