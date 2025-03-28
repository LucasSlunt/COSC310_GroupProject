package com.example.task_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.task_manager.DTO.TaskDTO;
import com.example.task_manager.DTO.TaskRequestDTO;
import com.example.task_manager.service.TeamMemberService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final TeamMemberService teamMemberService;

    public TaskController(TeamMemberService teamMemberService) {
        this.teamMemberService = teamMemberService;
    }

    // Create a Task
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequestDTO request) {
        try {
            TaskDTO task = teamMemberService.createTask(request);
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
            TaskDTO updatedTask = teamMemberService.editTask(taskId, taskDTO);
            return ResponseEntity.ok(updatedTask);
        } 
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
