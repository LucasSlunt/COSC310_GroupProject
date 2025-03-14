package com.example.task_manager.service_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.transaction.Transactional;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.entity.Admin;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.repository.AuthInfoRepository;
import com.example.task_manager.repository.IsAssignedRepository;
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
	private Task task;
	private TeamMember teamMember;
	private Team team;

	@BeforeEach
	void setUp() {
		isAssignedRepository.deleteAllInBatch();
		taskRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
		authInfoRepository.deleteAllInBatch();
		teamMemberRepository.deleteAllInBatch();

		teamMember = new TeamMember("Team Member", "teamMember" + System.nanoTime() + "@example.com","defaultpw");
		teamMember = teamMemberRepository.save(teamMember);

		team = new Team("Team Name " + System.nanoTime(), teamMember);
		team = teamRepository.save(team);

		task = new Task("Task Title " + System.nanoTime(), "Task Description", team, false, "Open", LocalDate.now());
		task = taskRepository.save(task);
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
            team.getTeamId()
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
}
