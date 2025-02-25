package com.example.task_manager.controller;

import com.example.task_manager.DTO.TeamDTO;
import com.example.task_manager.DTO.TeamMemberDTO;
import com.example.task_manager.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // Create a Team
    @PostMapping
    public ResponseEntity<?> createTeam(@RequestParam String teamName, @RequestParam int teamLeadId) {
        try {
            TeamDTO team = teamService.createTeam(teamName, teamLeadId);
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
    public ResponseEntity<?> changeTeamLead(@PathVariable int teamId, @RequestParam int teamLeadId) {
        try {
            teamService.changeTeamLead(teamId, teamLeadId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get Team Members
    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberDTO>> getTeamMembers(@PathVariable int teamId) {
        try {
            return ResponseEntity.ok(teamService.getTeamMembers(teamId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
