package com.example.task_manager.service_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.*;

import com.example.task_manager.service.AdminService;
import com.example.task_manager.service.TeamService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private TeamService teamService;

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

	private AdminDTO admin;
	private TeamMemberDTO teamMember;
	private TaskDTO unlockedTask;
	private TaskDTO lockedTask;
	private Team team;

	/*
	 * Added nanoTime to each string to ensure no entities with the same name are added twice, which will cause an error
	 */

	@BeforeEach
	void setUp() {
		isAssignedRepository.deleteAllInBatch();
		isMemberOfRepository.deleteAllInBatch();
		taskRepository.deleteAllInBatch();
		authInfoRepository.deleteAllInBatch();
		adminRepository.deleteAllInBatch();
		teamRepository.deleteAllInBatch();
		teamMemberRepository.deleteAllInBatch();

		admin = adminService.createAdmin("Admin Name", "admin" + System.nanoTime() + "@example.com","defaultpw");
		teamMember = adminService.createTeamMember("TM Name", "teamMember" + System.nanoTime() + "@example.com","defaultpw");

		team = new Team();
		team.setTeamName("Team Name " + System.nanoTime());
		team = teamRepository.save(team);

		TaskRequestDTO taskRequest = new TaskRequestDTO(
			"Unlocked Task",
			"Task Description",
			false,
			"Open",
			LocalDate.now(),
			null,
			team.getTeamId()
		);

		unlockedTask = adminService.createTask(taskRequest);

		taskRequest = new TaskRequestDTO(
			"Locked Task",
			"Task Description",
			true,
			"Open",
			LocalDate.now(),
			null,
			team.getTeamId()
		);

		lockedTask = adminService.createTask(taskRequest);
	}

	// Try to create admin account
	@Test
	void testCreateAdmin() {
		AdminDTO adminDTO = adminService.createAdmin("Admin Name Testing", "admin" + System.nanoTime() + "@example.com","defaultpw");

		assertNotNull(adminDTO);
		assertEquals("Admin Name Testing", adminDTO.getUserName());
	}	

	// try to create admin with the same name
	@Test
	void testCreateAdminExistingName() {
		AdminDTO adminDTO = adminService.createAdmin("John Doe", "admin" + System.nanoTime() + "@example.com","defaultpw");

		Exception exception = assertThrows(RuntimeException.class,
				() -> adminService.createAdmin("John Doe", "admin" + System.nanoTime() + "@example.com","defaultpw"));

		assertNotNull(adminDTO);
	}
	
	// try to create admin with the same email
	@Test
	void testCreateAdminExistingEmail() { 
		AdminDTO adminDTO = adminService.createAdmin("John Doe", "admin_email@example.com","defaultpw");

		Exception exception = assertThrows(RuntimeException.class,
				() -> adminService.createAdmin("Alice Wonder", "admin_email@example.com","defaultpw"));

		assertNotNull(adminDTO);
	}
	
	@Test
	void testDeleteAdmin() {
		adminService.deleteAdmin(admin.getAccountId());
		Optional<Admin> found = adminRepository.findById(admin.getAccountId());
		assertFalse(found.isPresent());
	}

    @Test
    void testModifyAdminName() {
        AdminDTO admin = adminService.createAdmin("AdminOriginal", "original_admin" + System.nanoTime() + "@example.com", "password");
        AdminDTO updatedAdminDTO = adminService.modifyAdminName(admin.getAccountId(), "UpdatedAdmin");
        assertEquals("UpdatedAdmin", updatedAdminDTO.getUserName());
    }

    @Test
    void testModifyAdminEmail() {
        AdminDTO admin = adminService.createAdmin("AdminEmailChange", "email_admin" + System.nanoTime() + "@example.com", "password");
        String newEmail = "updated" + System.nanoTime() + "@example.com";
        AdminDTO updatedAdmin = adminService.modifyAdminEmail(admin.getAccountId(), newEmail);
        assertEquals(newEmail, updatedAdmin.getUserEmail());
    }

    @Test
    void testCreateTeamMember() {
        TeamMemberDTO teamMemberDTO = adminService.createTeamMember("Member" + System.nanoTime(), "member" + System.nanoTime() + "@example.com", "password");
        assertNotNull(teamMemberDTO);
        assertTrue(teamMemberDTO.getUserEmail().contains("@example.com"));
    }

    @Test
    void testDeleteTeamMember() {
        TeamMemberDTO teamMember = adminService.createTeamMember("ToDelete" + System.nanoTime(), "delete_member" + System.nanoTime() + "@example.com", "password");
        adminService.deleteTeamMember(teamMember.getAccountId());
        assertFalse(teamMemberRepository.findById(teamMember.getAccountId()).isPresent());
    }

    @Test
    void testModifyTeamMemberName() {
        TeamMemberDTO teamMember = adminService.createTeamMember("OldName", "name_member" + System.nanoTime() + "@example.com", "password");
        TeamMemberDTO updatedTeamMember = adminService.modifyTeamMemberName(teamMember.getAccountId(), "NewName");
        assertEquals("NewName", updatedTeamMember.getUserName());
    }

    @Test
    void testAssignToTeam() {
        TeamMemberDTO teamMember = adminService.createTeamMember("AssignedMember", "assigned_member" + System.nanoTime() + "@example.com", "password");
        TeamMemberDTO teamLead = adminService.createTeamMember("TeamLead", "lead" + System.nanoTime() + "@example.com", "password");

        TeamDTO team = teamService.createTeam("Team-" + System.nanoTime(), teamLead.getAccountId());

        adminService.assignToTeam(teamMember.getAccountId(), team.getTeamId());
        
        TeamMember updatedTeamMember = teamMemberRepository.findById(teamMember.getAccountId()).orElseThrow();
        assertTrue(updatedTeamMember.getTeams().stream()
                .anyMatch(isMember -> isMember.getTeam().getTeamId() == team.getTeamId()));
    }

    @Test
    void testLockTask() {
        Team team = teamRepository.save(new Team("LockTeam-" + System.nanoTime(), null));
        TaskRequestDTO taskRequest = new TaskRequestDTO("TaskToLock", "Lock Task Desc", false, "Open", LocalDate.now(), null, team.getTeamId());
        TaskDTO task = adminService.createTask(taskRequest);

        adminService.lockTask(task.getTaskId());
        Task updatedTask = taskRepository.findById(task.getTaskId()).orElseThrow();
        assertTrue(updatedTask.isLocked());
    }

    @Test
    void testUnlockTask() {
        Team team = teamRepository.save(new Team("UnlockTeam-" + System.nanoTime(), null));
        TaskRequestDTO taskRequest = new TaskRequestDTO("TaskToUnlock", "Unlock Task Desc", true, "Open", LocalDate.now(), null, team.getTeamId());
        TaskDTO task = adminService.createTask(taskRequest);

        adminService.unlockTask(task.getTaskId());
        Task updatedTask = taskRepository.findById(task.getTaskId()).orElseThrow();
        assertFalse(updatedTask.isLocked());
    }

    @Test
    void testModifyNonExistentAdmin() {
        Exception exception = assertThrows(RuntimeException.class, 
            () -> adminService.modifyAdminName(9999, "New Name"));
        assertNotNull(exception);
    }

    @Test
    void testAssignToNonexistentTeam() {
        TeamMemberDTO teamMember = adminService.createTeamMember("NoTeamMember", "noteam@example.com", "password");
        Exception exception = assertThrows(RuntimeException.class, 
            () -> adminService.assignToTeam(teamMember.getAccountId(), 9999));
        assertNotNull(exception);
    }

    @Test
    void testLockNonexistentTask() {
        Exception exception = assertThrows(RuntimeException.class, 
            () -> adminService.lockTask(9999));
        assertNotNull(exception);
    }
}
