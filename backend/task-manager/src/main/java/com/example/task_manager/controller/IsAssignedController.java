package com.example.task_manager.controller;

import com.example.task_manager.service.IsAssignedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/assignments")
public class IsAssignedController {

    private final IsAssignedService isAssignedService;

    public IsAssignedController(IsAssignedService isAssignedService) {
        this.isAssignedService = isAssignedService;
    }

    // Assign Team Member to a Task
    @PostMapping("/{teamMemberId}/task/{taskId}")
    public ResponseEntity<?> assignToTask(@PathVariable int teamMemberId, @PathVariable int taskId) {
        try {
            return ResponseEntity.ok(isAssignedService.assignToTask(teamMemberId, taskId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Unassign Team Member from Task
    @DeleteMapping("/{teamMemberId}/task/{taskId}")
    public ResponseEntity<?> unassignFromTask(@PathVariable int teamMemberId, @PathVariable int taskId) {
        try {
            return ResponseEntity.ok(isAssignedService.unassignFromTask(teamMemberId, taskId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Check if Assigned
    @GetMapping("/{teamMemberId}/task/{taskId}")
    public ResponseEntity<?> isAssignedToTask(@PathVariable int teamMemberId, @PathVariable int taskId) {
        return ResponseEntity.ok(isAssignedService.isAssignedToTask(teamMemberId, taskId));
    }
}
