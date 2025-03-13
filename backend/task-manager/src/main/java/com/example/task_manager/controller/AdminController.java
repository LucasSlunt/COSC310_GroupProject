package com.example.task_manager.controller;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.AdminRequestDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.DTO.UpdateEmailRequestDTO;
import com.example.task_manager.DTO.UpdateNameRequestDTO;
import java.util.List;
import com.example.task_manager.repository.AdminRepository;
import com.example.task_manager.repository.AuthInfoRepository;
import com.example.task_manager.service.AdminService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;

@RestController
@RequestMapping("/api/admin")
//This is an admin controller
public class AdminController {

    private final AuthInfoRepository authInfoRepository;

    private final AuthController authController;

    private final AdminRepository adminRepository;

    private final AdminService adminService;

    public AdminController(AdminService adminService, AdminRepository adminRepository, AuthController authController, AuthInfoRepository authInfoRepository) {
        this.adminService = adminService;
        this.adminRepository = adminRepository;
        this.authController = authController;
        this.authInfoRepository = authInfoRepository;
    }

    // Create Admin entity
    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestBody AdminRequestDTO request) {
        try {
            AdminDTO createAdmin = adminService.createAdmin(
                request.getName(),
                request.getEmail(),
                request.getPassword()
            );
            return ResponseEntity.ok(createAdmin);
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
    public ResponseEntity<?> updateAdminName(@PathVariable int adminId, @RequestBody UpdateNameRequestDTO request) {
        try {
            AdminDTO updatedAdmin = adminService.modifyAdminName(adminId, request.getNewName());
            return ResponseEntity.ok(updatedAdmin);
        } 
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Admin Email
    @PutMapping("/{adminId}/update-email")
    public ResponseEntity<?> updateAdminEmail(@PathVariable int adminId, @RequestBody UpdateEmailRequestDTO request) {
        try {
            AdminDTO updatedAdmin = adminService.modifyAdminEmail(adminId, request.getNewEmail());
            return ResponseEntity.ok(updatedAdmin);
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
    public ResponseEntity<?> createTeamMember(@RequestBody AdminRequestDTO request) {
        try {
            TeamMemberDTO createTeamMember = adminService.createTeamMember(
                request.getName(),
                request.getEmail(),
                request.getPassword()
            );
            return ResponseEntity.ok(createTeamMember);
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Team Member Name
    @PutMapping("/team-member/{teamMemberId}/update-name")
    public ResponseEntity<?> modifyTeamMemberName(@PathVariable int teamMemberId, @RequestBody UpdateNameRequestDTO request) {
        try {
            return ResponseEntity.ok(adminService.modifyTeamMemberName(teamMemberId, request.getNewName()));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Team Member Email
    @PutMapping("/team-member/{teamMemberId}/update-email")
    public ResponseEntity<?> modifyTeamMemberEmail(@PathVariable int teamMemberId, @RequestBody UpdateEmailRequestDTO request) {
        try {
            return ResponseEntity.ok(adminService.modifyTeamMemberEmail(teamMemberId, request.getNewEmail()));
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
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    //get all admins
    @GetMapping("/admins")
    public ResponseEntity<?> getAdmins() {
        try {
            List<AdminDTO> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    //get all team members
    @GetMapping("/team-members")
    public ResponseEntity<?> getTeamMembers() {
        try {
            List<TeamMemberDTO> teamMembers = adminService.getAllTeamMembers();
            return ResponseEntity.ok(teamMembers);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/all-teams")
    public ResponseEntity<?> getAllTeams() {
        try {
            List<TeamDTO> teams = adminService.getAllTeams();
            return ResponseEntity.ok(teams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminById(@PathVariable int adminId) {
        try {
            AdminDTO admin = adminService.getAdminById(adminId);
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{teamMemberId}")
    public ResponseEntity<?> getTeamMemberById(@PathVariable int teamMemberId) {
        try {
            TeamMemberDTO teamMember = adminService.getTeamMemberById(teamMemberId);
            return ResponseEntity.ok(teamMember);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
