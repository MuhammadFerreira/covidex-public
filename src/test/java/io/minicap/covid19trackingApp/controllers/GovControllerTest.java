package io.minicap.covid19trackingApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.governmentUser;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.appUsers.userRole;
import io.minicap.covid19trackingApp.repository.GovRepository;
import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.EmailService;
import io.minicap.covid19trackingApp.service.UserService;
import io.minicap.covid19trackingApp.service.statsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class GovControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private GovRepository govRepo;
    

    @Test
    public void contextLoads()
    {
        assertNotNull(mockMvc);
        assertNotNull(userRepo);
        assertNotNull(patientRepo);
    }
    
    // @Test
    // @WithMockUser(username = "test")
    // public void testEditProfile() throws Exception
    // {
    //     governmentUser gov = new governmentUser();
    //     gov.setFirstName("firstname");
    //     gov.setLastName("lastName");
    //     Mockito.when(govRepo.findByEmail("test")).thenReturn(gov);


    //     mockMvc.perform(MockMvcRequestBuilders.get("/govhome/about-me"))
    //     .andExpect(MockMvcResultMatchers.status().isOk())
    //     .andExpect(MockMvcResultMatchers.view().name("govAboutMe"));
    // }

    
    //place after thymeleaf mapping edit
    // @Test
    // @WithMockUser(username = "test")
    // public void testEditProfile() throws Exception
    // {
    //     governmentUser gov = new governmentUser();
    //     gov.setFirstName("firstname");
    //     gov.setLastName("lastName");
    //     Mockito.when(govRepo.findByEmail("test")).thenReturn(gov);


    //     mockMvc.perform(MockMvcRequestBuilders.get("/govhome/edit-profile"))
    //     .andExpect(MockMvcResultMatchers.status().isOk())
    //     .andExpect(MockMvcResultMatchers.view().name("governmentAccountSettings"));
    // }

    @Test
    @WithMockUser(username = "test")
    public void testShowAdminHome() throws Exception
    {
        List<Patient> list = new ArrayList<>();
        Patient p1 = new Patient();
        Patient p2 = new Patient();
        Patient p3 = new Patient();
        Patient p4 = new Patient();
        Patient p5 = new Patient();
        Patient p6 = new Patient();
        
        //set dummy patients num of doses
        p1.setNumOfDoses(2);
        p2.setNumOfDoses(4);
        p3.setNumOfDoses(1);
        p4.setNumOfDoses(3);
        p5.setNumOfDoses(0);
        p6.setNumOfDoses(2);


        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);

        User u1 = new User();
        User u2 = new User();
        User u3 = new User();
        User u4 = new User();

        List<User> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);
        users.add(u3);
        users.add(u4);

        governmentUser gov = new governmentUser();
        gov.setFirstName("firstname");
        gov.setLastName("lastName");
        Mockito.when(govRepo.findByEmail("test")).thenReturn(gov);

        Mockito.when(patientRepo.findAll()).thenReturn(list);
        Mockito.when(patientRepo.findAllBynumOfDoses(0)).thenReturn(list);
        Mockito.when(patientRepo.findAllBynumOfDoses(1)).thenReturn(list);
        Mockito.when(patientRepo.findAllBynumOfDoses(2)).thenReturn(list);
        Mockito.when(patientRepo.findAllBynumOfDoses(3)).thenReturn(list);
        Mockito.when(userRepo.findAllByUserRole(userRole.DOCTOR)).thenReturn(users);
        Mockito.when(patientRepo.findAllByInfectionStatus(infectionStatus.asymptomatic)).thenReturn(list);
        Mockito.when(patientRepo.findAllByInfectionStatus(infectionStatus.symptomatic)).thenReturn(list);
        Mockito.when(patientRepo.findAllByInfectionStatus(infectionStatus.critical)).thenReturn(list);
        Mockito.when(patientRepo.findAllByInfectionStatus(infectionStatus.contactTraced)).thenReturn(list);
        Mockito.when(patientRepo.findAllByInfectionStatus(infectionStatus.none)).thenReturn(list);

        
        
        mockMvc.perform(MockMvcRequestBuilders.get("/govhome"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("governmentDashboard"));
    }

    @Test
    public void flagPatientsTest() throws Exception
    {
        //given a patient
        String email = "Bulbasaur@gmail.com";
        Patient patient = new Patient();
        patient.setEmail(email);
        long id = 1005;
        patient.setId(id);
        patient.setIsFlagged(false);

        //when
        when(userRepo.findByEmail(email)).thenReturn(patient);
        when(patientRepo.getById(id)).thenReturn(patient);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/govhome/patients/flag")
            .param("patientEmail", email))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "Patient status updated"));

    }
    
}
