package io.minicap.covid19trackingApp.service;

import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.config.CustomUserDetails;
import io.minicap.covid19trackingApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class EmailServiceTest {

    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void contextLoads()
    {
        assertNotNull(mailSender);
    }

    @Test
    public void testSendEmail()
    {
        
            SimpleMailMessage mail = new SimpleMailMessage();
             mail.setFrom("admin@spring.io");
             mail.setSubject("A new message for you");
             mail.setText("Hello GreenMail!");
             mail.setTo("test@greenmail.io");

             emailService = new EmailService(mailSender);
             emailService.sendEmail(mail);
        verify(mailSender).send(mail);
    }
    
}
