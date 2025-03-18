package com.example.task_manager.DTO_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.example.task_manager.DTO.PasswordChangeRequestDTO;

public class ChangePasswordRequestDTOTest {
    @Test
    void testDTOConstructor() {
        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO(123, "oldPass123", "newPass456");

        assertEquals(123, requestDTO.getTeamMemberId());
        assertEquals("oldPass123", requestDTO.getOldPassword());
        assertEquals("newPass456", requestDTO.getNewPassword());
    }

    @Test
    void testDTOGetters() {
        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO(456, "previousPass", "securePass");

        assertEquals(456, requestDTO.getTeamMemberId());
        assertEquals("previousPass", requestDTO.getOldPassword());
        assertEquals("securePass", requestDTO.getNewPassword());
    }

    @Test
    void testDTOSetters() {
        PasswordChangeRequestDTO requestDTO = new PasswordChangeRequestDTO();

        requestDTO.setTeamMemberId(789);
        requestDTO.setOldPassword("pastPassword");
        requestDTO.setNewPassword("updatedPassword123");

        assertEquals(789, requestDTO.getTeamMemberId());
        assertEquals("pastPassword", requestDTO.getOldPassword());
        assertEquals("updatedPassword123", requestDTO.getNewPassword());
    }
}
