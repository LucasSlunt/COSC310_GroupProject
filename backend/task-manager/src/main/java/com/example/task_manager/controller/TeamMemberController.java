package com.example.task_manager.controller;

import com.example.task_manager.DTO.IsAssignedDTO;
import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.service.TeamMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    public TeamMemberController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    // Create a Task
    @PostMapping
    public ResponseEntity<?> createTask(@RequestParam String title,
                                        @RequestParam String description,
                                        @RequestParam Boolean isLocked,
                                        @RequestParam String status,
                                        @RequestParam int teamId) {
        try {
            TaskDTO task = teamMemberService.createTask(title, description, isLocked, status, LocalDate.now(), LocalDate.now(), null, null);
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
            TaskDTO task = teamMemberService.editTask(
                taskId,
                taskDTO.getTitle(),
                taskDTO.getDescription(),
                taskDTO.isLocked(),
                taskDTO.getStatus(),
                null,
                taskDTO.getDueDate()
            );
            return ResponseEntity.ok(task);
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
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        try {
            teamMemberService.changePassword(teamMemberId, oldPassword, newPassword);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
