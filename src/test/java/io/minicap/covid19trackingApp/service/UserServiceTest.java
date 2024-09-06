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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @MockBean
    private UserRepository repo;

    @InjectMocks
    private UserService service;

    @Test
    public void userFoundByEmail() {
    //given an existent user's email
    String email = "Bulbasaur@gmail.com";
    User expected = new User();
    expected.setEmail(email);
    //when
    when(repo.findByEmail(email)).thenReturn(expected);
    User actual = service.findByEmail(email);
    //then
    assertEquals(expected, actual);
    verify(repo).findByEmail(email);
    }
    

    @Test
    public void userFoundByConfirmationToken()
    {
        //given an existent confirmation token for an existent user
        String token = "fb7964db-1f34-4054-ada1-2292b33f4dd6";
        User expected = new User();
        expected.setConfirmationToken(token);
        //when
        when(repo.findByConfirmationToken(token)).thenReturn(expected);
        User actual = service.findByConfirmationToken(token);
        //then
        assertEquals(expected, actual);
        verify(repo).findByConfirmationToken(token);
    }

    @Test
    public void userSaved() 
    {
        //given a NEW user
        User newUser = new User();
        String email = "DNE";
        newUser.setEmail(email);
        //when
        when(repo.save(newUser)).thenReturn(newUser);
        service.saveUser(newUser);
        //then
        verify(repo).save(newUser);
    }

    @Test
    public void userLoadedByUsername() 
    {
        //given an existent username
        String expected = "Bulbasaur@gmail.com";
        User user = new User();
        user.setEmail(expected);
        //when
        when(repo.findByEmail(expected)).thenReturn(user);
        String actual = service.loadUserByUsername(expected).getUsername();
        //then
        assertThat(actual).isEqualTo(expected);
        verify(repo).findByEmail(expected);
    }

    @Test
    public void loadingUserFailed()
    {
        //given a non-existent username
        String username = "DNE";
        //when
        when(repo.findByEmail(username)).thenReturn(null);
        //then
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(username));
        verify(repo).findByEmail(username);
    }
}
