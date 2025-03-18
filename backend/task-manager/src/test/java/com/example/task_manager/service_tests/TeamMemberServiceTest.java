package com.example.task_manager.service_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.entity.Admin;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.repository.AdminRepository;
import com.example.task_manager.repository.AuthInfoRepository;
import com.example.task_manager.repository.IsAssignedRepository;
import com.example.task_manager.repository.IsMemberOfRepository;
import com.example.task_manager.service.AdminService;
import com.example.task_manager.service.AuthInfoService;
import com.example.task_manager.service.TeamMemberService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class TeamMemberServiceTest {

    @Autowired
    private AuthInfoService authInfoService;

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private AdminService adminService;

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

    private TeamMember createUniqueTeamMember() {
        return teamMemberRepository.save(new TeamMember(
            "TeamMember_" + System.nanoTime(), 
            "team_member" + System.nanoTime() + "@example.com", 
            "defaultpw"
        ));
    }

    private Team createUniqueTeam(TeamMember teamLead) {
        return teamRepository.save(new Team(
            "Team_" + System.nanoTime(),
            teamLead
        ));
    }

    private Task createUniqueTask(Team team) {
        return taskRepository.save(new Task(
            "Task_" + System.nanoTime(), 
            "Task Description", 
            team, 
            false, 
            "Open", 
            LocalDate.now()
        ));
    }

    @Test
    void testCreateTask() {
        TeamMember teamLead = createUniqueTeamMember();
        Team team = createUniqueTeam(teamLead);
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
            "New Task",
            "Task Description",
            false,
            "Open",
            LocalDate.now().plusDays(5),
            null,
            team.getTeamId()
        );

        TaskDTO newTaskDTO = teamMemberService.createTask(taskRequestDTO);

        assertNotNull(newTaskDTO);
        assertEquals("New Task", newTaskDTO.getTitle());
        assertEquals("Task Description", newTaskDTO.getDescription());
        assertEquals(team.getTeamId(), newTaskDTO.getTeamId());
    }

    @Test
    void testCreateTaskWithNullTitle() {
        TeamMember teamLead = createUniqueTeamMember();
        Team team = createUniqueTeam(teamLead);

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
            null, "Task Description", false, "Open", LocalDate.now(), null, team.getTeamId()
        );

        Exception exception = assertThrows(RuntimeException.class, () -> 
            teamMemberService.createTask(taskRequestDTO));

        assertTrue(exception.getMessage().contains("Task title cannot be null or empty"));
    }

    @Test
    void testCreateTaskWithNullTeam() {
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
            "New Task", "Task Description", false, "Open", LocalDate.now(), null, null
        );

        Exception exception = assertThrows(RuntimeException.class, () -> 
            teamMemberService.createTask(taskRequestDTO));

        assertTrue(exception.getMessage().contains("Task must be assigned to a team"));
    }

    @Test
    void testDeleteTask() {
        TeamMember teamLead = createUniqueTeamMember();
        Team team = createUniqueTeam(teamLead);
        Task task = createUniqueTask(team);

        teamMemberService.deleteTask(task.getTaskId());

        Optional<Task> deletedTask = taskRepository.findById(task.getTaskId());
        assertFalse(deletedTask.isPresent());
    }

    @Test
    void testDeleteNonExistentTask() {
        Exception exception = assertThrows(RuntimeException.class, 
            () -> teamMemberService.deleteTask(9999));

        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    void testEditTask() {
        TeamMember teamLead = createUniqueTeamMember();
        Team team = createUniqueTeam(teamLead);
        Task task = createUniqueTask(team);

        TaskDTO taskDTO = new TaskDTO(
            task.getTaskId(),
            "Updated Task Title",
            "Updated Description",
            true,
            "In Progress",
            LocalDate.now(),
            null,
            team.getTeamId(),
            null
        );

        TaskDTO updatedTask = teamMemberService.editTask(task.getTaskId(), taskDTO);

        assertEquals("Updated Task Title", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals("In Progress", updatedTask.getStatus());
        assertTrue(updatedTask.getIsLocked());
    }

    @Test
    void testAssignToTask() {
        TeamMember teamLead = createUniqueTeamMember();
        Team team = createUniqueTeam(teamLead);
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        teamMemberService.assignToTask(task.getTaskId(), teamMember.getAccountId());

        assertTrue(teamMemberService.getAssignedTasks(teamMember.getAccountId())
            .stream()
            .anyMatch(t -> t.getTaskId() == task.getTaskId()));
    }

    @Test
    void testChangePassword() {
        TeamMember teamMember = createUniqueTeamMember();
        int teamMemberId = teamMember.getAccountId();

        teamMemberService.changePassword(teamMemberId, "defaultpw", "coolnewpassword");

        assertTrue(authInfoService.approveLogin(teamMemberId, "coolnewpassword"));
    }

    @Test
    void testChangePasswordButSupplyWrongPassword() {
        TeamMember teamMember = createUniqueTeamMember();
        int teamMemberId = teamMember.getAccountId();

        teamMemberService.changePassword(teamMemberId, "wrongpw", "coolnewpassword");

        assertFalse(authInfoService.approveLogin(teamMemberId, "coolnewpassword"));
    }

    @Test
    void testChangePasswordTM() {
        TeamMember teamMember = createUniqueTeamMember();
        int teamMemberId = teamMember.getAccountId();

        teamMemberService.resetPassword(teamMemberId, "coolernewpassword");

        assertTrue(authInfoService.approveLogin(teamMemberId, "coolernewpassword"));
    }

    @Test
    void testChangePasswordTMWithNullPassword() {
        TeamMember teamMember = createUniqueTeamMember();
        int teamMemberId = teamMember.getAccountId();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            teamMemberService.resetPassword(teamMemberId, null);
        });

        assertEquals("Cannot change password to null or empty string", exception.getMessage());
    }

    @Test
    void testChangePasswordTMWithEmptyPassword() {
        TeamMember teamMember = createUniqueTeamMember();
        int teamMemberId = teamMember.getAccountId();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            teamMemberService.resetPassword(teamMemberId, null);
        });
        
        assertEquals("Cannot change password to null or empty string", exception.getMessage());
    }

    @Test
    void testIsAdmin() {
        Admin admin = new Admin("Admin_" + System.nanoTime(), "admin_" + System.nanoTime() + "@example.com", "adminpw");
        admin = teamMemberRepository.save(admin);

        boolean isAdmin = authInfoService.isAdmin(admin.getAccountId());
        assertTrue(isAdmin);
    }

    @Test
    void testIsNotAdmin() {
        TeamMember teamMember = createUniqueTeamMember();
        boolean isAdmin = authInfoService.isAdmin(teamMember.getAccountId());
        assertFalse(isAdmin);
    }

    @Test
    void testIsAdminForNonExistentMember() {
        int fakeId = 999999; // Assuming this ID does not exist

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authInfoService.isAdmin(fakeId);
        });

        assertTrue(exception.getMessage().contains("Team Member not found"));
    }

    @Test
    void testGetTeamsForMember() {
        TeamMember teamMember = createUniqueTeamMember();
        teamMember = teamMemberRepository.save(teamMember);
        Team team = createUniqueTeam(teamMember);
        team = teamRepository.save(team);
        Team team2 = createUniqueTeam(teamMember);
        team2 = teamRepository.save(team2);

        adminService.assignToTeam(teamMember.getAccountId(), team.getTeamId());
    	adminService.assignToTeam(teamMember.getAccountId(), team2.getTeamId());

		List<TeamDTO> teamsForMember = teamMemberService.getTeamsForMember(teamMember.getAccountId());

		System.out.println("Found " + teamsForMember.size() + " memberships in DB");

		assertNotNull(teamsForMember);
		assertEquals(2, teamsForMember.size());

		assertEquals(team.getTeamId(), teamsForMember.get(0).getTeamId());
		assertEquals(team.getTeamName(), teamsForMember.get(0).getTeamName());
		assertEquals(team.getTeamLead().getAccountId(), teamsForMember.get(0).getTeamLeadId());

		assertEquals(team2.getTeamId(), teamsForMember.get(1).getTeamId());
		assertEquals(team2.getTeamName(), teamsForMember.get(1).getTeamName());
		assertEquals(team2.getTeamLead().getAccountId(), teamsForMember.get(1).getTeamLeadId());
	}

	@Test
    void testGetAllTeams() {
        teamRepository.deleteAll();

        TeamMember teamMember = createUniqueTeamMember();
        Team team = createUniqueTeam(teamMember);
        Team team2 = createUniqueTeam(teamMember);

		List<TeamDTO> teams = adminService.getAllTeams();

		System.out.println("Found " + teams.size() + " teams in DB");

		assertNotNull(teams);
		assertEquals(2, teams.size());

		assertEquals(team.getTeamId(), teams.get(0).getTeamId());
		assertEquals(team.getTeamName(), teams.get(0).getTeamName());
		assertEquals(team.getTeamLead().getAccountId(), teams.get(0).getTeamLeadId());

		assertEquals(team2.getTeamId(), teams.get(1).getTeamId());
		assertEquals(team2.getTeamName(), teams.get(1).getTeamName());
		assertEquals(team2.getTeamLead().getAccountId(), teams.get(1).getTeamLeadId());
	}
}
