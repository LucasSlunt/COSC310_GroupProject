package com.example.task_manager.entity_tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.task_manager.Test_setup_methods;
import com.example.task_manager.entity.AuthInfo;
import com.example.task_manager.entity.TeamMember;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AuthInfoEntityTest extends Test_setup_methods{

    @Autowired
    private TestEntityManager entityManager;

    

    /**
     * Tests if an AuthInfo entity can be persisted and retrieved correctly.
     * Ensures that the saved AuthInfo retains the correct account ID, hashed password, and salt.
     */
    @Test
    void testAuthInfoPersistence() {
        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        AuthInfo savedAuthInfo = entityManager.find(AuthInfo.class, teamMember.getAccountId());

        assertNotNull(savedAuthInfo);
        assertEquals(teamMember.getAccountId(), savedAuthInfo.getAccountId());
        assertEquals(teamMember.getAuthInfo().getHashedPassword(), savedAuthInfo.getHashedPassword());
        assertNotNull(savedAuthInfo.getSalt());
    }

    /**
     * Tests that an AuthInfo entity cannot be persisted without a corresponding TeamMember.
     * Ensures that a PersistenceException is thrown when attempting to save AuthInfo without an owner.
     */
    @Test
    void testAuthInfoFailsWithoutTeamMember() {
        AuthInfo authInfo = new AuthInfo("hashed_password_" + System.nanoTime(), "random_salt", null);

        Exception e = assertThrows(PersistenceException.class, () -> {
            entityManager.persist(authInfo);
            entityManager.flush();
        });

        assertNotNull(e);
    }

    /**
     * Tests whether an AuthInfo entity is deleted when its associated TeamMember is removed.
     * Ensures that cascading delete behavior works as expected, preventing orphaned AuthInfo records.
     */
    @Test
    void testAuthInfoIsDeletedWithTeamMember() {
        TeamMember teamMember = createUniqueTeamMember();
        entityManager.persist(teamMember);
        entityManager.flush();

        AuthInfo authInfo = teamMember.getAuthInfo();
        entityManager.persist(authInfo);
        entityManager.flush();
        
        entityManager.remove(teamMember);
        entityManager.flush();

        AuthInfo deletedAuthInfo = entityManager.find(AuthInfo.class, authInfo.getAccountId());
        assertNull(deletedAuthInfo);
    }

    /*
     * Tests that AuthInfo is connected to the correct TeamMember
     */
    @Test
    void testAuthInfoBelongsToCorrectTeamMember() {
        TeamMember member1 = createUniqueTeamMember();
        TeamMember member2 = createUniqueTeamMember();

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.flush();

        AuthInfo retrievedAuthInfo1 = entityManager.find(AuthInfo.class, member1.getAccountId());
        AuthInfo retrievedAuthInfo2 = entityManager.find(AuthInfo.class, member2.getAccountId());

        assertNotNull(retrievedAuthInfo1);
        assertNotNull(retrievedAuthInfo2);
        assertEquals(member1.getAccountId(), retrievedAuthInfo1.getAccountId());
        assertEquals(member2.getAccountId(), retrievedAuthInfo2.getAccountId());
    }
}
