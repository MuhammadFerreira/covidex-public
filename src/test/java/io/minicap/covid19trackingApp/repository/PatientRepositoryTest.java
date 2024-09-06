package io.minicap.covid19trackingApp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.appUsers.userRole;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@DataJpaTest
@Sql({"/cleanup-data.sql","/create-data.sql"})
public class PatientRepositoryTest{

    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private PatientRepository underTest;
    
    @Test
    public void contextLoads(){
        assertNotNull(underTest);
        assertNotNull(entityManager);
    }

    @Test
    public void testfindByContactTraceTo()
    {
        Patient ptest = underTest.findByEmail("Bulbasaur@gmail.com");
        List<Patient> contactTraced = underTest.findByContactTraceTo(ptest);
    }

    @Test
    public void testFindByEmail()
    {
        Patient p = underTest.findByEmail("Bulbasaur@gmail.com");
        assertEquals("Bulbasaur", p.getFirstName());
        assertEquals("Bulbasaur@gmail.com", p.getEmail());
        assertEquals(1, p.getPid());
    }

    @Test
    public void testFindAllByInfectionStatus() throws IllegalArgumentException
    {   
        
        infectionStatus status = infectionStatus.symptomatic;
        List<Patient> infectedlist = underTest.findAllByInfectionStatus(status);
        //check if there is atleast 1 infected symptomatic in the database
        assertTrue(infectedlist.size()>0);
    }

    @Test
    public void testFindAllBynumOfDoses()
    {
        //given
        int numOfDoses = 2;
        //when
        List<Patient> list = underTest.findAllBynumOfDoses(numOfDoses);
        //then
        for(int i = 0; i < list.size(); i++) {
            assertThat(list.get(i).getNumOfDoses()).isEqualTo(2);
        }
    }
}