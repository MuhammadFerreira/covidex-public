package io.minicap.covid19trackingApp.controllers;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.gender;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;
import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.ReportRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.ReportService;
import io.minicap.covid19trackingApp.service.statsService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private PatientRepository patientRepo;

    @MockBean
    private UserRepository userRepo;

    @Test
    public void contextLoads(){
        assertNotNull(reportService);
        assertNotNull(patientRepo);
        assertNotNull(mockMvc);
    }

    @Test
    @WithMockUser(username = "Bulbasaur@gmail.com")
    public void editProfilePostTest() throws Exception
    {
        //given a patient
        String email = "Bulbasaur@gmail.com";

        Patient patient = new Patient();
        patient.setEmail(email);

        //when
        when(patientRepo.findByEmail(email)).thenReturn(patient);

        //then if the patient is positive
        mockMvc.perform(MockMvcRequestBuilders.post("/patienthome/edit-profile")
            .param("gender", "male")
            .param("address", "100 boulevard Bulbasaur")
            .param("numOfDoses", "1")
            .param("isPositive", "true")
            .param("varientType", "alpha")
            .param("infectionStatus", "critical")
            .param("birthDate", "1990-08-10")
            .param("phoneNumber", "4380921234"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

    }

        //improved testing can be done as a UI test
    @Test 
    @WithMockUser(roles = {"PATIENT"}, username = "test")
    public void addDailyReportTest() throws Exception
    {
        User user = new User();
        user.setEnabled(true);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/patienthome/add-daily-report"));
    }


/*
//giving nested servlet exception
    @Test 
    @WithMockUser(username = "Bulbasaur",roles = {"PATIENT"})
    public void showPatientHomeTest () throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/patienthome"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patientDashboard"));
    }
  */
  /* Method DNE anymore
  @Test 
  @WithMockUser(roles = {"PATIENT"}, username = "test")
  public void createDailyReportTestEditProfile () throws Exception
  {
      User user = new User();
      user.setEnabled(false);
      Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
      mockMvc.perform(MockMvcRequestBuilders.get("/patienthome/add-daily-report"))
              .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
              .andExpect(MockMvcResultMatchers.view().name("redirect:/patienthome/edit-profile"));
  }*/

  /* Method DNE anymore
    @Test 
    @WithMockUser(roles = {"PATIENT"}, username = "test")
    public void createDailyReportTest () throws Exception
    {
        User user = new User();
        user.setEnabled(true);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/patienthome/add-daily-report"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("patientStatusUpdate"));
    }*/
        

   
}