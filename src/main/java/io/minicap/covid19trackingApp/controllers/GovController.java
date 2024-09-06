package io.minicap.covid19trackingApp.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import io.minicap.covid19trackingApp.appUsers.Doctor;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.gender;
import io.minicap.covid19trackingApp.appUsers.govRole;
import io.minicap.covid19trackingApp.appUsers.governmentUser;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.appUsers.userRole;

import io.minicap.covid19trackingApp.repository.GovRepository;

import io.minicap.covid19trackingApp.repository.DoctorRepository;

import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.PatientService;
import io.minicap.covid19trackingApp.service.UserService;
import io.minicap.covid19trackingApp.service.statsService;

//Simple controller used specifically just for login
@Controller
public class GovController {

    private UserService userService; 

    private PatientService patientService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private GovRepository govRepo;

    @Autowired
    public GovController(UserService userService, PatientService patientService) {
    
        this.userService = userService;
        this.patientService = patientService;
    }

    @Autowired
    private DoctorRepository doctorRepo;



    //Show the Government Dashboard
    @GetMapping("/govhome")
    public ModelAndView showAdminHome(ModelAndView modelAndView, User user) throws IOException
    {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        governmentUser gov = govRepo.findByEmail(authentication.getName());

        modelAndView.addObject("gov", gov);

        //set view to right HTML
        modelAndView.setViewName("governmentDashboard");

        //Grab all patient from DB
        List<Patient> listPatients = patientRepo.findAll();
        
        //Get total number of vaccines administered to patients. Will soon be moved in new class
        int totalVaccines = 0;
        for(int i = 0; i < listPatients.size(); i++)
        {
            totalVaccines = listPatients.get(i).getNumOfDoses() + totalVaccines;  
        }
        
        //Get total number of patients 
        int numberOfPatients = listPatients.size();
        modelAndView.addObject("numberOfPatients", numberOfPatients);
       
        //Get average vaccines per person. Will soon be moved in new class
        double avgVaccine = (double) totalVaccines/ (double)numberOfPatients;

        String avgVaccines = Double.toString(avgVaccine).substring(0, 3); 
        modelAndView.addObject("avgDoses", avgVaccines);
        
        //Get the amount of users with specific dose number
        int noDose = patientRepo.findAllBynumOfDoses(0).size();
        modelAndView.addObject("noDose", noDose);

        int oneDose = patientRepo.findAllBynumOfDoses(1).size();
        modelAndView.addObject("oneDose", oneDose);
        
        int twoDoses = patientRepo.findAllBynumOfDoses(2).size();
        modelAndView.addObject("twoDoses", twoDoses);
        
        int threeDoses = patientRepo.findAllBynumOfDoses(3).size();
        modelAndView.addObject("threeDoses", threeDoses);

        //Get total number of doctors in the DB
        int numberOfDoctors = userRepo.findAllByUserRole(userRole.DOCTOR).size();
        modelAndView.addObject("numberOfDoctors", numberOfDoctors);

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

    

    //Return page with Information about user
    @GetMapping("/govhome/about-me")
    public ModelAndView viewProfile(ModelAndView modelAndView, User user) throws IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        governmentUser gov = govRepo.findByEmail(authentication.getName());

        modelAndView.addObject("gov", gov);

        //Set to right html page
        modelAndView.setViewName("govAboutMe");
        return modelAndView;   
    }

    //Return Page used to Edit Profile
    @GetMapping("/govhome/edit-profile")
    public ModelAndView editProfile(ModelAndView modelAndView, User user) throws IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        governmentUser gov =  govRepo.findByEmail(authentication.getName());

        modelAndView.addObject("gov", gov);
        
