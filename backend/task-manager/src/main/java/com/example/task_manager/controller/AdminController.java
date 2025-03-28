package com.example.task_manager.controller;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.ChangeRoleRequestDTO;
import com.example.task_manager.DTO.ResetPasswordRequestDTO;
import com.example.task_manager.service.AdminService;
import com.example.task_manager.service.TeamMemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins/actions")
public class AdminController {

    private final AdminService adminService;
    private final TeamMemberService teamMemberService;

    public AdminController(AdminService adminService, TeamMemberService teamMemberService) {
        this.adminService = adminService;
        this.teamMemberService = teamMemberService;
    }

    // Assign Team Member to Team
    @PostMapping("/team-member/{teamMemberId}/assign-to-team/{teamId}")
    public ResponseEntity<?> assignToTeam(@PathVariable int teamMemberId, @PathVariable int teamId) {
        try {
            return ResponseEntity.ok(adminService.assignToTeam(teamMemberId, teamId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    //change the role (promote/demote) of a team member
    @PostMapping("/{teamMemberId}/role")
    public ResponseEntity<?> changeRole(@PathVariable int teamMemberId, @RequestBody ChangeRoleRequestDTO request) {
        try {
            return ResponseEntity.ok(adminService.changeRole(teamMemberId, request.getRole()));
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lock Task
    @PutMapping("/tasks/{taskId}/lock")
    public ResponseEntity<?> lockTask(@PathVariable int taskId) {
        try {
            adminService.lockTask(taskId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Unlock Task
    @PutMapping("/tasks/{taskId}/unlock")
    public ResponseEntity<?> unlockTask(@PathVariable int taskId) {
        try {
            adminService.unlockTask(taskId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //get all admins
    @GetMapping
    public ResponseEntity<?> getAdmins() {
        try {
            List<AdminDTO> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Reset Password
    @PostMapping("/{teamMemberId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable int teamMemberId,
            @RequestBody ResetPasswordRequestDTO request) {
        try {
            teamMemberService.resetPassword(teamMemberId, request.getNewPassword());
            return ResponseEntity.noContent().build();
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
