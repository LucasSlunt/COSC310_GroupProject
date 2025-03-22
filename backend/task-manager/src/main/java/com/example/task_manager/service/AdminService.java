package com.example.task_manager.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.entity.*;
import com.example.task_manager.enums.RoleType;
import com.example.task_manager.repository.*;

import jakarta.transaction.Transactional;

@Service // Marks this class as a Spring service, allowing it to be managed as a Spring bean
@Transactional
public class AdminService extends TeamMemberService {

	private final AdminRepository adminRepository;

	// Constructor injection for required repositories
	public AdminService(AdminRepository adminRepository, 
						TeamMemberRepository teamMemberRepository, 
						TeamRepository teamRepository, 
						IsMemberOfRepository isMemberOfRepository, 
						TaskRepository taskRepository,
						IsAssignedRepository isAssignedRepository,
						AuthInfoService authInfoService,
						NotificationService notifService) {
		super(teamMemberRepository, teamRepository, taskRepository, isMemberOfRepository, isAssignedRepository, authInfoService, notifService);
		this.adminRepository = adminRepository;
	}

	// Creates and saves a new Admin entity
	public AdminDTO createAdmin(String adminName, String adminEmail, String adminPassword) {
		Admin admin = new Admin(adminName, adminEmail, adminPassword);
		admin.setRole(RoleType.ADMIN);
		admin = adminRepository.save(admin);
		return convertToDTO(admin);
	}

	// Deletes an Admin by ID
	public void deleteAdmin(int adminId) {
		if (!adminRepository.existsById(adminId)) {
			throw new RuntimeException("Admin not found with ID: " + adminId);
		}
		adminRepository.deleteById(adminId);
	}

