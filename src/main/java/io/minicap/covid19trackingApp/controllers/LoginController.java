package io.minicap.covid19trackingApp.controllers;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.EmailService;
import io.minicap.covid19trackingApp.service.UserService;

//Simple controller used specifically just for login
@Controller
public class LoginController {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private EmailService emailService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    public LoginController(BCryptPasswordEncoder bCryptPasswordEncoder,
                              UserService userService, EmailService emailService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    //Show LogIn Page
    @GetMapping("/login")
    public ModelAndView showLoginPage(ModelAndView modelAndView, User user)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))){
            modelAndView.setViewName("homepage");
            modelAndView.addObject("confirmationMessage", "There is already someone signed in!");
            return modelAndView;
        }

        modelAndView.setViewName("login");
        

        return modelAndView;
        
    }

    //Show Forget Password Page
    @GetMapping("/forgotpassword")
    public ModelAndView showForgetPasswordPage(ModelAndView modelAndView, User user)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))){
            modelAndView.setViewName("homepage");
            modelAndView.addObject("confirmationMessage", "There is already someone signed in!");
            return modelAndView;
        }

        modelAndView.setViewName("forgotPassword");

        return modelAndView;
        
    }

    // Process forget password page
    @PostMapping("/forgotpassword")
    public ModelAndView processForgetPasswordPage(ModelAndView modelAndView, User user,  HttpServletRequest request)
    {

        // Lookup user in database by e-mail
        User userInDB = userService.findByEmail(user.getEmail());

        //if this user does not exist
        if(userInDB == null)
        {
            //Confirm that the email has been sent
            modelAndView.addObject("confirmationMessage", "A e-mail has been sent to " + user.getEmail());
            modelAndView.setViewName("homepage");

            return modelAndView;   
        }

        //if this user does not exist
        if(!userInDB.getEnabled())
        {
            //Confirm that the email has been sent
            modelAndView.addObject("confirmationMessage", "A e-mail has already been sent to this user");
            
            return modelAndView;
            
        }

        // Disable user until they click on confirmation link in email
        userInDB.setEnabled(false);

        // Generate random 36-character string token for confirmation link
        String newToken = UUID.randomUUID().toString();
        userInDB.setConfirmationToken(newToken);
        
        //Save user to database
        userService.saveUser(userInDB);

        //Set URL for Registration
        String appUrl = request.getScheme() + "://" + request.getServerName();


        SimpleMailMessage registrationEmail = new SimpleMailMessage();
            
        registrationEmail.setTo(userInDB.getEmail());
            
        //Subject of Email
        registrationEmail.setSubject("Reset Password");
            
            //Email body
            registrationEmail.setText("To change your password, please click the link below:\n"
                    + appUrl + "/confirm?token=" + newToken);
            
            //Email sender
            registrationEmail.setFrom("noreply@domain.com");

            //Send the email
            emailService.sendEmail(registrationEmail);

            //Confirm that the email has been sent
            modelAndView.addObject("confirmationMessage", "A e-mail has been sent to " + userInDB.getEmail());
            
            //Set REGISTER page
            modelAndView.setViewName("homepage");

        return modelAndView;
        
    }

    //Redirect to right page based on user authority
    @PostMapping("/home")
    @ResponseBody
    public ModelAndView showHomePage(ModelAndView modelAndView, HttpServletRequest request)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currUser = userRepo.findByEmail(auth.getName());
        
        if(currUser.isEnabled())
        {
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
                return new ModelAndView("redirect:adminhome");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR")))
                return new ModelAndView("redirect:doctorhome");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GOVERNMENT")))
                return new ModelAndView("redirect:govhome");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT")))
                return new ModelAndView("redirect:patienthome");
        }
        else
        {
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
                return new ModelAndView("redirect:adminhome/edit-profile");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR")))
                return new ModelAndView("redirect:doctorhome/edit-profile");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GOVERNMENT")))
                return new ModelAndView("redirect:govhome/edit-profile");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT")))
                return new ModelAndView("redirect:/patienthome/edit-profile");   
        }
        modelAndView.setViewName("error");

        return modelAndView;   
    }

    //Redirect to right page based on user authority
    @GetMapping("/home")
    public ModelAndView showHome(ModelAndView modelAndView, HttpServletRequest request)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currUser = userRepo.findByEmail(auth.getName());
        
        if(currUser.isEnabled())
        {
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
                return new ModelAndView("redirect:adminhome");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR")))
                return new ModelAndView("redirect:doctorhome");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GOVERNMENT")))
                return new ModelAndView("redirect:govhome");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT")))
                return new ModelAndView("redirect:patienthome");
        }
        else
        {
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
                return new ModelAndView("redirect:adminhome/edit-profile");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR")))
                return new ModelAndView("redirect:doctorhome/edit-profile");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GOVERNMENT")))
                return new ModelAndView("redirect:govhome/edit-profile");

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT")))
                return new ModelAndView("redirect:/patienthome/edit-profile");   
        }
        modelAndView.setViewName("error");

        return modelAndView;   
    }

    // User logout 
    @GetMapping("/logout")
    public ModelAndView showLogoutPage(ModelAndView modelAndView, User user)
    {
        modelAndView.setViewName("homepage");

        modelAndView.addObject("confirmationMessage", "You have been logged out!");

        return modelAndView;
        
    }  
}