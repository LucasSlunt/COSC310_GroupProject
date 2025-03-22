package com.example.task_manager.entity_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationEntityTest {
    
    @Autowired
    private EntityManager entMan;

    private Team persistTeam() {
        Team team = new Team("Team_" + System.nanoTime(), null);
        entMan.persist(team);
        entMan.flush();
        return entMan.find(Team.class, team.getTeamId());
    }

    private TeamMember persistTeamMember() {
        TeamMember teamMember = new TeamMember("User_" + System.nanoTime(), "user_" + System.nanoTime() + "@example.com", "defaultpw");
        entMan.persist(teamMember);
        entMan.flush();
        return entMan.find(TeamMember.class, teamMember.getAccountId());
    }

    private Task persistTask(Team team) {
        Task task = new Task("Task_" + System.nanoTime(), "Task description", team, false, "Open", LocalDate.now(), TaskPriority.HIGH);
        entMan.persist(task);
        entMan.flush();
        return entMan.find(Task.class, task.getTaskId());
    }

    @Test
    void testNotificationPersistence() {
        Team team = persistTeam();
        TeamMember teamMember = persistTeamMember();
        Task task = persistTask(team);

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "You have been assigned a task.", task, teamMember);
        entMan.persist(notification);
        entMan.flush();

        Notification savedNotification = entMan.find(Notification.class, notification.getNotificationId());

        assertNotNull(savedNotification);
        assertEquals(notification.getMessage(), savedNotification.getMessage());
        assertEquals(notification.getType(), savedNotification.getType());
    }

    @Test
    void testPrePersistCreatedAtField() {
        Team team = persistTeam();
        TeamMember teamMember = persistTeamMember();
        Task task = persistTask(team);

        Notification notification = new Notification(NotificationType.TASK_STATUS_EDITED, "Task status updated", task, teamMember);
        entMan.persist(notification);
        entMan.flush();

        Notification savedNotification = entMan.find(Notification.class, notification.getNotificationId());

        assertNotNull(savedNotification.getCreatedAt());
        assertTrue(savedNotification.getCreatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    void testNotificationAssociations() {
        Team team = persistTeam();
        TeamMember teamMember = persistTeamMember();
        Task task = persistTask(team);

        Notification notification = new Notification(NotificationType.TASK_DUE_DATE_EDITED, "Task due date changed", task, teamMember);
        entMan.persist(notification);
        entMan.flush();

        Notification savedNotification = entMan.find(Notification.class, notification.getNotificationId());

        assertNotNull(savedNotification.getTask());
        assertEquals(task.getTaskId(), savedNotification.getTask().getTaskId());

        assertNotNull(savedNotification.getTeamMember());
        assertEquals(teamMember.getAccountId(), savedNotification.getTeamMember().getAccountId());
    }

    @Test
    void testReadUnreadStatusToggle() {
        Team team = persistTeam();
        TeamMember teamMember = persistTeamMember();
        Task task = persistTask(team);

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "Task assigned", task, teamMember);
        notification.setIsRead(false);
        entMan.persist(notification);
        entMan.flush();

        Notification savedNotification = entMan.find(Notification.class, notification.getNotificationId());

        assertFalse(savedNotification.getIsRead());

        savedNotification.setIsRead(true);
        entMan.merge(savedNotification);
        entMan.flush();

        Notification updatedNotification = entMan.find(Notification.class, savedNotification.getNotificationId());
        assertTrue(updatedNotification.getIsRead());
    }

    @Test
    void testDeletingNotification() {
        Team team = persistTeam();
        TeamMember teamMember = persistTeamMember();
        Task task = persistTask(team);

        Notification notification = new Notification(NotificationType.TASK_UNASSIGNED, "Task unassigned", task, teamMember);
        entMan.flush();

        int notificationId = notification.getNotificationId();

        entMan.remove(notification);
        entMan.flush();

        assertNull(entMan.find(Notification.class, notificationId));
    }

    @Test
    void testNotificationWithoutTeamMemberFails() {
        Team team = persistTeam();
        Task task = persistTask(team);

        Notification notification = new Notification(NotificationType.TASK_ASSIGNED, "Task assigned", task, null);

        Exception e = assertThrows(PersistenceException.class, () -> {
            entMan.persist(notification);
            entMan.flush();
        });

        assertNotNull(e);
    }
}
