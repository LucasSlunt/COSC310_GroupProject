package com.example.task_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.task_manager.entity.IsAssigned;

@Repository
public interface IsAssignedRepository extends JpaRepository<IsAssigned, Integer> {
}