package com.example.task_manager.repository_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.task_manager.Test_setup_methods;
import com.example.task_manager.entity.IsMemberOf;
import com.example.task_manager.entity.Team;
import com.example.task_manager.entity.TeamMember;
import com.example.task_manager.repository.AuthInfoRepository;
import com.example.task_manager.repository.IsMemberOfRepository;
import com.example.task_manager.repository.TeamMemberRepository;
import com.example.task_manager.repository.TeamRepository;
import com.example.task_manager.repository.AdminRepository;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IsMemberOfRepositoryTest extends Test_setup_methods{




    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private AuthInfoRepository authInfoRepository;

    

    /**
     * Tests the findMembersByTeamId() method to ensure it retrieves the correct members.
     */
    @Test
    void testFindMembersByTeamId() {
        Team team = createAndPersistUniqueTeam();
        TeamMember teamMember = createAndPersistUniqueTeamMember();
        createAndPersistUniqueMembership(team, teamMember);

        List<IsMemberOf> members = isMemberOfRepository.findMembersByTeamId(team.getTeamId());

        assertNotNull(members);
        assertEquals(1, members.size());
        assertEquals(teamMember.getAccountId(), members.get(0).getTeamMember().getAccountId());
    }

    /**
     * Tests that finding members for a non-existent team returns an empty list.
     */
    @Test
    void testFindMembersByNonExistentTeamId() {
        List<IsMemberOf> members = isMemberOfRepository.findMembersByTeamId(9999);
        assertNotNull(members);
        assertTrue(members.isEmpty());
    }

    /**
     * Tests the existsByTeamMemberAccountIdAndTeamTeamId() method for an existing member.
     */
    @Test
    void testExistsByTeamMemberAndTeam() {
        Team team = createAndPersistUniqueTeam();
        TeamMember teamMember = createAndPersistUniqueTeamMember();
        createAndPersistUniqueMembership(team, teamMember);

        boolean exists = isMemberOfRepository.existsByTeamMemberAccountIdAndTeamTeamId(
                teamMember.getAccountId(), team.getTeamId());
        assertTrue(exists);
    }

    /**
     * Tests the existsByTeamMemberAccountIdAndTeamTeamId() method for a non-existent membership.
     */
    @Test
    void testExistsByNonExistentMemberAndTeam() {
        boolean exists = isMemberOfRepository.existsByTeamMemberAccountIdAndTeamTeamId(9999, 9999);
        assertFalse(exists);
    }

    /**
     * Tests finding a specific TeamMember-Team relationship.
     */
    @Test
    void testFindByTeamMemberAndTeam() {
        Team team = createAndPersistUniqueTeam();
        TeamMember teamMember = createAndPersistUniqueTeamMember();
        IsMemberOf isMemberOf = createAndPersistUniqueMembership(team, teamMember);

        Optional<IsMemberOf> foundMembership = isMemberOfRepository.findByTeamMemberAndTeam(teamMember, team);
        assertTrue(foundMembership.isPresent());
        assertEquals(isMemberOf.getId(), foundMembership.get().getId());
    }

    /**
     * Tests deleting a membership and ensuring it no longer exists.
     */
    @Test
    void testDeleteMembership() {
        Team team = createAndPersistUniqueTeam();
        TeamMember teamMember = createAndPersistUniqueTeamMember();
        IsMemberOf isMemberOf = createAndPersistUniqueMembership(team, teamMember);

        isMemberOfRepository.delete(isMemberOf);
        entityManager.flush();

        Optional<IsMemberOf> deletedMembership = isMemberOfRepository.findByTeamMemberAndTeam(teamMember, team);
        assertFalse(deletedMembership.isPresent());

        boolean exists = isMemberOfRepository.existsByTeamMemberAccountIdAndTeamTeamId(
                teamMember.getAccountId(), team.getTeamId());
        assertFalse(exists);
    }
}
