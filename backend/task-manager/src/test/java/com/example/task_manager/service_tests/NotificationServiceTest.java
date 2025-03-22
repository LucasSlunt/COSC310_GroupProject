package com.example.task_manager.service_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.task_manager.DTO.NotificationDTO;
import com.example.task_manager.entity.IsAssigned;
import com.example.task_manager.entity.Notification;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.enums.NotificationType;
import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.repository.IsAssignedRepository;
import com.example.task_manager.service.NotificationService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private IsAssignedRepository isAssignedRepository;

    private TeamMember createUniqueTeamMember() {
        return teamMemberRepository.save(new TeamMember(
            "User_" + System.nanoTime(),
            "user_" + System.nanoTime() + "@example.com",
            "defaultpw"
        ));
    }

    private Team createUniqueTeam() {
        return teamRepository.save(new Team(
            "Team_" + System.nanoTime(),
            null
        ));
    }

    private Task createUniqueTask(Team team) {
        Task task = new Task(
            "Task_" + System.nanoTime(),
            "Task Description",
            team,
            false,
            "Open",
            LocalDate.now(), 
            TaskPriority.MEDIUM
        );
        return taskRepository.save(task);
    }

    @Test
    void testNotifyTaskTitleChange() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        isAssignedRepository.save(assignment);

        String oldTitle = task.getTitle();
        task.setTitle("Updated Title");

        notificationService.notifyTaskTitleChange(task, oldTitle);

        List<Notification> notifications = notificationRepository.findByTeamMemberIdAndIsReadFalse(teamMember.getAccountId());

        assertFalse(notifications.isEmpty());
        assertEquals(NotificationType.TASK_TITLE_EDITED, notifications.get(0).getType());
        assertTrue(notifications.get(0).getMessage().contains("Task Update: The title of a task was changed"));
    }

    @Test
    void testNotifyTaskDescriptionChange() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        isAssignedRepository.save(assignment);

        String oldDescription = task.getDescription();
        task.setDescription("Updated Description");

        notificationService.notifyTaskDescriptionChange(task, oldDescription);

        List<Notification> notifications = notificationRepository.findByTeamMemberIdAndIsReadFalse(teamMember.getAccountId());

        assertFalse(notifications.isEmpty());
        assertEquals(NotificationType.TASK_DESCRIPTION_EDITED, notifications.get(0).getType());
        assertTrue(notifications.get(0).getMessage().contains("Task Update: The description of a task was changed"));
    }

    @Test
    void testNotifyTaskDueDateChange() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        IsAssigned assignment = new IsAssigned(task, teamMember, team);
        isAssignedRepository.save(assignment);

        LocalDate oldDueDate = LocalDate.now();
        task.setDueDate(LocalDate.now().plusDays(5));

        notificationService.notifyTaskDueDateChange(task, oldDueDate);

        List<Notification> notifications = notificationRepository.findByTeamMemberIdAndIsReadFalse(teamMember.getAccountId());

        assertFalse(notifications.isEmpty());
        assertEquals(NotificationType.TASK_DUE_DATE_EDITED, notifications.get(0).getType());
        assertTrue(notifications.get(0).getMessage().contains("Task Update: The due date of a task was changed"));
    }

    @Test
    void testMarkNotificationAsRead() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "You have been assigned a task.", task, teamMember);
        notification = notificationRepository.save(notification);

        notificationService.markAsRead(notification.getNotificationId());

        Notification updatedNotification = notificationRepository.findById(notification.getNotificationId()).orElse(null);

        assertNotNull(updatedNotification);
        assertTrue(updatedNotification.getIsRead());
    }

    @Test
    void testMarkNotificationAsUnread() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "You have been assigned a task.", task, teamMember);
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);

        notificationService.markAsUnread(notification.getNotificationId());

        Notification updatedNotification = notificationRepository.findById(notification.getNotificationId()).orElse(null);

        assertNotNull(updatedNotification);
        assertFalse(updatedNotification.getIsRead());
    }

    @Test
    void testDeleteNotification() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        Notification notification = new Notification(NotificationType.TASK_UNASSIGNED, "Task unassigned", task, teamMember);
        notification = notificationRepository.save(notification);

        notificationService.deleteNotification(notification.getNotificationId());

        assertFalse(notificationRepository.existsById(notification.getNotificationId()));
    }

        @Test
    void testGetUnreadNotifications() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        Notification notification = new Notification(
            NotificationType.TASK_ASSIGNED,
            "You have been assigned a task.",
            task,
            teamMember
        );
        notificationRepository.save(notification);

        List<NotificationDTO> unreadNotifications = notificationService.getUnreadNotifications(teamMember.getAccountId());

        assertFalse(unreadNotifications.isEmpty());
        assertEquals(1, unreadNotifications.size());
        assertEquals("You have been assigned a task.", unreadNotifications.get(0).getMessage());
        assertEquals(NotificationType.TASK_ASSIGNED, unreadNotifications.get(0).getType());
        assertFalse(unreadNotifications.get(0).isIsRead());
        assertEquals(task.getTaskId(), unreadNotifications.get(0).getTaskId());
        assertEquals(teamMember.getAccountId(), unreadNotifications.get(0).getTeamMemberId());
    }

    @Test
    void testGetReadNotifications() {
        Team team = createUniqueTeam();
        Task task = createUniqueTask(team);
        TeamMember teamMember = createUniqueTeamMember();

        Notification notification = new Notification(
            NotificationType.TASK_ASSIGNED,
            "You have been assigned a task.",
            task,
            teamMember
        );
        notification.setIsRead(true);
        notificationRepository.save(notification);

        List<NotificationDTO> readNotifications = notificationService.getReadNotifications(teamMember.getAccountId());

        assertFalse(readNotifications.isEmpty());
        assertEquals(1, readNotifications.size());
        assertEquals("You have been assigned a task.", readNotifications.get(0).getMessage());
        assertEquals(NotificationType.TASK_ASSIGNED, readNotifications.get(0).getType());
        assertTrue(readNotifications.get(0).isIsRead());
        assertEquals(task.getTaskId(), readNotifications.get(0).getTaskId());
        assertEquals(teamMember.getAccountId(), readNotifications.get(0).getTeamMemberId());
    }

    @Test
    void testGetUnreadNotificationsForNonExistentMember() {
        Exception exception = assertThrows(RuntimeException.class, () -> 
            notificationService.getUnreadNotifications(9999));

        assertTrue(exception.getMessage().contains("TeamMember not found"));
    }

    @Test
    void testGetReadNotificationsForNonExistentMember() {
        Exception exception = assertThrows(RuntimeException.class, () -> 
            notificationService.getReadNotifications(9999));

        assertTrue(exception.getMessage().contains("TeamMember not found"));
    }
}
