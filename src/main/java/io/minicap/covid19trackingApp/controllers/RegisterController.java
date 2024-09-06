package io.minicap.covid19trackingApp.controllers;

import io.minicap.covid19trackingApp.appUsers.Administrator;
import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.governmentUser;
import io.minicap.covid19trackingApp.appUsers.userRole;
import io.minicap.covid19trackingApp.repository.DoctorRepository;
import io.minicap.covid19trackingApp.service.EmailService;
import io.minicap.covid19trackingApp.service.UserService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

//Controller used for all processes involving registration. This includes initial registration -> email user with token -> creating password
@Controller
public class RegisterController {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private EmailService emailService;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    public RegisterController(BCryptPasswordEncoder bCryptPasswordEncoder,
            UserService userService, EmailService emailService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return null;

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                && !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
            modelAndView.setViewName("homepage");
            modelAndView.addObject("confirmationMessage", "There is already someone signed in!");
            return modelAndView;
        }
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Lookup user in database by e-mail
        User userInDB = userService.findByEmail(user.getEmail());

        User newUser;

        if (userInDB != null) 
        {
            modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.");
            modelAndView.setViewName("register");
            bindingResult.reject("email");
        }
        if (bindingResult.hasErrors()) 
        {
            modelAndView.setViewName("register");
        } 
        else // new user so we create user and send confirmation e-mail
        { 

            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
            {
                //Admin can choose registration type
                userRole role = user.getUserRole();

                switch(role)
                {
                    case PATIENT:
                        newUser = new Patient(user);
                        break;
                    case ADMINISTRATOR:
                        newUser = new Administrator(user);
                        break;
                    case DOCTOR:
                        newUser = new Doctor(user);
                        break;
                    case GOVERNMENT:
                        newUser = new governmentUser(user);
                        break;
                    default:
                        newUser = new Patient(user);
                }
                
                
                newUser.setUserRole(user.getUserRole());
            }
            else
            {
                //Defaults to Patient
                newUser = new Patient(user);
                newUser.setUserRole(userRole.PATIENT);
            }
            
            // Disable user until they click on confirmation link in email
            newUser.setEnabled(false);

            // Generate random 36-character string token for confirmation link
            newUser.setConfirmationToken(UUID.randomUUID().toString());
            
            //Save user to database
            userService.saveUser(newUser);

            //Set URL for Registration
            String appUrl = request.getScheme() + "://" + request.getServerName();


            SimpleMailMessage registrationEmail = new SimpleMailMessage();
            
            registrationEmail.setTo(newUser.getEmail());
            
            //Subject of Email
            registrationEmail.setSubject("Registration Confirmation");
            
            //Email body
            registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
                    + appUrl + "/confirm?token=" + newUser.getConfirmationToken());
            
            //Email sender
            registrationEmail.setFrom("noreply@domain.com");

            //Send the email
            emailService.sendEmail(registrationEmail);

            //Confirm that the email has been sent
            modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + newUser.getEmail());
            
            //Set REGISTER page
            modelAndView.setViewName("homepage");
        }

        return modelAndView;
    }

    // Process confirmation link
    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(ModelAndView modelAndView, @RequestParam("token") String token) {

        User user = userService.findByConfirmationToken(token);

        // No Such User with this token
        if (user == null) {
            modelAndView.addObject("errormessage", "Oops! Invalid Token.");
            modelAndView.setViewName("error");
            return modelAndView;
        }
        if (user.getEnabled()) {
            modelAndView.addObject("errormessage", "Oops! Invalid Token.");
            modelAndView.setViewName("error");
            return modelAndView;
        } else { // Token found
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }

        modelAndView.setViewName("confirm");
        return modelAndView;
    }

    // Process confirmation link
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public ModelAndView confirmRegistration(ModelAndView modelAndView, BindingResult bindingResult,
            @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {

        modelAndView.setViewName("confirm");

        
        // Check if passwords match
        if (!requestParams.get("password").equals(requestParams.get("ConfirmPassword"))) {
            bindingResult.reject("password");
            redir.addFlashAttribute("errorMessage", "Passwords do not match.");
            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
            return modelAndView;
        }

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure(requestParams.get("password"));

        // check if password is strong enough
        if (strength.getScore() < 2) {

            bindingResult.reject("password");

            redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");

            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
            return modelAndView;
        }

        // Find the user associated with the reset token
        User user = userService.findByConfirmationToken(requestParams.get("token"));

        // Set new password
        user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));

        // Set user to enabled
        user.setEnabled(true);

        // Save user
        userService.saveUser(user);

        modelAndView.setViewName("login");

        modelAndView.addObject("successMessage", "Your password has been set! You can now log in!");
        return modelAndView;
    }

}