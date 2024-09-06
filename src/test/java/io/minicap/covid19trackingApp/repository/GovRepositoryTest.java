package io.minicap.covid19trackingApp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

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

import io.minicap.covid19trackingApp.appUsers.governmentUser;

@DataJpaTest
@Sql({"/cleanup-data.sql","/create-data.sql"})
public class GovRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GovRepository underTest;

    @Test
    public void contextLoads(){
        assertNotNull(underTest);
        assertNotNull(entityManager);
    }

    @Test
    public void testFindByEmail()
    {
        governmentUser user = underTest.findByEmail("Snorlax@gmail.com");
        assertEquals("Snorlax@gmail.com", user.getEmail());
    }
}
