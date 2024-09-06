package io.minicap.covid19trackingApp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

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

import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.userRole;

@DataJpaTest
@Sql({"/cleanup-data.sql","/create-data.sql"})
public class UserRepositoryTest
{

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository underTest;
    
    @Test
    public void contextLoads()
    {
        assertNotNull(underTest);
        assertNotNull(entityManager);
    }
    
    @Test
    public void testFindByEmail()
    {
        User user = underTest.findByEmail("Bulbasaur@gmail.com");
        assertEquals("Bulbasaur", user.getFirstName());
        assertEquals("Bulbasaur@gmail.com", user.getEmail());
        assertEquals(1, user.getPid());

    }
    @Test
    public void testFindByConfirmationToken()
    {
        User user = underTest.findByConfirmationToken("fb7964db-1f34-4054-ada1-2292b33f4dd6");
        assertEquals("Bulbasaur", user.getFirstName());
        assertEquals("Bulbasaur@gmail.com", user.getEmail());
        assertEquals(1, user.getPid());
        assertEquals("fb7964db-1f34-4054-ada1-2292b33f4dd6", user.getConfirmationToken());
    }

    @Test
    public void listFoundByRole()
    {
        List<User> list = underTest.findAllByUserRole(userRole.PATIENT);
        assertNotNull(list);
        assertTrue(list.size()>0);
    }

}