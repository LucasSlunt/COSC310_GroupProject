package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.AuthInfoDTO;
import com.example.task_manager.controller.AuthController;
import com.example.task_manager.enums.RoleType;
import com.example.task_manager.service.AuthInfoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
public class AuthInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthInfoService authInfoService;

    /**
     * Test Successful Login
     */
    @Test
    void testLogin_Success() throws Exception {
        AuthInfoDTO mockUser = new AuthInfoDTO(1, "User_" + System.nanoTime(), RoleType.ADMIN);

        when(authInfoService.authenticateUser(1, "correctpassword")).thenReturn(mockUser);

        mockMvc.perform(post("/auth-info/login")
                .contentType("application/json")
                .content("{\"accountId\":1, \"password\":\"correctpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.userName").value(mockUser.getUserName()))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(authInfoService, times(1)).authenticateUser(1, "correctpassword");
    }

    /**
     * Test Login as Admin
     */
    @Test
    void testLogin_AdminSuccess() throws Exception {
        AuthInfoDTO mockAdmin = new AuthInfoDTO(2, "Admin_" + System.nanoTime(), RoleType.ADMIN);

        when(authInfoService.authenticateUser(2, "adminpassword")).thenReturn(mockAdmin);

        mockMvc.perform(post("/auth-info/login")
                .contentType("application/json")
                .content("{\"accountId\":2, \"password\":\"adminpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.userName").value(mockAdmin.getUserName()))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(authInfoService, times(1)).authenticateUser(2, "adminpassword");
    }

    /**
     * Test Login with Incorrect Password
     */
    @Test
    void testLogin_Failure_InvalidCredentials() throws Exception {
        when(authInfoService.authenticateUser(1, "wrongpassword"))
                .thenThrow(new RuntimeException("Invalid Credentials"));

        mockMvc.perform(post("/auth-info/login")
                .contentType("application/json")
                .content("{\"accountId\":1, \"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verify(authInfoService, times(1)).authenticateUser(1, "wrongpassword");
    }

    /**
     * Test Login with Nonexistent User
     */
    @Test
    void testLogin_Failure_NonExistentUser() throws Exception {
        when(authInfoService.authenticateUser(9999, "somepassword"))
                .thenThrow(new RuntimeException("Team Member not found"));

        mockMvc.perform(post("/auth-info/login")
                .contentType("application/json")
                .content("{\"accountId\":9999, \"password\":\"somepassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));

        verify(authInfoService, times(1)).authenticateUser(9999, "somepassword");
    }

    /**
     * Test `isAdmin` when the user is an admin
     */
    @Test
    void testIsAdmin_Success_AdminUser() throws Exception {
        when(authInfoService.isAdmin(2)).thenReturn(RoleType.ADMIN);

        mockMvc.perform(get("/auth-info/2/is-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("ADMIN"));

        verify(authInfoService, times(1)).isAdmin(2);
    }

    /**
     * Test `isAdmin` when the user is NOT an admin
     */
    @Test
    void testIsAdmin_Success_NonAdminUser() throws Exception {
        when(authInfoService.isAdmin(1)).thenReturn(RoleType.ADMIN);

        mockMvc.perform(get("/auth-info/1/is-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("ADMIN"));

        verify(authInfoService, times(1)).isAdmin(1);
    }

    /**
     * Test `isAdmin` when the user does NOT exist
     */
    @Test
    void testIsAdmin_Failure_UserNotFound() throws Exception {
        when(authInfoService.isAdmin(9999))
                .thenThrow(new RuntimeException("Team Member not found"));

        mockMvc.perform(get("/auth-info/9999/is-admin"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(authInfoService, times(1)).isAdmin(9999);
    }
}