        //Set to right html page
        modelAndView.setViewName("governmentAccountSettings");
        return modelAndView;   
    }

    //Save changes to profile 
    @PostMapping("/govhome/edit-profile")
    public ModelAndView editProfilePost(ModelAndView modelAndView, User user, @RequestParam gender gender, @RequestParam String address, @RequestParam String birthDate, @RequestParam govRole govRole, @RequestParam String phoneNumber) throws IOException, ParseException
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        governmentUser gov = govRepo.findByEmail(authentication.getName());

        gov.setDob(new SimpleDateFormat("yyyy/MM/dd").parse(birthDate.replace('-', '/')));
        gov.setGender(gender);
        gov.setAddress(address);
        gov.setGovRole(govRole);
        gov.setPhoneNumber(phoneNumber);
        userService.saveUser(gov);

        modelAndView = new ModelAndView("redirect:/govhome"); 
        modelAndView.addObject("confirmationMessage", "Government Profile Was Updated");

        return modelAndView;   
    }

    //Get information about specific Patients
    @GetMapping("/govhome/patients/{userId}")
    public ModelAndView showDoctorPatientStatus(ModelAndView modelAndView, User user, HttpServletRequest request, @PathVariable("userId") long id, RedirectAttributes redirectAttributes) throws IOException
    {
        Patient currentPatient = patientRepo.getById(id);
        String referer = request.getHeader("Referer");

        if(currentPatient.getEnabled() == false)
        {
            redirectAttributes.addFlashAttribute("error",  currentPatient.getFirstName() + " has not been enabled."); 
            return new ModelAndView("redirect:"+referer); 
        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(currentPatient.getDob());

        int birthYear = calendar.get(Calendar.YEAR);
        
        modelAndView.addObject("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);
        modelAndView.addObject("patient", currentPatient);
        modelAndView.setViewName("govPatientStatus");

        return modelAndView;    
    }
    
    //Show list of patients to government official
    @GetMapping("/govhome/patients")
    public ModelAndView showPatients(ModelAndView modelAndView, User user) throws IOException
    {
        
        // Set view to govPatientList.html
        modelAndView.setViewName("govPatientList");

        List <Patient> patients = patientRepo.findAll();   
        modelAndView.addObject("listPatients", patients);

        List <Doctor> doctors = doctorRepo.findAll();
        modelAndView.addObject("listDoctors", doctors);

        return modelAndView;  
    }

    // Functionality to flag Patients
    @PostMapping("/govhome/patients/flag")
    public ModelAndView flagPatients(ModelAndView modelAndView, @RequestParam String patientEmail){

        Patient patientInDB = patientRepo.getById(userRepo.findByEmail(patientEmail).getId());

        patientInDB.setIsFlagged(!patientInDB.getIsFlagged());
        patientRepo.save(patientInDB);

        modelAndView =  new ModelAndView("redirect:/govhome/patients");

        modelAndView.addObject("confirmationMessage", "Patient status updated");

        return modelAndView;
    }

    @GetMapping("/govhome/contact-trace/{userId}")
    public ModelAndView getContactTrace(ModelAndView modelAndView, User user, @PathVariable("userId") long id) throws IOException
    {
        
        // Set view to govPatientList.html
        modelAndView.setViewName("contactTraceGov");

        long num = id;

        Patient test = patientRepo.getById(num);
        
        JSONObject elements = patientService.produceJSONforNetworkGraph(test);

        modelAndView.addObject("elements", elements.toString());

        modelAndView.addObject("url", "/govhome/");

        

        return modelAndView;  
    }

    // Add functionality to flag a patient
    @GetMapping("/govhome/flag/{userId}")
    public ModelAndView flagPatients(ModelAndView modelAndView, HttpServletRequest request, @PathVariable("userId") long id, RedirectAttributes redirectAttributes){

        String referer = request.getHeader("Referer");

        Patient patientInDB = patientRepo.getById(id);

        if(patientInDB.getEnabled() == false)
        {
            redirectAttributes.addFlashAttribute("error", patientInDB.getFirstName() + " has not been enabled.");  
            return new ModelAndView("redirect:"+referer); 
        }

        patientInDB.setIsFlagged(!patientInDB.getIsFlagged());
        patientRepo.save(patientInDB);

        if(patientInDB.getIsFlagged())
        {
          redirectAttributes.addFlashAttribute("success", patientInDB.getFirstName() + " was flagged.");  
        }
        else
        {
            redirectAttributes.addFlashAttribute("success", patientInDB.getFirstName() + " was unflagged.");  
        }
     
        return new ModelAndView("redirect:"+referer); 
    }
}