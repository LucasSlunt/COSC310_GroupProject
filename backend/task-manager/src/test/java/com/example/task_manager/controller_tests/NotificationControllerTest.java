package com.example.task_manager.controller_tests;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.example.task_manager.DTO.NotificationDTO;
import com.example.task_manager.controller.NotificationController;
import com.example.task_manager.enums.NotificationType;
import com.example.task_manager.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(NotificationController.class)
@ActiveProfiles("test")
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    /**
     * Test Get Unread Notifications
     */
    @Test
    void testGetUnreadNotifications() throws Exception {
        List<NotificationDTO> mockNotifications = Arrays.asList(
                new NotificationDTO(1, "Task Assigned", NotificationType.TASK_ASSIGNED, false, LocalDateTime.now(), 17911, 7650),
                new NotificationDTO(2, "Task Updated", NotificationType.TASK_TITLE_EDITED, false, LocalDateTime.now(), 17911, 7650)
        );

        when(notificationService.getUnreadNotifications(17911)).thenReturn(mockNotifications);

        mockMvc.perform(get("/notif/17911/unread-notifs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Task Assigned"))
                .andExpect(jsonPath("$[1].message").value("Task Updated"));
    }

    /**
     * Test Get Read Notifications
     */
    @Test
    void testGetReadNotifications() throws Exception {
        List<NotificationDTO> mockNotifications = Arrays.asList(
                new NotificationDTO(3, "Task Completed", NotificationType.TASK_STATUS_EDITED, true, LocalDateTime.now(), 17911, 7650)
        );

        when(notificationService.getReadNotifications(17911)).thenReturn(mockNotifications);

        mockMvc.perform(get("/notif/17911/read-notifs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message").value("Task Completed"));
    }

    /**
     * Test Mark Notification as Read
     */
    @Test
    void testMarkAsRead() throws Exception {
        doNothing().when(notificationService).markAsRead(1);

        mockMvc.perform(put("/notif/1/mark-as-read"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification marked as read."));
    }

    /**
     * Test Mark Notification as Unread
     */
    @Test
    void testMarkAsUnread() throws Exception {
        doNothing().when(notificationService).markAsUnread(1);

        mockMvc.perform(put("/notif/1/mark-as-unread"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification marked as unread."));
    }

    /**
     * Test Delete Notification
     */
    @Test
    void testDeleteNotification() throws Exception {
        doNothing().when(notificationService).deleteNotification(1);

        mockMvc.perform(delete("/notif/1"))
                .andExpect(status().isNoContent());
    }
}
