package io.minicap.covid19trackingApp.controllers;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.minicap.covid19trackingApp.appUsers.Administrator;
import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.gender;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.appUsers.userRole;
import io.minicap.covid19trackingApp.repository.AdminRepository;
import io.minicap.covid19trackingApp.repository.DoctorRepository;
import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.UserService;
import io.minicap.covid19trackingApp.service.statsService;

//Simple controller used specifically just for login
@Controller
public class AdminController {

    private UserService userService;
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    public AdminController( UserService userService) {
    
        this.userService = userService;
    }

    
    
    //Display Admin Dashboard
    @GetMapping("/adminhome")
    public ModelAndView showAdminHome(ModelAndView modelAndView, User user) throws IOException
    {
        //Set the view to adminDashboard.html
        modelAndView.setViewName("adminDashboard");

        //Initialize new List
        List<Patient> listUsers = new ArrayList<Patient>();

        //Grab all User objects from DB with role PATIENT
        listUsers= patientRepo.findAll();

        //Get count of how many patients
        int numberOfPatients = listUsers.size();
        modelAndView.addObject("numberOfPatients", numberOfPatients);

        //Get count of how many Doctors
        int numberOfDoctors= doctorRepo.findAll().size();
        modelAndView.addObject("numberOfDoctors", numberOfDoctors);

        //Get count of how many Gov Users
        int numberOfGov = userRepo.findAllByUserRole(userRole.GOVERNMENT).size();
        modelAndView.addObject("numberOfGov", numberOfGov);

        //Get count of how many patients
        int numberOfAdmins = userRepo.findAllByUserRole(userRole.ADMINISTRATOR).size();
        modelAndView.addObject("numberOfAdmins", numberOfAdmins);

        List <Patient> filteredList = listUsers.stream().filter(patient -> patient.getDoctor() == null).collect(Collectors.toList());

        //Get count of how many patients
        int numberOfPatientsIncoming = filteredList.size();
        modelAndView.addObject("numberOfPatientsIncoming", numberOfPatientsIncoming);


        //Get count for ever Infection Status
        int numberOfAsymptomatic = patientRepo.findAllByInfectionStatus(infectionStatus.asymptomatic).size();
        modelAndView.addObject("numberOfAsymptomatic", numberOfAsymptomatic);
        
        int numberOfSymptomatic = patientRepo.findAllByInfectionStatus(infectionStatus.symptomatic).size();
        modelAndView.addObject("numberOfSymptomatic", numberOfSymptomatic);
        
        int numberOfCritical = patientRepo.findAllByInfectionStatus(infectionStatus.critical).size();
        modelAndView.addObject("numberOfCritical", numberOfCritical);
        
        int numberOfContactTraced = patientRepo.findAllByInfectionStatus(infectionStatus.contactTraced).size();
        modelAndView.addObject("numberOfContactTraced", numberOfContactTraced);

        int numberOfHealthy = patientRepo.findAllByInfectionStatus(infectionStatus.none).size();
        modelAndView.addObject("numberOfHealthy", numberOfHealthy);

        //Get data from Online Sources about COVID-19
        modelAndView.addObject("newInfections", statsService.getCurrentCases());

        modelAndView.addObject("changeInHospitilizations", statsService.getChangeinHospitilizations());

        modelAndView.addObject("deaths", statsService.getDeaths());

        return modelAndView;   
    }
    
