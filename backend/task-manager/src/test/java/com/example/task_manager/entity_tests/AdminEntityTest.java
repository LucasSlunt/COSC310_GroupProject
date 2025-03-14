package com.example.task_manager.entity_tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

import com.example.task_manager.entity.Admin;
import com.example.task_manager.entity.TeamMember;

import jakarta.transaction.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class AdminEntityTest {

    @Autowired
    private TestEntityManager entMan;

    @BeforeEach
    void cleanDatabase() {
        entMan.getEntityManager().createQuery("DELETE FROM IsAssigned").executeUpdate();
        entMan.getEntityManager().createQuery("DELETE FROM Task").executeUpdate();
        entMan.getEntityManager().createQuery("DELETE FROM AuthInfo").executeUpdate();
        entMan.getEntityManager().createQuery("DELETE FROM IsMemberOf").executeUpdate();
        entMan.getEntityManager().createQuery("DELETE FROM Team").executeUpdate();
        entMan.getEntityManager().createQuery("DELETE FROM TeamMember").executeUpdate();
        entMan.getEntityManager().createQuery("DELETE FROM Admin").executeUpdate();
        entMan.flush();
    }


    private Admin createUniqueAdmin() {
        return new Admin("Admin_" + System.nanoTime(), "admin_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    private TeamMember createUniqueTeamMember() {
        return new TeamMember("TeamUser_" + System.nanoTime(), "team_user_" + System.nanoTime() + "@example.com", "defaultpw");
    }

    /**
     * Tests if an Admin entity can be persisted and retrieved correctly.
     */
    @Test
    void testAdminPersistence() {
        Admin admin = createUniqueAdmin();
        entMan.persist(admin);
        entMan.flush();

        Admin savedAdmin = entMan.find(Admin.class, admin.getAccountId());

        assertNotNull(savedAdmin);
        assertEquals(admin.getUserName(), savedAdmin.getUserName());
        assertEquals(admin.getUserEmail(), savedAdmin.getUserEmail());
    }

    /**
     * Tests whether an Admin entity is also stored as a TeamMember.
     */
    @Test
    void testAdminStoredAsTeamMember() {
        Admin admin = createUniqueAdmin();
        entMan.persist(admin);
        entMan.flush();

        TeamMember savedMember = entMan.find(TeamMember.class, admin.getAccountId());

        assertNotNull(savedMember);
        assertTrue(savedMember instanceof Admin);
    }

    /**
     * Tests whether an Admin can be retrieved when querying all TeamMembers.
     */
    @Test
    @Transactional
    @Rollback
    void testAdminQueryFromTeamMember() {
        Admin admin = createUniqueAdmin();
        TeamMember teamMember = createUniqueTeamMember();
        entMan.persist(admin);
        entMan.persist(teamMember);
        entMan.flush();

        List<TeamMember> teamMembers = entMan.getEntityManager()
                .createQuery("SELECT tm FROM TeamMember tm", TeamMember.class)
                .getResultList();

        assertEquals(2, teamMembers.size());
        assertTrue(teamMembers.stream().anyMatch(tm -> tm instanceof Admin));
    }

    /**
     * Ensuring that deleting an admin works just like deleting a teamMember.
     */
    @Test
    void testAdminDeletion() {
        Admin admin = createUniqueAdmin();
        entMan.persist(admin);
        entMan.flush();

        entMan.remove(admin);
        entMan.flush();

        Admin deletedAdmin = entMan.find(Admin.class, admin.getAccountId());
        assertNull(deletedAdmin);
    }

    /**
     * Test that admin can be found by ID.
     */
    @Test
    void testAdminQueryById() {
        Admin admin = createUniqueAdmin();
        entMan.persist(admin);
        entMan.flush();

        Admin foundAdmin = entMan.getEntityManager()
                .createQuery("SELECT a FROM Admin a WHERE a.accountId = :id", Admin.class)
                .setParameter("id", admin.getAccountId())
                .getSingleResult();

        assertNotNull(foundAdmin);
        assertEquals(admin.getUserEmail(), foundAdmin.getUserEmail());
    }

    /**
     * Ensure Admin still functions as a TeamMember (since it extends TeamMember).
     */
    @Test
    void testAdminInheritsTeamMemberBehavior() {
        Admin admin = createUniqueAdmin();
        entMan.persist(admin);
        entMan.flush();

        TeamMember foundMember = entMan.find(TeamMember.class, admin.getAccountId());

        assertNotNull(foundMember);
        assertTrue(foundMember instanceof Admin);
    }
}
