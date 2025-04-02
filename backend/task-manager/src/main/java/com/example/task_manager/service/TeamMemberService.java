package com.example.task_manager.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.IsAssignedRepository;
import com.example.task_manager.repository.IsMemberOfRepository;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.enums.TaskPriority;

import jakarta.transaction.Transactional;

@Service // Marks this class as a Spring service, making it eligible for dependency injection
@Transactional // Ensures all methods run within a transactional context
public class TeamMemberService {
	
	protected final TeamMemberRepository teamMemberRepository;
	protected final TeamRepository teamRepository;
	protected final IsMemberOfRepository isMemberOfRepository;
	protected final TaskRepository taskRepository;
    protected final IsAssignedRepository isAssignedRepository;
    protected final NotificationRepository notifRepository;
	protected final AuthInfoService authInfoService;
	protected final NotificationService notifService;

	// Constructor for required repositories
	public TeamMemberService(TeamMemberRepository teamMemberRepository, 
							 TeamRepository teamRepository, 
							 TaskRepository taskRepository, 
							 IsMemberOfRepository isMemberOfRepository, 
							 IsAssignedRepository isAssignedRepository,
							 AuthInfoService authInfoService,
							 NotificationService notifService,
                             NotificationRepository notifRepository) {
		this.teamMemberRepository = teamMemberRepository;
		this.teamRepository = teamRepository;
		this.isMemberOfRepository = isMemberOfRepository;
		this.taskRepository = taskRepository;
		this.isAssignedRepository = isAssignedRepository;
		this.authInfoService = authInfoService;
        this.notifService = notifService;
        this.notifRepository = notifRepository;
	}
	
	/**
	 * Creates and saves a new Task.
	 * Allows optional parameters such as description, expected completion date, and due date.
	 * 
	 * @param title                 The title of the task.
	 * @param description           The task description (nullable).
	 * @param isLocked              Indicates if the task is locked (nullable).
	 * @param status                The current status of the task.
	 * @param expectedCompletionDate Expected completion date (nullable).
	 * @param dueDate               The due date of the task (nullable).
	 * @param team                  The team responsible for the task.
	 * @param assignedMembers       The set of assigned members (nullable).
	 * @return The created Task entity.
	 */
	public TaskDTO createTask(TaskRequestDTO request) {

		if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
			throw new RuntimeException("Task title cannot be null or empty");
		}

		if (request.getTeamId() == null) {
			throw new RuntimeException("Task must be assigned to a team");
		}
	
		Team team = teamRepository.findById(request.getTeamId())
				.orElseThrow(() -> new RuntimeException("Task must be assigned to a valid team"));

		TaskPriority priority = request.getPriority() != null ? request.getPriority() : TaskPriority.LOW;

		Task task = new Task();
		task.setTitle(request.getTitle());
		task.setIsLocked(request.getIsLocked());
		task.setStatus(request.getStatus());
		task.setDateCreated(LocalDate.now());
		task.setTeam(team);
		task.setPriority(priority);
		task.setDueDate(request.getDueDate());
		if (request.getDescription() != null) {
			task.setDescription(request.getDescription());
		}
		if (request.getDueDate() != null) {
			task.setExpectedCompletionDate(request.getDueDate());
		}

