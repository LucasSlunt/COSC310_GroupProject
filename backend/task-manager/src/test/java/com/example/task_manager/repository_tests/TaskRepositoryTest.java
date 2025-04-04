package com.example.task_manager.repository_tests;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.entity.Task;
import com.example.task_manager.test_helpers.RepositoryTestHelper;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class TaskRepositoryTest extends RepositoryTestHelper {

    @Test
    void testFindByTeam_TeamId() {
        TeamMember teamMember = new TeamMember("Rush", "spirit_of_the_radio@music.com", "rush_music");
        teamMemberRepository.save(teamMember);

        Team team = new Team("Team 1", teamMember);
        teamRepository.save(team);

        Task task = new Task("Fix Bugs", "Fix all critical bugs", team, false, "Open", LocalDate.now(), TaskPriority.HIGH);
        taskRepository.save(task);

        List<Task> results = taskRepository.findByTeam_TeamId(team.getTeamId());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Fix Bugs", results.get(0).getTitle());
        assertEquals(team.getTeamId(), results.get(0).getTeam().getTeamId());
    }
}

