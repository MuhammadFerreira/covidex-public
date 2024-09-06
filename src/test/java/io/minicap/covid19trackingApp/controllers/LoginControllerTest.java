package io.minicap.covid19trackingApp.controllers;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.EmailService;
import io.minicap.covid19trackingApp.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    UserService userService;

    @MockBean
    EmailService emailService;

    @MockBean
    private UserRepository userRepo;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldCreateMvc()
    {
        assertNotNull(mockMvc);
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    public void testShowLoginPageAnonymous() throws Exception
    {
        //when the user is anonymous (didn't sign in yet), send them to login page
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login"));

    }

    @Test
    @WithMockUser(roles = {"DOCTOR", "ADMINISTRATOR", "PATIENT", "GOVERNMENT"})
    public void testShowLoginPageNotAnonymous() throws Exception
    {
        //when user already signed in, send them to homepage
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("homepage"));

        //check modelandview object to strengthen test
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    public void testShowForgetPasswordPageAnonymous() throws Exception
    {
        //when the user is anonymous (didn't sign in yet), send them to forgot password page
        mockMvc.perform(MockMvcRequestBuilders.get("/forgotpassword"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("forgotPassword"));      
    }

    @Test
    @WithMockUser(roles = {"DOCTOR", "ADMINISTRATOR", "PATIENT", "GOVERNMENT"})
    public void testShowForgetPasswordPageNotAnonymous() throws Exception
    {
        //when user already signed in, send them to homepage instead of forgot password page
        mockMvc.perform(MockMvcRequestBuilders.get("/forgotpassword"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("homepage"));

        //check modelandview object to strengthen test
    }

    @Test
    @WithMockUser(username = "Bulbasaur@gmail.com")
    public void testProcessForgetPasswordPage() throws Exception
    {
        //if the user doesn't exist in the DB
        String email = "Bulbasaur@gmail.com";
        User user = new User();
        //when
        when(userService.findByEmail(email)).thenReturn(null);
        //then send user to homepage
        mockMvc.perform(MockMvcRequestBuilders.post("/forgotpassword"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("homepage"))
            .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "A e-mail has been sent to " + user.getEmail()));
        
        /*------------------------------------------*/
        //if user exists but is not enabled
        user.setEmail(email);
        user.setEnabled(false);
        //when
        when(userService.findByEmail(email)).thenReturn(user);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/forgotpassword")
            .flashAttr("user", user))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "A e-mail has already been sent to this user"));

        /*------------------------------*/
        //if user exists and is enabled
        user.setEnabled(true);
        //when
        when(userService.findByEmail(email)).thenReturn(user);
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/forgotpassword")
            .flashAttr("user", user))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "A e-mail has been sent to " + user.getEmail()))
            .andExpect(MockMvcResultMatchers.view().name("homepage"));

    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR", username = "test")
    public void testShowHomePageAdmin() throws Exception
    {   
        User user = new User();
        user.setEnabled(true);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        //if the user is an admin, redirect to the admin homepage
        mockMvc.perform(MockMvcRequestBuilders.post("/home"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:adminhome"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "test")
    public void testShowHomePageDoctor() throws Exception
    {   
        User user = new User();
        user.setEnabled(true);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        //if the user is an doctor, redirect to the doctor homepage
        mockMvc.perform(MockMvcRequestBuilders.post("/home"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:doctorhome"));
    }

    @Test
    @WithMockUser(roles = "GOVERNMENT", username = "test")
    public void testShowHomePageGov() throws Exception
    {   
        User user = new User();
        user.setEnabled(true);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        //if the user is an GOV, redirect to the gov homepage
        mockMvc.perform(MockMvcRequestBuilders.post("/home"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:govhome"));
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "test")
    public void testShowHomePagePatient() throws Exception
    {
        User user = new User();
        user.setEnabled(true);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        //if the user is an patient, redirect to the patient homepage
        mockMvc.perform(MockMvcRequestBuilders.post("/home"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:patienthome"));
    }
    

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR", "PATIENT","GOVERNMENT","DOCTOR"},username = "test")
    public void testShowHomePageErrorAdmin() throws Exception
    {
        User user = new User();
        user.setEnabled(false);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        //if the user is an GOV, redirect to the gov homepage
        mockMvc.perform(MockMvcRequestBuilders.post("/home"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.view().name("redirect:adminhome/edit-profile"));
    }

    @Test
    @WithMockUser(roles = {"ANONYMOUS"},username = "test")
    public void testShowHomePageError() throws Exception
    {
        User user = new User();
        user.setEnabled(false);
        Mockito.when(userRepo.findByEmail("test")).thenReturn(user);
        //if the user is an GOV, redirect to the gov homepage
        mockMvc.perform(MockMvcRequestBuilders.post("/home"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("error"));
    }



    @Test
    public void testLogout() throws Exception 
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/logout"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("homepage"))
            .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "You have been logged out!"));
        //check ModelAndView changes to strengthen test
    }
  

}
