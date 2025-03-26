package com.example.task_manager.controller;

import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.DTO.PasswordChangeRequestDTO;
import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberWithTeamLeadDTO;
import com.example.task_manager.service.AdminService;
import com.example.task_manager.service.TeamMemberService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members/actions")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
    private final AdminService adminService;

    public TeamMemberController(TeamMemberService teamMemberService, AdminService adminService) {
        this.teamMemberService = teamMemberService;
        this.adminService = adminService;
    }

    // Assign Member to Task
    @PostMapping("/{taskId}/assign/{teamMemberId}")
    public ResponseEntity<?> assignToTask(@PathVariable int taskId, @PathVariable int teamMemberId) {
        try {
            IsAssignedDTO assignedDTO = teamMemberService.assignToTask(taskId, teamMemberId);
            return ResponseEntity.ok(assignedDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Assign many members to a task
    @PostMapping("/{taskId}/mass-assign")
    public ResponseEntity<?> massAssignToTask(@PathVariable int taskId, @RequestBody List<Integer> teamMemberIds) {
        try {
            List<IsAssignedDTO> isAssignedDTOs = teamMemberService.massAssignToTask(taskId, teamMemberIds);
            return ResponseEntity.ok(isAssignedDTOs);
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Change Password (Placeholder)
    @PostMapping("/{teamMemberId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable int teamMemberId,
            @RequestBody PasswordChangeRequestDTO request) {
        try {
            teamMemberService.changePassword(teamMemberId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }    
    
    //Get all the teams that a team member is a member of
    @GetMapping("/{teamMemberId}/teams")
    public ResponseEntity<?> getTeamsForMember(@PathVariable int teamMemberId) {
        try {
            List<TeamDTO> teams = teamMemberService.getTeamsForMember(teamMemberId);
            return ResponseEntity.ok(teams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //get all assigned tasks that are assigned to a team member
    @GetMapping("/{teamMemberId}/tasks")
    public ResponseEntity<?> getAssignedTasks(@PathVariable int teamMemberId) {
        try {
            List<TaskDTO> tasks = teamMemberService.getAssignedTasks(teamMemberId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }    

    //get all team members stored in the database
    @GetMapping("/team-members")
    public ResponseEntity<?> getTeamMembers() {
        try {
            List<TeamMemberWithTeamLeadDTO> teamMembers = adminService.getAllTeamMembers();
            return ResponseEntity.ok(teamMembers);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
