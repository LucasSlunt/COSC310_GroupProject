package com.example.task_manager.DTO_tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.task_manager.DTO.ResetPasswordRequestDTO;

public class ResetPasswordRequestDTOTest {
    @Test
    void testDTOConstructor() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(123, "securePassword");

        assertEquals(123, requestDTO.getTeamMemberId());
        assertEquals("securePassword", requestDTO.getNewPassword());
    }

    @Test
    void testDTOGetters() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(456, "strongPass");

        assertEquals(456, requestDTO.getTeamMemberId());
        assertEquals("strongPass", requestDTO.getNewPassword());
    }

    @Test
    void testDTOSetters() {
        ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO();

        requestDTO.setTeamMemberId(789);
        requestDTO.setNewPassword("updatedPass123");

        assertEquals(789, requestDTO.getTeamMemberId());
        assertEquals("updatedPass123", requestDTO.getNewPassword());
    }
}
