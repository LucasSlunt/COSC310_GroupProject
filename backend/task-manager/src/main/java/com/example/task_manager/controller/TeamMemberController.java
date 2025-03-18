package com.example.task_manager.controller;

import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.DTO.PasswordChangeRequestDTO;
import com.example.task_manager.DTO.ResetPasswordRequestDTO;
import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.service.TeamMemberService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    // Create a Task
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequestDTO request) {
        try {
            TaskDTO task = teamMemberService.createTask(request);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete Task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable int taskId) {
        try {
            teamMemberService.deleteTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Edit Task
    @PutMapping("/{taskId}")
    public ResponseEntity<?> editTask(@PathVariable int taskId,
                                    @RequestBody TaskDTO taskDTO) {
        try {
            TaskDTO updatedTask = teamMemberService.editTask(taskId, taskDTO);
            return ResponseEntity.ok(updatedTask);
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    // Change Password (Placeholder)
    @PostMapping("/team-members/{teamMemberId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable int teamMemberId,
            @RequestBody PasswordChangeRequestDTO request) {
        try {
            teamMemberService.changePassword(teamMemberId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/team-members/{teamMemberId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable int teamMemberId,
            @RequestBody ResetPasswordRequestDTO request) {

        try {
            teamMemberService.resetPassword(teamMemberId, request.getNewPassword());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{teamMemberId}/teams")
    public ResponseEntity<?> getTeamsForMember(@PathVariable int teamMemberId) {
        try {
            List<TeamDTO> teams = teamMemberService.getTeamsForMember(teamMemberId);
            return ResponseEntity.ok(teams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{teamMemberId}/tasks")
    public ResponseEntity<?> getAssignedTasks(@PathVariable int teamMemberId) {
        try {
            List<TaskDTO> tasks = teamMemberService.getAssignedTasks(teamMemberId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }    
}
