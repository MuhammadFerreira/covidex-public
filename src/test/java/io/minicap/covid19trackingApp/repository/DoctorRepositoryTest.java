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

import io.minicap.covid19trackingApp.appUsers.Doctor;

@DataJpaTest
@Sql({"/cleanup-data.sql","/create-data.sql"})
public class DoctorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorRepository underTest;

    @Test
    public void contextLoads(){
        assertNotNull(underTest);
        assertNotNull(entityManager);
    }


    @Test
    public void testFindByEmail() {
        //given an email of doctor
        String docEmail = "Exeggutor@gmail.com";

        //when
        Doctor doctor = underTest.findByEmail(docEmail);

        //then
        assertNotNull("Doc exists, so should not be null", doctor);
        assertEquals("Doctor email should be correct", docEmail, doctor.getEmail());

        /*----------------------------------------*/
        //given a bad email (dne in DB or not a doctor)
        String badEmail = "Bulbasaur@gmail.com";

        //when
        Doctor notDoctor = underTest.findByEmail(badEmail);

        //then
        assertNull("Not doc, so should be null", notDoctor);
    }
}
