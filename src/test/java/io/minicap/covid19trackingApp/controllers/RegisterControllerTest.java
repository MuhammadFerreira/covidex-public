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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
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
import io.minicap.covid19trackingApp.service.EmailService;
import io.minicap.covid19trackingApp.service.UserService;

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
@AutoConfigureMockMvc(addFilters = false)
public class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @Test
    public void contextLoads(){
        assertNotNull(bCryptPasswordEncoder);
        assertNotNull(userService);
        assertNotNull(emailService);
        assertNotNull(mockMvc);
    }
    @Test
    @WithMockUser(roles = {"ANONYMOUS","ADMINISTRATOR"})
    public void testShowRegistrationPage() throws Exception
    {
        
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("register"));
    }
    
    @Test
    public void testShowRegistrationPageNull() throws Exception
    {
        
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
            .andReturn();
    }
    
    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    public void testShowRegistrationPageAdminOnly() throws Exception
    {
        
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
            .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ANONYMOUS"})
    public void testShowRegistrationPageAnonymousOnly() throws Exception
    {
        
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
            .andReturn();
    }

    @Test
    @WithMockUser(roles = {"PATIENT","DOCTOR","GOVERNMENT"})
    public void testShowRegistrationPageUser() throws Exception
    {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("homepage"));
    }
    
    


    // @Test
    // @WithMockUser(username = "Ditto@gmail.com",roles = {"ADMINISTRATOR"})
    // public void testProcessRegistrationForm() throws Exception
    // {   
    //     User dummy = new User();
    //     dummy.setEmail("Ditto@hotmail.com");
    //     dummy.setUserRole(userRole.ADMINISTRATOR);
    //     Mockito.when(userService.findByEmail(dummy.getEmail())).thenReturn(dummy);
    //     mockMvc.perform(MockMvcRequestBuilders.post("/register"))
    //     .andExpect(MockMvcResultMatchers.status().isOk())
    //     .andReturn();
    //     Mockito.verify(userService).saveUser(dummy);;
    // }
    
    // @Test    
    // void invalidUserEmail() throws Exception
    // {
    //     BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    //     EmailService email = new EmailService(new JavaMailSenderImpl());
    // //    RegisterController controller =new RegisterController(new BCryptPasswordEncoder(),
    // //                           new UserService(new UserRepository()),new EmailService(new JavaMailSender())));
    //     ModelAndView model  = new ModelAndView("redirect:users");

        
    //     // RequestBuilder request = MockMvcRequestBuilders.post("/register");
    //     // MvcResult result = mvc.perform(request).param("asd","asd").andReturn();
    //     // assertEquals(, result);
    
    // }


}
