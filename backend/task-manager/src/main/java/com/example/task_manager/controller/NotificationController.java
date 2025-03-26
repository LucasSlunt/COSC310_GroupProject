package com.example.task_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.task_manager.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    private NotificationService notifService;

    public NotificationController(NotificationService notifService) {
        this.notifService = notifService;
    }

    //Get all notifications marked as read for a specific team member
    @GetMapping("/{teamMemberId}/read")
    public ResponseEntity<?> getReadNotifications(@PathVariable int teamMemberId) {
        try {
            return ResponseEntity.ok(notifService.getReadNotifications(teamMemberId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Get all notifications marked as unread for a specific team member
    @GetMapping("/{teamMemberId}/unread")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable int teamMemberId) {
        try {
            return ResponseEntity.ok(notifService.getUnreadNotifications(teamMemberId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Mark a specific notification as read
    @PutMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<?> markAsRead(@PathVariable int notificationId) {
        try {
            notifService.markAsRead(notificationId);
            return ResponseEntity.ok("Notification marked as read.");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Mark a specific notification as unread
    @PutMapping("/{notificationId}/mark-as-unread")
    public ResponseEntity<?> markAsUnread(@PathVariable int notificationId) {
        try {
            notifService.markAsUnread(notificationId);
            return ResponseEntity.ok("Notification marked as unread.");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Delete a notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable int notificationId) {
        try {
            notifService.deleteNotification(notificationId);
            return ResponseEntity.noContent().build();
        } 
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
