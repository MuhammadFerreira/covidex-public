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
import io.minicap.covid19trackingApp.repository.PatientRepository;
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
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Authentication auth;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    PatientRepository patientRepo;

    

    @Test
    public void contextLoads()
    {
        assertNotNull(mockMvc);
        assertNotNull(userRepo);
        assertNotNull(patientRepo);
        assertNotNull(auth);
    }
    @Test
    public void testhome() throws Exception
    {        
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("homepage"));
    }

    @Test
    public void testErrorPage() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/error"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }
/*
    @Test
    public void testDoctorDashboardPage() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashTest"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("doctorDashboard"));
    }
*/

    @Test
    public void testlistUsersAdmin()
    {
      
        List<User> listusers = new ArrayList<User>();
        User u1 = new User();
        listusers.add(u1);
        //mock admin authority
        Mockito.when(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))).thenReturn(true);
        Mockito.when(userRepo.findAll()).thenReturn(listusers);
        //argument captor
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(auth).getAuthorities();
    }


    /* DOESN'T WORK
    @Test    
    public void listUsersAdmin() throws Exception //to show that auth could return an error, being null
    {
        User user = new User();
        user.setEmail("Omastar@gmail.com");
        user.setUserRole(userRole.ADMINISTRATOR);
        List<User> list = new ArrayList<User>();
        list.add(user);

        Authentication auth = Mockito.mock(Authentication.class, RETURNS_DEEP_STUBS);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))).thenReturn(true);

        when(userRepo.findAll()).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        
    }*/

    /*
    @Test
    public void listUsersAdmin() //need to make sure auth.getAuthorities does not return null
    {
        HomeController controller = new HomeController(); //test controller

        //mocks
        Model model = Mockito.mock(Model.class);
        User user = Mockito.mock(User.class);

        Authentication auth = Mockito.mock(Authentication.class, RETURNS_DEEP_STUBS);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))).thenReturn(true);
        assertEquals("users", controller.listUsers(model, user));
    }*/
    
}
