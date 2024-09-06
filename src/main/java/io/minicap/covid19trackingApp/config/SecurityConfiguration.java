package io.minicap.covid19trackingApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.minicap.covid19trackingApp.service.UserService;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
     
    //UserService() will be used for the username
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }
     
    //BryptPasswordEncoder will be used for the password decryption
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    //Here we set our authentication provider details
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    //We overide the configure method to allow us to use our custom details for login
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(authenticationProvider());
    }

    //We configure HTML security for the different webpages here
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/register").permitAll()
                .antMatchers("/confirm").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/adminhome").hasRole("ADMINISTRATOR")
                .antMatchers("/patienthome").hasRole("PATIENT")
                .antMatchers("/govhome").hasRole("GOVERNMENT")
                .antMatchers("/doctorhome").hasRole("DOCTOR")
                .and()
                .formLogin().loginPage("/login").usernameParameter("email").successForwardUrl("/home").permitAll()
                .and()
                .logout().logoutSuccessUrl("/logout").permitAll();
    }

    
}



