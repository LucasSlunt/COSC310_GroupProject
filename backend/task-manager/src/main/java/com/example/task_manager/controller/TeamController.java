package com.example.task_manager.controller;

import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamRequestDTO;
import com.example.task_manager.service.AdminService;
import com.example.task_manager.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final AdminService adminService;

    public TeamController(TeamService teamService, AdminService adminService) {
        this.teamService = teamService;
        this.adminService = adminService;
    }

    // Create a Team
    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamRequestDTO request) {
        try {
            TeamDTO team = teamService.createTeam(request.getTeamName(), request.getTeamLeadId());
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete Team
    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable int teamId) {
        try {
            teamService.deleteTeam(teamId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Change Team Lead
    @PutMapping("/{teamId}/change-lead")
    public ResponseEntity<?> changeTeamLead(@PathVariable int teamId, @RequestBody TeamRequestDTO request) {
        try {
            TeamDTO updatedTeam = teamService.changeTeamLead(teamId, request.getTeamName(), request.getTeamLeadId());
            return ResponseEntity.ok(updatedTeam);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTeams() {
        try {
            List<TeamDTO> teams = adminService.getAllTeams();
            return ResponseEntity.ok(teams);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
