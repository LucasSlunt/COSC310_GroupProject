package com.example.task_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.task_manager.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    public List<Task> findByTeam_TeamId(int teamId);
}
