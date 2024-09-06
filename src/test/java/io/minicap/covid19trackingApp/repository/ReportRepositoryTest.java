package io.minicap.covid19trackingApp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;

@DataJpaTest
@Sql({"/cleanup-data.sql","/create-data.sql"})
public class ReportRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReportRepository underTest;

    @Autowired
    private PatientRepository patientRepo;

    @Test
    public void contextLoads(){
        assertNotNull(underTest);
        assertNotNull(entityManager);
    }

    @Test
    public void testFindAllByPatient()
    {   

        List<dailyReport> list  = underTest.findAllByPatient(patientRepo.findByEmail("Bulbasaur@gmail.com"));
        assertNotNull(list);
        assertTrue(list.size()>0);
    }

    @Test
    public void testFindAllByPatientAndIsReviewed()
    {
        List<dailyReport> list = underTest.findAllByPatientAndIsReviewed(patientRepo.findByEmail("Bulbasaur@gmail.com"
        ), true);
        assertNotNull(list);
        assertTrue(list.size()>0);
    }
}
