package io.minicap.covid19trackingApp.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.userRole;

public class CustomUserDetailsTest {


    @Test
    public void testShouldReturnAuthorities() {

        //expect the authority of administrator
        ArrayList authorityList = new ArrayList<SimpleGrantedAuthority>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        Collection<? extends GrantedAuthority> expectedAuthorities = authorityList;

        //mock the behavior of getUserRole when performed in the method
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getUserRole()).thenReturn(userRole.ADMINISTRATOR);

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);

        //should return the expected authority; administrator
        assertEquals(expectedAuthorities, userDetails.getAuthorities());
    }
}
