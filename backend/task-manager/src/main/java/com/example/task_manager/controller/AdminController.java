package com.example.task_manager.controller;

import com.example.task_manager.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Create Admin entity
    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestParam String name, @RequestParam String email) {
        try {
            return ResponseEntity.ok(adminService.createAdmin(name, email));
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete Admin
    @DeleteMapping("/{adminId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable int adminId) {
        try {
            adminService.deleteAdmin(adminId);
            return ResponseEntity.noContent().build();
        } 
        catch (Exception e) {
            return ResponseEntity.status(404).body("Admin not found");
        }
    }

    // Modify Admin Name
    @PutMapping("/{adminId}/update-name")
    public ResponseEntity<?> updateAdminName(@PathVariable int adminId, @RequestParam String newName) {
        try {
            return ResponseEntity.ok(adminService.modifyAdminName(adminId, newName));
        } 
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Admin Email
    @PutMapping("/{adminId}/update-email")
    public ResponseEntity<?> updateAdminEmail(@PathVariable int adminId, @RequestParam String newEmail) {
        try {
            return ResponseEntity.ok(adminService.modifyAdminEmail(adminId, newEmail));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Admin not found");
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Create Team Member
    @PostMapping("/team-member")
    public ResponseEntity<?> createTeamMember(@RequestParam String name, @RequestParam String email) {
        try {
            return ResponseEntity.ok(adminService.createTeamMember(name, email));
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Team Member Name
    @PutMapping("/team-member/{teamMemberId}/update-name")
    public ResponseEntity<?> modifyTeamMemberName(@PathVariable int teamMemberId, @RequestParam String newName) {
        try {
            return ResponseEntity.ok(adminService.modifyTeamMemberName(teamMemberId, newName));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Team Member Email
    @PutMapping("/team-member/{teamMemberId}/update-email")
    public ResponseEntity<?> modifyTeamMemberEmail(@PathVariable int teamMemberId, @RequestParam String newEmail) {
        try {
            return ResponseEntity.ok(adminService.modifyTeamMemberEmail(teamMemberId, newEmail));
        } 
        catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Team member not found");
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete Team Member
    @DeleteMapping("/team-member/{teamMemberId}")
    public ResponseEntity<?> deleteTeamMember(@PathVariable int teamMemberId) {
        try {
            adminService.deleteTeamMember(teamMemberId);
            return ResponseEntity.noContent().build();
        } 
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Team member not found");
        }
    }

    // Assign Team Member to Team
    @PostMapping("/team-member/{teamMemberId}/assign-to-team/{teamId}")
    public ResponseEntity<?> assignToTeam(@PathVariable int teamMemberId, @PathVariable int teamId) {
        try {
            return ResponseEntity.ok(adminService.assignToTeam(teamMemberId, teamId));
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Promote Team Member to Admin
    @PostMapping("/team-member/{teamMemberId}/promote")
    public ResponseEntity<?> promoteToAdmin(@PathVariable int teamMemberId) {
        try {
            return ResponseEntity.ok(adminService.promoteToAdmin(teamMemberId));
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
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Unlock Task
    @PutMapping("/tasks/{taskId}/unlock")
    public ResponseEntity<?> unlockTask(@PathVariable int taskId) {
        try {
            adminService.unlockTask(taskId);
            return ResponseEntity.ok().build();
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