		task = taskRepository.save(task);
		return convertToDTO(task);
	}

	/**
	 * Deletes a task by ID.
	 * Explicitly removes associated assignments before deleting the task.
	 *
	 * @param taskId The ID of the task to be deleted.
	 */
	public void deleteTask(int taskId) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
	
		// Ensure all assignments are removed before deleting the task
        isAssignedRepository.deleteAllByTask_TaskId(taskId);
	    notifRepository.deleteAllByTask_TaskId(taskId); // 👈 add this

		taskRepository.delete(task);
	}    

	/**
	 * Edits the details of an existing Task.
	 * Allows partial updates; only non-null and non-empty values are updated.
	 *
	 * @param taskId                The ID of the task to be updated.
	 * @param newTitle              The new title (nullable).
	 * @param newDescription        The new description (nullable).
	 * @param isLocked              The lock status (nullable).
	 * @param newStatus             The updated task status (nullable).
	 * @param newExpectedCompletionDate New expected completion date (nullable).
	 * @param newDueDate            The new due date (nullable).
	 * @return The updated Task entity.
	 */
	public TaskDTO editTask(int taskId, TaskDTO taskDTO) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));
				
		String oldTitle = task.getTitle();
		String oldDescription = task.getDescription();
		boolean oldLockStatus = task.isLocked();
		String oldStatus = task.getStatus();
		LocalDate oldDueDate = task.getDueDate();

		// if (taskDTO.getTitle() == oldTitle &&
		//     taskDTO.getDescription() == oldDescription &&
		// 	taskDTO.getIsLocked() == oldLockStatus && 
		// 	taskDTO.getDueDate() == oldDueDate &&
		// 	taskDTO.getStatus() == oldStatus){
		// 		//nothing has been changed
		// }
		if ((taskDTO.getTitle() != null && !taskDTO.getTitle().isEmpty()) &&
			(!taskDTO.getTitle().equals(oldTitle))) {
			task.setTitle(taskDTO.getTitle());

			//call notif method
			notifService.notifyTaskTitleChange(task, oldTitle);
		}

		if (taskDTO.getDescription() != null && !taskDTO.getDescription().isEmpty()&&
			(!taskDTO.getDescription().equals(oldDescription))) {
			task.setDescription(taskDTO.getDescription());

			//call notif method
			notifService.notifyTaskDescriptionChange(task, oldDescription);
		}
		
		if (taskDTO.getIsLocked() != task.isLocked()) {
			task.setIsLocked(taskDTO.getIsLocked());

			//call notif method
			notifService.notifyTaskLockChange(task, oldLockStatus);
		}
		
		if (taskDTO.getStatus() != null && !taskDTO.getStatus().isEmpty()) {
			task.setStatus(taskDTO.getStatus());

			//call notif method
			notifService.notifyTaskStatusChange(task, oldStatus);
		}

		if (taskDTO.getDueDate() != null && !taskDTO.getDueDate().equals(oldDueDate)) {
			task.setDueDate(taskDTO.getDueDate());

			//call notif method
			notifService.notifyTaskDueDateChange(task, oldDueDate);
		}

		if (taskDTO.getPriority() != null) {
			try {
				task.setPriority(taskDTO.getPriority());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Invalid priority value. Use: LOW, MEDIUM, or HIGH.");
			}
		}

		task = taskRepository.save(task);

		return convertToDTO(task);
	}

	/**
	 * Assigns a TeamMember to a Task.
	 * Ensures that a member cannot be assigned to the same task more than once.
	 *
	 * @param taskId       The ID of the task.
	 * @param teamMemberId The ID of the team member to be assigned.
	 */
    public IsAssignedDTO assignToTask(int taskId, int teamMemberId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

        // Ensure that the member is not already assigned to this task
        boolean alreadyAssigned = isAssignedRepository.existsByTeamMember_AccountIdAndTask_TaskId(teamMemberId, taskId);
        if (alreadyAssigned) {
            throw new RuntimeException("Team Member is already assigned to this task.");
        }

        IsAssigned isAssigned = new IsAssigned(task, teamMember, task.getTeam());
        isAssigned = isAssignedRepository.save(isAssigned);

        return convertToDTO(isAssigned);
    }
    
    public List<IsAssignedDTO> massAssignToTask(int taskId, List<Integer> teamMemberIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        List<IsAssigned> newAssignments = new ArrayList<>();

        for (Integer teamMemberId : teamMemberIds) {
            TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                    .orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

            boolean alreadyAssigned = isAssignedRepository.existsByTeamMember_AccountIdAndTask_TaskId(teamMemberId,
                    taskId);

            if (!alreadyAssigned) {
                IsAssigned isAssigned = new IsAssigned(task, teamMember, task.getTeam());
                newAssignments.add(isAssigned);
            }
        }
        
        List<IsAssigned> savedAssignments = isAssignedRepository.saveAll(newAssignments);

        return savedAssignments.stream()    
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

	/**
	 * Changes the password for a TeamMember.
	 * Currently, this method is a placeholder for future implementation.
	 *
	 * @param teamMemberId The ID of the team member.
	 * @param oldPassword  The current password (not yet implemented).
	 * @param newPassword  The new password to set (not yet implemented).
	 */
	public void changePassword(int teamMemberId, String oldPassword, String newPassword) {
		if (newPassword == null || newPassword.isEmpty()) {
			throw new RuntimeException("Cannot change password to null or empty string");
		}
		TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
				.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

		boolean isOldPasswordVerified = authInfoService.approveLogin(teamMember.getAccountId(), oldPassword);
		if (isOldPasswordVerified) {
			String salt = teamMember.getAuthInfo().getSalt();
			String newHashedPassword = AuthInfoService.hashPassword(newPassword, salt);
			teamMember.getAuthInfo().setHashedPassword(newHashedPassword);
		}else{
			throw new RuntimeException("password is incorrect" + oldPassword);
		}
	}
	
	public void resetPassword(int teamMemberId, String newPassword) {
		//check if password is valid
		if (newPassword == null || newPassword.isEmpty()) {
			throw new RuntimeException("Cannot change password to null or empty string");
		}

		//ensure the team member exists
		TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
				.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

		//create new salt and new password
		String newSalt = AuthInfoService.generateSalt();
		String newHashedPassword = AuthInfoService.hashPassword(newPassword, newSalt);

		//set the new salt and hashed password
		teamMember.getAuthInfo().setSalt(newSalt);
		teamMember.getAuthInfo().setHashedPassword(newHashedPassword);

		teamMemberRepository.save(teamMember);
	}

	public TeamMemberDTO getTeamMember(int accountId) {
		TeamMember teamMember = teamMemberRepository.findById(accountId)
			.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + accountId));
		
		return convertToDTO(teamMember);
	}

	public List<TaskDTO> getAssignedTasks(int teamMemberId) {
		return isAssignedRepository.findByTeamMember_AccountId(teamMemberId).stream()
            .map(isAssigned -> {
                Task task = isAssigned.getTask();

				List<TeamMemberDTO> assignedMembers = (task.getAssignedMembers() != null ? task.getAssignedMembers() : new ArrayList<>())
					.stream()
					.map(assignment -> convertToDTO(((IsAssigned) assignment).getTeamMember()))
					.collect(Collectors.toList());
				
                TaskDTO taskDTO = convertToDTO(task);
				taskDTO.setAssignedMembers(assignedMembers);
				
                return taskDTO;
            })
            .collect(Collectors.toList());
	}

	public List<TeamDTO> getTeamsForMember(int teamMemberId) {
		TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
				.orElseThrow(() -> new RuntimeException("Team Member not found with ID: " + teamMemberId));

		return teamMember.getTeams().stream()
                .map(isMemberOf -> {
                    Team team = isMemberOf.getTeam();
                    TeamMember lead = team.getTeamLead();
                    int leadId = (lead != null) ? lead.getAccountId() : -1; // 👈 sentinel for no lead
                    return new TeamDTO(team.getTeamId(), team.getTeamName(), leadId);
                })
				.collect(Collectors.toList());
	}
			
	/*
	 * Converts TeamMember entity to a TeamMemberDTO
	 */
	private TeamMemberDTO convertToDTO(TeamMember teamMember) {
		return new TeamMemberDTO(
			teamMember.getAccountId(), 
			teamMember.getUserName(), 
			teamMember.getUserEmail(),
			teamMember.getRole()
		);
	}

	/**
	 * Converts a Task entity to a TaskDTO.
	 */
	private TaskDTO convertToDTO(Task task) {
		List<TeamMemberDTO> assignedMembers = task.getAssignedMembers()
			.stream()
        	.map(assignment -> convertToDTO(assignment.getTeamMember()))
        	.collect(Collectors.toList());

		return new TaskDTO(
			task.getTaskId(),
			task.getTitle(),
			task.getDescription(),
			task.isLocked(),
			task.getStatus(),
			task.getDateCreated(),
			task.getDueDate(),
			task.getTeam().getTeamId(),
			assignedMembers,
			task.getPriority() != null ? task.getPriority() : TaskPriority.LOW
		);
	}

	/**
	 * Converts an IsAssigned entity to an IsAssignedDTO.
	 */
	private IsAssignedDTO convertToDTO(IsAssigned isAssigned) {
		return new IsAssignedDTO(
			isAssigned.getId(),
			isAssigned.getTask().getTaskId(),
			isAssigned.getTeamMember().getAccountId(),
			isAssigned.getTeam().getTeamId()
		);
	}
}
