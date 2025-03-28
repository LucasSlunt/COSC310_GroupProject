package com.example.task_manager.controller;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.task_manager.DTO.AdminRequestDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.DTO.UpdateEmailRequestDTO;
import com.example.task_manager.DTO.UpdateNameRequestDTO;
import com.example.task_manager.service.AdminService;

@RestController
@RequestMapping("/api/members")
public class TeamMemberAccountController {
    private final AdminService adminService;

    public TeamMemberAccountController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Create Team Member
    @PostMapping
    public ResponseEntity<?> createTeamMember(@RequestBody AdminRequestDTO request) {
        try {
            TeamMemberDTO createTeamMember = adminService.createTeamMember(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword()
            );
            return ResponseEntity.ok(createTeamMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete Team Member
    @DeleteMapping("/{teamMemberId}")
    public ResponseEntity<?> deleteTeamMember(@PathVariable int teamMemberId) {
        try {
            adminService.deleteTeamMember(teamMemberId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Team member not found");
        }
    }

    // Modify Team Member Name
    @PutMapping("/{teamMemberId}/name")
    public ResponseEntity<?> modifyTeamMemberName(@PathVariable int teamMemberId, @RequestBody UpdateNameRequestDTO request) {
        try {
            return ResponseEntity.ok(adminService.modifyTeamMemberName(teamMemberId, request.getNewName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Team Member Email
    @PutMapping("/{teamMemberId}/email")
    public ResponseEntity<?> modifyTeamMemberEmail(@PathVariable int teamMemberId, @RequestBody UpdateEmailRequestDTO request) {
        try {
            return ResponseEntity.ok(adminService.modifyTeamMemberEmail(teamMemberId, request.getNewEmail()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Team member not found");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get a team member's info with their ID
    @GetMapping("/team-member/{teamMemberId}")
    public ResponseEntity<?> getTeamMemberById(@PathVariable int teamMemberId) {
        try {
            TeamMemberDTO teamMember = adminService.getTeamMemberById(teamMemberId);
            return ResponseEntity.ok(teamMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
