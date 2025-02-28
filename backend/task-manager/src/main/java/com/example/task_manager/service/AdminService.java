package com.example.task_manager.service;

import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.entity.*;
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
						AuthInfoService authInfoService) {
		super(teamMemberRepository, teamRepository, taskRepository, isMemberOfRepository, isAssignedRepository, authInfoService);
		this.adminRepository = adminRepository;
	}

	// Creates and saves a new Admin entity
	public AdminDTO createAdmin(String adminName, String adminEmail, String adminPassword) {
		Admin admin = new Admin(adminName, adminEmail, adminPassword);
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

	// Promotes a TeamMember to Admin by removing them from the TeamMember table and adding them to the Admin table
	public AdminDTO promoteToAdmin(int teamMemberId) {
		TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
			.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

		teamMemberRepository.delete(teamMember);
		teamMemberRepository.flush(); // Ensures deletion is immediately reflected in the database

		// Creates a new Admin using the TeamMember's existing details
		Admin admin = new Admin();
		admin.setUserName(teamMember.getUserName());
		admin.setUserEmail(teamMember.getUserEmail());
		admin.setAuthInfo(teamMember.getAuthInfo());
		admin.setTeams(new HashSet<>(teamMember.getTeams()));

		admin = adminRepository.save(admin);

		return convertToDTO(admin);
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

	private AdminDTO convertToDTO(Admin admin) {
    	return new AdminDTO(admin.getAccountId(), admin.getUserName(), admin.getUserEmail());
    }

	private TeamMemberDTO convertToDTO(TeamMember teamMember) {
        return new TeamMemberDTO(teamMember.getAccountId(), teamMember.getUserName(), teamMember.getUserEmail());
    }
}
