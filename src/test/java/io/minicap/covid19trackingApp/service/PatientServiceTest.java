package io.minicap.covid19trackingApp.service;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.config.CustomUserDetails;
import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Painter;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql({ "/cleanup-data.sql", "/create-data.sql" })
public class PatientServiceTest {

    @Autowired
    private PatientRepository patientRepo;

    @Test
    public void contextLoads() {
        assertNotNull(patientRepo);
    }

    @Test
    public void testGetEdgesFrom() {
        PatientService patientService = Mockito.spy(new PatientService(patientRepo));
        Patient patient = patientRepo.findByEmail("Charmeleon@gmail.com");
        List<Patient> contactTraced = patientRepo.findByContactTraceTo(patient);
        assertNotNull(patient);
        ArrayList<Patient[]> edges = new ArrayList<Patient[]>();

        for (Patient a : contactTraced) {
            Patient[] edgeArray = new Patient[2];

            edgeArray[0] = a;
            edgeArray[1] = patient;

            if (edges.contains(edgeArray)) {
                continue;
            }
            edges.add(edgeArray);
            edges.addAll(patientService.getEdgesFrom(a));
        }
        assertNotNull(edges);
        // edge 0
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), edges.get(0)[0]);
        assertEquals(patientRepo.findByEmail("Charmeleon@gmail.com"), edges.get(0)[1]);
        // edge 1
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), edges.get(1)[0]);
        assertEquals(patientRepo.findByEmail("Charmeleon@gmail.com"), edges.get(1)[1]);
        // edge 2
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), edges.get(2)[0]);
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), edges.get(2)[1]);
    }

    @Test
    public void testGetEdgesTo() {

        PatientService patientService = Mockito.spy(new PatientService(patientRepo));
        Patient patient = patientRepo.findByEmail("Bulbasaur@gmail.com");
        List<Patient> contactTracedFrom = patientRepo.findByContactTraceFrom(patient);
        assertNotNull(patient);
        ArrayList<Patient[]> edges = new ArrayList<Patient[]>();
        for (Patient a : contactTracedFrom) {

            Patient[] edgeArray = new Patient[2];

            edgeArray[0] = patient;
            edgeArray[1] = a;

            if (edges.contains(edgeArray)) {
                continue;
            }

            edges.add(edgeArray);
            edges.addAll(patientService.getEdgesTo(a));
        }
        assertTrue(edges.size() > 0);
        // edge 0
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), edges.get(0)[0]);
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), edges.get(0)[1]);
        // edge 1
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), edges.get(1)[0]);
        assertEquals(patientRepo.findByEmail("Charmeleon@gmail.com"), edges.get(1)[1]);
        // edge 2
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), edges.get(2)[0]);
        assertEquals(patientRepo.findByEmail("Charizard@gmail.com"), edges.get(2)[1]);
        // edge 3
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), edges.get(3)[0]);
        assertEquals(patientRepo.findByEmail("Squirtle@gmail.com"), edges.get(3)[1]);
        // edge 4
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), edges.get(4)[0]);
        assertEquals(patientRepo.findByEmail("Venusaur@gmail.com"), edges.get(4)[1]);
        // edge 5
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), edges.get(5)[0]);
        assertEquals(patientRepo.findByEmail("Charmander@gmail.com"), edges.get(5)[1]);
        // edge 6
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), edges.get(6)[0]);
        assertEquals(patientRepo.findByEmail("Charmeleon@gmail.com"), edges.get(6)[1]);

    }

    @Test
    public void testGetNodesTo() {
        PatientService patientService = Mockito.spy(new PatientService(patientRepo));
        Patient ptest = patientRepo.findByEmail("Charizard@gmail.com");
        List<Patient> contactTraced = patientRepo.findByContactTraceTo(ptest);
        assertNotNull(contactTraced);
        ArrayList<Patient> nodes = new ArrayList<Patient>();
        for (Patient a : contactTraced) {
            if (nodes.contains(a)) {
                continue;
            }
            nodes.add(a);
            nodes.addAll(patientService.getNodesTo(a));
        }
        assertTrue(nodes.size() == 2);
        assertTrue(nodes.contains(patientRepo.findByEmail("Ivysaur@gmail.com")));
        assertTrue(nodes.contains(patientRepo.findByEmail("Bulbasaur@gmail.com")));

    }

    @Test
    public void testGetNodesFrom() {
        PatientService patientService = Mockito.spy(new PatientService(patientRepo));
        Patient ptest = patientRepo.findByEmail("Bulbasaur@gmail.com");
        List<Patient> contactTraced = patientRepo.findByContactTraceFrom(ptest);
        assertNotNull(contactTraced);
        ArrayList<Patient> nodes = new ArrayList<Patient>();
        for (Patient a : contactTraced) {
            if (nodes.contains(a)) {
                continue;
            }
            nodes.add(a);
            nodes.addAll(patientService.getNodesFrom(a));
        }
        // should be 6
        assertTrue(nodes.size() == 6);
        assertTrue(nodes.contains(patientRepo.findByEmail("Ivysaur@gmail.com")));
        assertTrue(nodes.contains(patientRepo.findByEmail("Charmeleon@gmail.com")));
        assertTrue(nodes.contains(patientRepo.findByEmail("Charizard@gmail.com")));
        assertTrue(nodes.contains(patientRepo.findByEmail("Squirtle@gmail.com")));
        assertTrue(nodes.contains(patientRepo.findByEmail("Venusaur@gmail.com")));
        assertTrue(nodes.contains(patientRepo.findByEmail("Charmander@gmail.com")));

    }

    @Test
    public void testGetAllEdges() {
        PatientService patientService = Mockito.spy(new PatientService(patientRepo));
        Patient patient = patientRepo.findByEmail("Bulbasaur@gmail.com");

        ArrayList<Patient[]> allEdges = patientService.getEdgesTo(patient);

        allEdges.addAll(patientService.getEdgesFrom(patient));

        assertNotNull(allEdges);
        // edge 0
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), allEdges.get(0)[0]);
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), allEdges.get(0)[1]);
        // edge 1
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), allEdges.get(1)[0]);
        assertEquals(patientRepo.findByEmail("Charmeleon@gmail.com"), allEdges.get(1)[1]);
        // edge 2
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), allEdges.get(2)[0]);
        assertEquals(patientRepo.findByEmail("Charizard@gmail.com"), allEdges.get(2)[1]);
        // edge 3
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), allEdges.get(3)[0]);
        assertEquals(patientRepo.findByEmail("Squirtle@gmail.com"), allEdges.get(3)[1]);
        // edge 4
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), allEdges.get(4)[0]);
        assertEquals(patientRepo.findByEmail("Venusaur@gmail.com"), allEdges.get(4)[1]);
        // edge 5
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), allEdges.get(5)[0]);
        assertEquals(patientRepo.findByEmail("Charmander@gmail.com"), allEdges.get(5)[1]);
        // edge 6
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), allEdges.get(6)[0]);
        assertEquals(patientRepo.findByEmail("Charmeleon@gmail.com"), allEdges.get(6)[1]);

    }

    @Test
    public void testGetAllNodes() {
        PatientService patientService = Mockito.spy(new PatientService(patientRepo));
        Patient patient = patientRepo.findByEmail("Charmeleon@gmail.com");

        ArrayList<Patient> allNodes = patientService.getNodesTo(patient);

        allNodes.addAll(patientService.getNodesFrom(patient));

        allNodes.add(patient);
        assertNotNull(allNodes);
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), allNodes.get(0));
        assertEquals(patientRepo.findByEmail("Ivysaur@gmail.com"), allNodes.get(1));
        assertEquals(patientRepo.findByEmail("Bulbasaur@gmail.com"), allNodes.get(2));
        assertEquals(patientRepo.findByEmail("Charmeleon@gmail.com"), allNodes.get(3));
    }

}
