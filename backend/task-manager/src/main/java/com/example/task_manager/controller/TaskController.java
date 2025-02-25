package com.example.task_manager.controller;

import com.example.task_manager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Notify Members of Task
    @PostMapping("/{taskId}/notify")
    public ResponseEntity<?> notifyMembers(@PathVariable int taskId, @RequestParam String message) {
        try {
            taskService.notifyMembers(taskId, message);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
