package com.example.task_manager.DTO_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.example.task_manager.DTO.TaskDTO;

public class TaskDTOTest {

    @Test
    void testDTOConstructorAndGetters() {
        LocalDate dueDate = LocalDate.of(2024, 5, 10);
        TaskDTO dto = new TaskDTO(1, "Task Title", "Task Description", false, "Open", dueDate, 201);

        assertEquals(1, dto.getTaskId());
        assertEquals("Task Title", dto.getTitle());
        assertEquals("Task Description", dto.getDescription());
        assertFalse(dto.isLocked());
        assertEquals("Open", dto.getStatus());
        assertEquals(dueDate, dto.getDueDate());
        assertEquals(201, dto.getTeamId());
    }

    @Test
    void testDTOSetters() {
        TaskDTO dto = new TaskDTO();
        LocalDate newDueDate = LocalDate.of(2024, 6, 15);

        dto.setTaskId(2);
        dto.setTitle("Updated Task Title");
        dto.setDescription("Updated Description");
        dto.setLocked(true);
        dto.setStatus("In Progress");
        dto.setDueDate(newDueDate);
        dto.setTeamId(202);

        assertEquals(2, dto.getTaskId());
        assertEquals("Updated Task Title", dto.getTitle());
        assertEquals("Updated Description", dto.getDescription());
        assertTrue(dto.isLocked());
        assertEquals("In Progress", dto.getStatus());
        assertEquals(newDueDate, dto.getDueDate());
        assertEquals(202, dto.getTeamId());
    }
}
