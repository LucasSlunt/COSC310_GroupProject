package com.example.task_manager.DTO;

import java.time.LocalDate;
import java.util.List;

public class TaskDTO {
    private int taskId;
    private String title;
    private String description;
    private boolean isLocked;
    private String status;
    private LocalDate dateCreated;
    private LocalDate dueDate;
    private int teamId;
    private List<TeamMemberDTO> assignedMembers;

    public TaskDTO() {}

    public TaskDTO(int taskId, String title, String description, boolean isLocked, String status, LocalDate dateCreated, LocalDate dueDate, int teamId, List<TeamMemberDTO> assignedMembers) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.isLocked = isLocked;
        this.status = status;
        this.dateCreated = dateCreated;
        this.dueDate = dueDate;
        this.teamId = teamId;
        this.assignedMembers = assignedMembers;
    }

    //getters and setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    public boolean getIsLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public List<TeamMemberDTO> getAssignedMembers() {
        return assignedMembers;
    }

    public void setAssignedMembers(List<TeamMemberDTO> assignedMembers) {
        this.assignedMembers = assignedMembers;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }
}