	// Updates an Admin's username
	public AdminDTO modifyAdminName(int adminId, String newAdminName) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));

		admin.setUserName(newAdminName);
		admin = adminRepository.save(admin);
		return convertToDTO(admin);
	}

	// Updates an Admin's email
	public AdminDTO modifyAdminEmail(int adminId, String newAdminEmail) {
		Admin admin = adminRepository.findById(adminId)
			.orElseThrow(() -> new RuntimeException("Admin not found with ID: " + adminId));
	
		admin.setUserEmail(newAdminEmail);
		admin = adminRepository.save(admin);
		return convertToDTO(admin);
	}

	// Creates and saves a new TeamMember entity
	public TeamMemberDTO createTeamMember(String userName, String userEmail, String userPassword) {
		TeamMember teamMember = new TeamMember(userName, userEmail, userPassword);
		teamMember.setRole(RoleType.TEAM_MEMBER);
		teamMember = teamMemberRepository.save(teamMember);
		return convertToDTO(teamMember);
	}

	// Deletes a TeamMember by ID
	public void deleteTeamMember(int accountId) {
		teamMemberRepository.deleteById(accountId);
	}

	// Updates a TeamMember's username
	public TeamMemberDTO modifyTeamMemberName(int userId, String newUserName) {
		TeamMember teamMember = teamMemberRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + userId));
	
		teamMember.setUserName(newUserName);
		teamMember = teamMemberRepository.save(teamMember);
		return convertToDTO(teamMember);
	}

	// Updates a TeamMember's email
	public TeamMemberDTO modifyTeamMemberEmail(int userId, String newUserEmail) {
		TeamMember teamMember = teamMemberRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + userId));

		teamMember.setUserEmail(newUserEmail);
		teamMember = teamMemberRepository.save(teamMember);
		return convertToDTO(teamMember);
	}
	
	// Promotes a member to admin or demotes a member to team member depending on what role is passed through the API
	public Object changeRole(int memberId, RoleType currentRole) {
		//if the current role is Admin they are being demoted to team member
		if (currentRole == RoleType.ADMIN) {
			Admin admin = adminRepository.findById(memberId)
					.orElseThrow(() -> new RuntimeException("Admin not found with ID: " + memberId));

			String name = admin.getUserName();
			String email = admin.getUserEmail();
			Set<IsMemberOf> teams = admin.getTeams();
			Set<IsAssigned> tasks = admin.getAssignedTasks();

			String hashed;
			String salt;
			AuthInfo authInfo = admin.getAuthInfo();
			if (authInfo != null) {
				hashed = authInfo.getHashedPassword();
				salt = authInfo.getSalt();
			} 
			else {
				throw new RuntimeException("AuthInfo is null before deletion — cannot preserve credentials.");
			}

			//deleete admin, which deletes the member from both team member and admin tables
			adminRepository.delete(admin);
			adminRepository.flush();

			TeamMember teamMember = new TeamMember(name, email, "TEMP_PASSWORD");
			teamMember.getAuthInfo().setHashedPassword(hashed);
			teamMember.getAuthInfo().setSalt(salt);

			teamMember.setTeams(teams);
			teamMember.setAssignedTasks(tasks);

			return convertToDTO(teamMemberRepository.save(teamMember));
		}
		
		//if the current role is Team Member they are being promoted to admin
		if (currentRole == RoleType.TEAM_MEMBER) {
			TeamMember teamMember = teamMemberRepository.findById(memberId)
					.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + memberId));

			String name = teamMember.getUserName();
			String email = teamMember.getUserEmail();
			Set<IsMemberOf> teams = teamMember.getTeams();
			Set<IsAssigned> tasks = teamMember.getAssignedTasks();

			String hashed;
			String salt;
			AuthInfo authInfo = teamMember.getAuthInfo();
			if (authInfo != null) {
				hashed = authInfo.getHashedPassword();
				salt = authInfo.getSalt();
			} 
			else {
				throw new RuntimeException("AuthInfo is null before deletion — cannot preserve credentials.");
			}

			teamMemberRepository.delete(teamMember);
			teamMemberRepository.flush();

			Admin admin = new Admin(name, email, "TEMP_PASSWORD");
			admin.getAuthInfo().setHashedPassword(hashed);
			admin.getAuthInfo().setSalt(salt);

			admin.setTeams(teams);
			admin.setAssignedTasks(tasks);

			return convertToDTO(adminRepository.save(admin));
		}

		throw new IllegalArgumentException("Invalid role transaction");
	}

	// Assigns a TeamMember to a Team by creating an IsMemberOf entry
	public TeamMemberDTO assignToTeam(int teamMemberId, int teamId) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));
		
		TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
			.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

		IsMemberOf isMemberOf = new IsMemberOf();
		isMemberOf.setTeam(team);
		isMemberOf.setTeamMember(teamMember);

		isMemberOfRepository.save(isMemberOf);
		
		// Ensures the association is also reflected in the TeamMember entity
		teamMember.getTeams().add(isMemberOf);
		return convertToDTO(teamMemberRepository.save(teamMember));
	}

	// Locks a Task by setting its isLocked property to true
	public void lockTask(int taskId) {
		Task task = taskRepository.findById(taskId)
			.orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
		
		task.setIsLocked(true);
		taskRepository.save(task);
	}

	// Unlocks a Task by setting its isLocked property to false
	public void unlockTask(int taskId) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

		task.setIsLocked(false);
		taskRepository.save(task);
	}

	//get all admins
	public List<AdminDTO> getAllAdmins() {
		return adminRepository.findAll().stream()
				.map(admin -> new AdminDTO(admin.getAccountId(), admin.getUserName(), admin.getUserEmail(), admin.getRole()))
				.collect(Collectors.toList());
	}

	//get all team members
	public List<TeamMemberDTO> getAllTeamMembers() {
		return adminRepository.findAll().stream()
				.map(teamMember -> new TeamMemberDTO(teamMember.getAccountId(), teamMember.getUserName(), teamMember.getUserEmail(), teamMember.getRole()))
				.collect(Collectors.toList());
	}

	//get all teams
	public List<TeamDTO> getAllTeams() {
		return teamRepository.findAll().stream()
				.map(team -> new TeamDTO(team.getTeamId(), team.getTeamName(), team.getTeamLead().getAccountId()))
				.collect(Collectors.toList());
	}

	//get a single admin
	public AdminDTO getAdminById(int adminId) {
		Admin admin = adminRepository.findById(adminId)
				.orElseThrow(() -> new RuntimeException("Admin not found"));
		return new AdminDTO(admin.getAccountId(), admin.getUserName(), admin.getUserEmail(), admin.getRole());
	}

	//get a single team member
	public TeamMemberDTO getTeamMemberById(int teamMemberId) {
		TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
			.orElseThrow(() -> new RuntimeException("Team Member not found"));
		return new TeamMemberDTO(teamMember.getAccountId(), teamMember.getUserName(), teamMember.getUserEmail(), teamMember.getRole());
	}

	private AdminDTO convertToDTO(Admin admin) {
    	return new AdminDTO(admin.getAccountId(), admin.getUserName(), admin.getUserEmail(), admin.getRole());
    }

	private TeamMemberDTO convertToDTO(TeamMember teamMember) {
        return new TeamMemberDTO(teamMember.getAccountId(), teamMember.getUserName(), teamMember.getUserEmail(), teamMember.getRole());
    }
}
