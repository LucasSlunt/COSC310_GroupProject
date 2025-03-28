package com.example.task_manager.controller;

import com.example.task_manager.DTO.AuthInfoDTO;
import com.example.task_manager.enums.RoleType;
import com.example.task_manager.service.AuthInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthInfoService authInfoService;

    public AuthController(AuthInfoService authInfoService) {
        this.authInfoService = authInfoService;
    }

    /*
     * Endpoint for authenticating a user.
     * Expects a JSON request body with `teamMemberId` and `password`.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthInfoDTO> login(@RequestBody AuthInfoDTO loginRequest) {
        try {
            AuthInfoDTO authInfo = authInfoService.authenticateUser(
                loginRequest.getAccountId(),
                loginRequest.getPassword()
            );

            return ResponseEntity.ok(authInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(null); // 401 Unauthorized
        }
    }

    /*
     * Endpoint for figuring out is a user is an admin
     * Takes `teamMemberId` as a path variable
     */
    @GetMapping("/{teamMemberId}/is-admin")
    public ResponseEntity<?> isAdmin(@PathVariable int teamMemberId) {
        try {
            RoleType role = authInfoService.isAdmin(teamMemberId);
            return ResponseEntity.ok(role);
        }
        catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(null);
            }
            return ResponseEntity.status(500).body(null);
        }
    }
}