package com.example.task_manager.controller;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.AdminRequestDTO;
import com.example.task_manager.DTO.UpdateEmailRequestDTO;
import com.example.task_manager.DTO.UpdateNameRequestDTO;
import com.example.task_manager.service.AdminService;

@RestController
@RequestMapping("/api/admins")
public class AdminAccountController {
    private final AdminService adminService;

    public AdminAccountController(AdminService adminService) {
        this.adminService = adminService;
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
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete Admin
    @DeleteMapping("/{adminId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable int adminId) {
        try {
            adminService.deleteAdmin(adminId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Admin not found");
        }
    }

    // Modify Admin Name
    @PutMapping("/{adminId}/name")
    public ResponseEntity<?> updateAdminName(@PathVariable int adminId, @RequestBody UpdateNameRequestDTO request) {
        try {
            AdminDTO updatedAdmin = adminService.modifyAdminName(adminId, request.getNewName());
            return ResponseEntity.ok(updatedAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modify Admin Email
    @PutMapping("/{adminId}/email")
    public ResponseEntity<?> updateAdminEmail(@PathVariable int adminId, @RequestBody UpdateEmailRequestDTO request) {
        try {
            AdminDTO updatedAdmin = adminService.modifyAdminEmail(adminId, request.getNewEmail());
            return ResponseEntity.ok(updatedAdmin);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Admin not found");
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
}