    //Return page with information about user
    @GetMapping("/adminhome/about-me")
    public ModelAndView aboutMe(ModelAndView modelAndView, User user) throws IOException
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User currUser = userRepo.findByEmail(auth.getName());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currUser.getDob());
        
        int birthYear = calendar.get(Calendar.YEAR);
        
        modelAndView.addObject("adminAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);
        
        if(!currUser.isEnabled())
        {
            return new ModelAndView("redirect:/adminhome/edit-profile");
        }

        modelAndView.addObject("admin", currUser);
        modelAndView.setViewName("adminAboutMe");

        return modelAndView;   
    }

    

    //Return Edit Profile Page
    @GetMapping("/adminhome/edit-profile")
    public ModelAndView editProfile(ModelAndView modelAndView, User user) throws IOException
    { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Administrator admin =  adminRepo.findByEmail(authentication.getName());

        modelAndView.addObject("admin", admin);

        //Set to right html page
        modelAndView.setViewName("adminAccountSettings");

        

        return modelAndView;   
    }

    // Retrieve user from DB and edit it. Then save edited user.
    @PostMapping("/adminhome/edit-profile")
    public ModelAndView editProfilePost(ModelAndView modelAndView, User user, @RequestParam gender gender, @RequestParam String address, @RequestParam String birthDate, @RequestParam String phoneNumber) throws IOException, ParseException
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Administrator admin = adminRepo.findByEmail(authentication.getName());

        admin.setDob(new SimpleDateFormat("yyyy/MM/dd").parse(birthDate.replace('-', '/')));

        admin.setPhoneNumber(phoneNumber);

        admin.setGender(gender);

        admin.setAddress(address);
 
        userService.saveUser(admin);

        modelAndView = new ModelAndView("redirect:/adminhome"); 
        
        modelAndView.addObject("confirmationMessage", "Administrator Profile Was Updated");

        return modelAndView;   
    }

    // Show list of patients to admin
    @GetMapping("/adminhome/patients")
    public ModelAndView showPatients(ModelAndView modelAndView, User user) throws IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Administrator admin = adminRepo.findByEmail(authentication.getName());

        modelAndView.addObject("admin", admin);

        modelAndView.setViewName("adminPatientList");

        List <Patient> patients = patientRepo.findAll();   
        modelAndView.addObject("listPatients", patients);

        List <Doctor> doctors = doctorRepo.findAll();
        modelAndView.addObject("listDoctors", doctors);

        return modelAndView;  
    }

    //Show list of doctors to admin
    @GetMapping("/adminhome/doctors")
    public ModelAndView showDoctors(ModelAndView modelAndView, User user) throws IOException
    {
        modelAndView.setViewName("doctorListAdministrator");

        List <Doctor> doctors = doctorRepo.findAll();
        modelAndView.addObject("listDoctors", doctors);
        modelAndView.addObject("patientRepo", patientRepo);

        return modelAndView;  
    }

    //Show list of all doctors to admin
    @GetMapping("/adminhome/users")
    public ModelAndView showUsers(ModelAndView modelAndView, User user) throws IOException
    {
        modelAndView.setViewName("userListAdministrator");

        List <User> users = userRepo.findAll();
        
        modelAndView.addObject("listUsers", users);

        return modelAndView;  
    }

    //Assign Patient to Doctor
    @GetMapping("/adminhome/assign")
    public ModelAndView assignUsers(ModelAndView modelAndView) throws IOException
    {
        

        List <Patient> patients = patientRepo.findAll();   
        modelAndView.addObject("listPatients", patients);


        List <Doctor> doctors = doctorRepo.findAll();
        modelAndView.addObject("listDoctors", doctors);
        modelAndView.setViewName("assignUsers");
        return modelAndView;  
    }

    // Functionality Used to Assign a Patient to a Doctor
    @PostMapping("/adminhome/patients")
    public ModelAndView processAssignUsers(ModelAndView modelAndView, @RequestParam String patientEmail, @RequestParam String doctorEmail) throws IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Administrator admin = adminRepo.findByEmail(authentication.getName());

        modelAndView.addObject("admin", admin);

        try
        {
            Patient patientInDB = patientRepo.getById(userRepo.findByEmail(patientEmail).getId());
            patientInDB.setDoctor(doctorRepo.getById(userRepo.findByEmail(doctorEmail).getId()));
            patientRepo.save(patientInDB);

        }
        catch(Exception e)
        {
            modelAndView =  new ModelAndView("redirect:/adminhome/patients");
            modelAndView.addObject("confirmationMessage", "Unable to Process Request");
            return modelAndView;
        }

        modelAndView =  new ModelAndView("redirect:/adminhome/patients");
        
        modelAndView.addObject("confirmationMessage", "Patient has Been Assigned");

        return modelAndView; 
    }


    // Functionality to flag a Patient
    @PostMapping("/adminhome/patients/flag")
    public ModelAndView flagPatients(ModelAndView modelAndView, @RequestParam String patientEmail){
       
        Patient patientInDB = patientRepo.getById(userRepo.findByEmail(patientEmail).getId());

        patientInDB.setIsFlagged(!patientInDB.getIsFlagged());
        patientRepo.save(patientInDB);

        modelAndView.addObject("confirmationMessage", "Patient status updated");

        return new ModelAndView("redirect:/adminhome/patients"); 
    }

    // Return page with information about a specific user
    @GetMapping("/adminhome/patients/{userId}")
    public ModelAndView showDoctorPatientStatus(ModelAndView modelAndView, User user, @PathVariable("userId") long id) throws IOException
    { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Administrator admin = adminRepo.findByEmail(authentication.getName());

        modelAndView.addObject("admin", admin);  
        Patient currentPatient = patientRepo.getById(id);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentPatient.getDob());
        int birthYear = calendar.get(Calendar.YEAR);
        
        modelAndView.addObject("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);
        modelAndView.addObject("patient", currentPatient);
        modelAndView.setViewName("adminPatientStatus");

        return modelAndView;    
    }

    // Functionality to Mark Doctor as Full
    @PostMapping("/adminhome/doctors/markFull")
    public ModelAndView markDoctorFull(ModelAndView modelAndView, @RequestParam String doctorEmail){
       
        Doctor doctorInDB = doctorRepo.getById(userRepo.findByEmail(doctorEmail).getId());

        doctorInDB.setIsFull(!doctorInDB.getIsFull());
        doctorRepo.save(doctorInDB);

        modelAndView =  new ModelAndView("redirect:/adminhome/doctors");

        modelAndView.addObject("confirmationMessage", "Doctor status updated");

        return modelAndView;
    }
  
}