package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.AdminDTO;
import com.example.task_manager.DTO.AdminRequestDTO;
import com.example.task_manager.enums.RoleType;
import com.example.task_manager.service.AdminService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.task_manager.DTO.UpdateEmailRequestDTO;
import com.example.task_manager.DTO.UpdateNameRequestDTO;
import com.example.task_manager.controller.AdminAccountController;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminAccountController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminAccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Create an Admin
     */
    @Test
    void testCreateAdmin() throws Exception {
        AdminDTO mockAdmin = new AdminDTO(1, "Admin_" + System.nanoTime(), "admin_" + System.nanoTime() + "@example.com", RoleType.ADMIN);
        AdminRequestDTO requestDTO = new AdminRequestDTO(mockAdmin.getUserName(), mockAdmin.getUserEmail(), "securePass");

        when(adminService.createAdmin(mockAdmin.getUserName(), mockAdmin.getUserEmail(), "securePass")).thenReturn(mockAdmin);

        mockMvc.perform(post("/api/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value(mockAdmin.getUserName()))
                .andExpect(jsonPath("$.userEmail").value(mockAdmin.getUserEmail()));
    }

    /**
     * Delete Admin
     */
    @Test
    void testDeleteAdmin() throws Exception {
        doNothing().when(adminService).deleteAdmin(1);

        mockMvc.perform(delete("/api/admins/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Modify Admin Name
     */
    @Test
    void testModifyAdminName() throws Exception {
        AdminDTO updatedAdmin = new AdminDTO(1, "NewAdmin_" + System.nanoTime(), "admin@example.com", RoleType.ADMIN);
        UpdateNameRequestDTO requestDTO = new UpdateNameRequestDTO(updatedAdmin.getUserName());

        when(adminService.modifyAdminName(1, updatedAdmin.getUserName())).thenReturn(updatedAdmin);

        mockMvc.perform(put("/api/admins/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(updatedAdmin.getUserName()));
    }

    /**
     * Modify Admin Email
     */
    @Test
    void testModifyAdminEmail() throws Exception {
        AdminDTO updatedAdmin = new AdminDTO(1, "Admin User", "updated_" + System.nanoTime() + "@example.com",
                RoleType.ADMIN);
        UpdateEmailRequestDTO requestDTO = new UpdateEmailRequestDTO(updatedAdmin.getUserEmail());

        when(adminService.modifyAdminEmail(1, updatedAdmin.getUserEmail())).thenReturn(updatedAdmin);

        mockMvc.perform(put("/api/admins/1/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value(updatedAdmin.getUserEmail()));
    }
    
    @Test
    void testGetAdminById() throws Exception {
        AdminDTO mockAdmin = new AdminDTO(1, "Admin Sandler", "adam_sandler@example.com", RoleType.ADMIN);

        when(adminService.getAdminById(1)).thenReturn(mockAdmin);

        mockMvc.perform(get("/api/admins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value("Admin Sandler"))
                .andExpect(jsonPath("$.userEmail").value("adam_sandler@example.com"));
    }

    @Test
    void testGetAdminById_NotFound() throws Exception {
        when(adminService.getAdminById(999)).thenThrow(new RuntimeException("Admin not found"));

        mockMvc.perform(get("/api/admins/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Admin not found"));
    }
}
