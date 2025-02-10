package com.example.task_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.task_manager.entity.IsMemberOf;

@Repository
public interface IsMemberOfRepository extends JpaRepository<IsMemberOf, Integer> {
	// Retrieves all members associated with a given team ID
	@Query("SELECT i FROM IsMemberOf i WHERE i.team.teamId = :teamId")
	List<IsMemberOf> findMembersByTeamId(@Param("teamId") int teamId);
}
