package com.example.task_manager.repository_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.task_manager.Test_setup_methods;
import com.example.task_manager.entity.Notification;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.enums.NotificationType;
import com.example.task_manager.repository.NotificationRepository;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationRepositoryTest extends Test_setup_methods {

    @Autowired
    private NotificationRepository notificationRepository;
    

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TaskRepository taskRepository;

   

    @Test
    void testSaveNotification() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "You have been assigned a task.", task, teamMember);
        Notification savedNotification = notificationRepository.save(notification);

        assertNotNull(savedNotification.getNotificationId());
        assertEquals(notification.getMessage(), savedNotification.getMessage());
        assertEquals(notification.getType(), savedNotification.getType());
    }

    @Test
    void testPrePersistCreatedAtField() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_STATUS_EDITED, "Task status updated", task, teamMember);
        notificationRepository.save(notification);

        assertNotNull(notification.getCreatedAt());
        assertTrue(notification.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testNotificationAssociations() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

        Notification notification = new Notification(NotificationType.TASK_DUE_DATE_EDITED, "Task due date changed", task, teamMember);
        Notification savedNotification = notificationRepository.save(notification);

        assertNotNull(savedNotification.getTask());
        assertEquals(task.getTaskId(), savedNotification.getTask().getTaskId());

        assertNotNull(savedNotification.getTeamMember());
        assertEquals(teamMember.getAccountId(), savedNotification.getTeamMember().getAccountId());
    }

    @Test
    void testReadUnreadStatusToggle() {
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

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
        Team team = createAndSaveUniqueTeam();
        TeamMember teamMember = createAndSaveUniqueTeamMember();
        Task task = createAndSaveUniqueTask(team);

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
