package com.example.task_manager.DTO;

import java.time.LocalDate;
import java.util.List;
import com.example.task_manager.enums.TaskPriority;

public class TaskRequestDTO {
    private String title;
    private String description;
    private Boolean isLocked;
    private String status;
    private LocalDate dueDate;
    private List<Integer> assignedTo;
    private Integer teamId;
    private TaskPriority priority;

    public TaskRequestDTO() {}

    public TaskRequestDTO(String title, String description, Boolean isLocked, String status, LocalDate dueDate, List<Integer> assignedTo, Integer teamId, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.isLocked = isLocked;
        this.status = status;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.teamId = teamId;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<Integer> getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(List<Integer> assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
}
