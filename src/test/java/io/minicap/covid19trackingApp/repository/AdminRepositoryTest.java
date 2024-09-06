package io.minicap.covid19trackingApp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import io.minicap.covid19trackingApp.appUsers.Administrator;
import io.minicap.covid19trackingApp.appUsers.User;

@DataJpaTest
@Sql({"/cleanup-data.sql","/create-data.sql"})
public class AdminRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdminRepository underTest;

    @Test
    public void contextLoads(){
        assertNotNull(underTest);
        assertNotNull(entityManager);
    }

    @Test
    public void testFindByEmail() {
        //Given the email of an existing admin
        String existingEmail = "Jynx@gmail.com";
        
        //when searching an admin in the DB with email
        Administrator existingAdmin = underTest.findByEmail(existingEmail);

        //then
        assertNotNull("For an existing admin, object should not be null", existingAdmin);
        assertEquals("For an existing admin, email should be the same", existingEmail, existingAdmin.getEmail());

        /*-----------------------------------------*/
        //given a fake email or an email of a user who is not an admin
        String badEmail = "Bulbasaur@gmail.com";

        //when searching an admin in the DB with bad email
        Administrator notAdmin = underTest.findByEmail(badEmail);

        //then
        assertNull("Is not admin so should be null", notAdmin);
    }
}
