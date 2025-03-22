package com.example.task_manager.DTO_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.example.task_manager.DTO.ResetPasswordRequestDTO;

public class ResetPasswordRequestDTOTest {
    @Test
    void testConstructorAndSetters() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();

        dto.setTeamMemberId(1);
        dto.setNewPassword("OneLastBreath");

        assertEquals(1, dto.getTeamMemberId());
        assertEquals("OneLastBreath", dto.getNewPassword());
    }

    @Test
    void testAllArgsConstructor() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO(1, "Creed");

        assertEquals(1, dto.getTeamMemberId());
        assertEquals("Creed", dto.getNewPassword());
    }
}
