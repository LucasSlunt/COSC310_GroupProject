package com.example.task_manager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.IsMemberOf;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.IsMemberOfRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.sun.source.util.TaskListener;

import jakarta.transaction.Transactional;

@Service // Marks this class as a Spring service, allowing it to be injected where needed
@Transactional
public class TeamService {

    private final TaskRepository taskRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final IsMemberOfRepository isMemberOfRepository;

    // Constructor injection for required repositories
    public TeamService(TaskRepository taskRepository, TeamMemberRepository teamMemberRepository,
            TeamRepository teamRepository,
            IsMemberOfRepository isMemberOfRepository) {
        this.taskRepository = taskRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.isMemberOfRepository = isMemberOfRepository;
    }

    /**
     * Creates a new team with the specified team name and team lead.
     *
     * @param teamName The name of the team.
     * @param teamLeadId The ID of the team lead.
     * @return The created Team entity.
     */
    public TeamDTO createTeam(String teamName, int teamLeadId) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new RuntimeException("Team name cannot be empty");
        }

        TeamMember teamLead = teamMemberRepository.findById(teamLeadId)
                .orElseThrow(() -> new RuntimeException("Team Lead not found with ID: " + teamLeadId));

        Team team = new Team();
        team.setTeamName(teamName);
        team.setTeamLead(teamLead);

        team = teamRepository.save(team);
        
        IsMemberOf isMemberOf = new IsMemberOf(teamLead, team);
        isMemberOfRepository.save(isMemberOf);

		return convertToDTO(team);
	}

    /**
     * Deletes a team by its ID.
     *
     * @param teamId The ID of the team to delete.
     */
    public void deleteTeam(int teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));

        teamRepository.delete(team);
    }

    /**
     * Changes the team lead of an existing team.
     *
     * @param teamId The ID of the team whose lead should be changed.
     * @param teamLeadId The ID of the new team lead.
     */
    public TeamDTO changeTeamLead(int teamId, String teamName, int teamLeadId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));

        TeamMember teamMember = teamMemberRepository.findById(teamLeadId)
                .orElseThrow(() -> new RuntimeException("Team Lead not found with ID: " + teamLeadId));

        team.setTeamLead(teamMember);
        team.setTeamName(teamName);
        teamRepository.save(team);

        return new TeamDTO(team.getTeamId(), team.getTeamName(), team.getTeamLead().getAccountId());
    }

    /**
     * Retrieves all members of a specified team.
     *
     * @param teamId The ID of the team.
     * @return A list of TeamMembers belonging to the team.
     */
    public List<TeamMemberDTO> getTeamMembers(int teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));
        return isMemberOfRepository.findMembersByTeamId(teamId).stream()
                .map(IsMemberOf::getTeamMember)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*
	 * Returns all tasks attached to a team
     */
    public List<TaskDTO> getTeamTasks(int teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with ID: " + teamId));

        return taskRepository.findByTeam_TeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /*
	 * Converts a Task entity to a TaskDTO entity
     */
    private TaskDTO convertToDTO(Task task) {
        List<TeamMemberDTO> members = task.getAssignedMembers().stream()
                .map(IsAssigned::getTeamMember)
                .map(member -> new TeamMemberDTO(
                member.getAccountId(),
                member.getUserName(),
                member.getUserEmail(),
                member.getRole()))
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
                members,
                task.getPriority()
        );
    }

    /**
     * Converts a Team entity to a TeamDTO.
     */
    private TeamDTO convertToDTO(Team team) {
        return new TeamDTO(
                team.getTeamId(),
                team.getTeamName(),
                team.getTeamLead() != null ? team.getTeamLead().getAccountId() : null
        );
    }

    /**
     * Converts a TeamMember entity to a TeamMemberDTO.
     */
    private TeamMemberDTO convertToDTO(TeamMember teamMember) {
        return new TeamMemberDTO(teamMember.getAccountId(), teamMember.getUserName(), teamMember.getUserEmail(), teamMember.getRole());
    }
}
