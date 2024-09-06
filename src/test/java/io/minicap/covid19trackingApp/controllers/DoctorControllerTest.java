package io.minicap.covid19trackingApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.userRole;
import io.minicap.covid19trackingApp.repository.DoctorRepository;
import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.ReportRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private ReportRepository reportRepo;

    @MockBean
    private DoctorRepository doctorRepo;

    @Test
    public void contextLoads()
    {
        assertNotNull(userRepo);
        assertNotNull(patientRepo);
        assertNotNull(reportRepo);
        assertNotNull(doctorRepo);

    }

    // @Test
    // @WithMockUser(username = "test")
    // public void testShowDoctorHome() throws Exception
    // {
    //     User user = new User();
    //     user.setEnabled(true);
    //     Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
    //     mockMvc.perform(MockMvcRequestBuilders.get("/doctorhome"))
    //     .andExpect(MockMvcResultMatchers.status().isOk())
    //     .andExpect(MockMvcResultMatchers.view().name("doctorDashboard"));
    // }
}
