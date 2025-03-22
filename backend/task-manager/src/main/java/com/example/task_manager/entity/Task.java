package com.example.task_manager.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.example.task_manager.enums.TaskPriority;

import jakarta.persistence.*;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private boolean isLocked;

    @Column(nullable = false)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(nullable = false)
    private LocalDate dateCreated;

    private LocalDate expectedCompletionDate;
    private LocalDate dueDate;

    @ManyToOne()
    @JoinColumn(name = "teamId", nullable = true)
    private Team team;

    @OneToMany(mappedBy = "task", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private Set<IsAssigned> assignedMembers = new HashSet<>();

    public Task() {}

    public Task(String title, String description, Team team, boolean isLocked, String status, LocalDate dateCreated, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.team = team;
        this.isLocked = isLocked;
        this.status = status;
        this.dateCreated = dateCreated;
        this.priority = priority;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        if (taskId < 0) {
            throw new IllegalArgumentException("Task ID cannot be less than 0.");
        }
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty or null.");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty or null.");
        }
        this.description = description;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {

        this.isLocked = isLocked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty or null.");
        }
        this.status = status;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        if (dateCreated == null) {
            throw new IllegalArgumentException("Date created cannot be null.");
        }
        if (dateCreated.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date created cannot be in the future");
        }
        this.dateCreated = dateCreated;
    }

    public LocalDate getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(LocalDate expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null.");
        }
        this.team = team;
    }

    public Set<IsAssigned> getAssignedMembers() {
        return assignedMembers;
    }

    /*
     * If assignedMembers is null it initializes an empty set
     * There can be no members assigned, so an empty set, but it cannot be null
     */
    public void setAssignedMembers(Set<IsAssigned> assignedMembers) {
        this.assignedMembers = (assignedMembers != null) ? assignedMembers : new HashSet<>();
    }
    
    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
}
