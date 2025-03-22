package com.example.task_manager.repository_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TaskRepository taskRepository;

    // Helper methods to create unique entities for each test
    private Team createUniqueTeam() {
        Team team = new Team();
        team.setTeamName("QA Team_" + System.nanoTime());
        return teamRepository.save(team);
    }

    private TeamMember createUniqueTeamMember() {
        TeamMember teamMember = new TeamMember("Alice_" + System.nanoTime(), "alice" + System.nanoTime() + "@example.com", "password123");
        return teamMemberRepository.save(teamMember);
    }

    private Task createUniqueTask(Team team) {
        Task task = new Task();
        task.setTitle("Test Notifications " + System.nanoTime());
        task.setStatus("Open");
        task.setDateCreated(LocalDate.now());
        task.setTeam(team);
        task.setPriority(TaskPriority.MEDIUM);
        return taskRepository.save(task);
    }

    @Test
    void testSaveNotification() {
        Team team = createUniqueTeam();
        TeamMember teamMember = createUniqueTeamMember();
        Task task = createUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "You have been assigned a task.", task, teamMember);
        Notification savedNotification = notificationRepository.save(notification);

        assertNotNull(savedNotification.getNotificationId());
        assertEquals(notification.getMessage(), savedNotification.getMessage());
        assertEquals(notification.getType(), savedNotification.getType());
    }

    @Test
    void testPrePersistCreatedAtField() {
        Team team = createUniqueTeam();
        TeamMember teamMember = createUniqueTeamMember();
        Task task = createUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_STATUS_EDITED, "Task status updated", task, teamMember);
        notificationRepository.save(notification);

        assertNotNull(notification.getCreatedAt());
        assertTrue(notification.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testNotificationAssociations() {
        Team team = createUniqueTeam();
        TeamMember teamMember = createUniqueTeamMember();
        Task task = createUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_DUE_DATE_EDITED, "Task due date changed", task, teamMember);
        Notification savedNotification = notificationRepository.save(notification);

        assertNotNull(savedNotification.getTask());
        assertEquals(task.getTaskId(), savedNotification.getTask().getTaskId());

        assertNotNull(savedNotification.getTeamMember());
        assertEquals(teamMember.getAccountId(), savedNotification.getTeamMember().getAccountId());
    }

    @Test
    void testReadUnreadStatusToggle() {
        Team team = createUniqueTeam();
        TeamMember teamMember = createUniqueTeamMember();
        Task task = createUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "Task assigned", task, teamMember);
        notification.setIsRead(false);
        notificationRepository.save(notification);

        Notification savedNotification = notificationRepository.findById(notification.getNotificationId()).get();
        assertFalse(savedNotification.getIsRead());

        savedNotification.setIsRead(true);
        notificationRepository.save(savedNotification);

        Notification updatedNotification = notificationRepository.findById(savedNotification.getNotificationId()).get();
        assertTrue(updatedNotification.getIsRead());
    }

    @Test
    void testDeleteNotification() {
        Team team = createUniqueTeam();
        TeamMember teamMember = createUniqueTeamMember();
        Task task = createUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_UNASSIGNED, "Task unassigned", task, teamMember);
        notificationRepository.save(notification);

        Notification savedNotification = notificationRepository.save(notification);
        int notificationId = savedNotification.getNotificationId();

        notificationRepository.deleteById(notificationId);

        assertFalse(notificationRepository.existsById(notificationId));
    }

    @Test
    void testFindUnreadNotificationsForNonexistentMember() {
        List<Notification> notifs = notificationRepository.findByTeamMemberIdAndIsReadFalse(99999);
        assertNotNull(notifs);
        assertEquals(0, notifs.size());
    }

    @Test
    void testFindReadNotificationsForNonexistentMember() {
        List<Notification> notifs = notificationRepository.findByTeamMemberIdAndIsReadTrue(99999);
        assertNotNull(notifs);
        assertEquals(0, notifs.size());
    }
}
