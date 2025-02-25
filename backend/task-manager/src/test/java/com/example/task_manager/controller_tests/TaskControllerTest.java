package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import com.example.task_manager.controller.TaskController;
import com.example.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        doNothing().when(taskService).notifyMembers(anyInt(), anyString());
    }

    // Notify the Members (Success Case)
    @Test
    void testNotifyMembers_Success() throws Exception {
        mockMvc.perform(post("/api/tasks/101/notify")
                .param("message", "Reminder: Task deadline is approaching."))
                .andExpect(status().isOk());
        
        verify(taskService, times(1)).notifyMembers(101, "Reminder: Task deadline is approaching.");
    }

    // Notify Members (Task Not Found)
    @Test
    void testNotifyMembers_TaskNotFound() throws Exception {
        doThrow(new RuntimeException("Task not found with ID: 101"))
                .when(taskService).notifyMembers(101, "Reminder: Task deadline is approaching.");

        mockMvc.perform(post("/api/tasks/101/notify")
                .param("message", "Reminder: Task deadline is approaching."))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task not found with ID: 101"));
    }
}
