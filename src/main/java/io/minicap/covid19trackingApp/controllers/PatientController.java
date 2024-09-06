package io.minicap.covid19trackingApp.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.gender;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.appUsers.userRole;
import io.minicap.covid19trackingApp.appUsers.varientType;
import io.minicap.covid19trackingApp.appointment.Appointment;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;
import io.minicap.covid19trackingApp.repository.AppointmentRepository;
import io.minicap.covid19trackingApp.repository.DoctorRepository;
import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.ReportRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.EmailService;
import io.minicap.covid19trackingApp.service.ReportService;
import io.minicap.covid19trackingApp.service.UserService;
import io.minicap.covid19trackingApp.service.statsService;

//Simple controller used specifically just for login
@Controller
public class PatientController {

    private UserService userService;

    private ReportService reportService;

    private EmailService emailService;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    public PatientController(ReportService reportService, UserService userService, EmailService emailService) {
        this.reportService = reportService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public JSONArray appointmentsToJSON(List<Appointment> appointments) {

        JSONArray jsonAppointments = new JSONArray();

        for (Appointment a : appointments) {
            JSONObject obj = new JSONObject();

            obj.put("start", a.getStart());
            obj.put("end", a.getStart());

            jsonAppointments.put(obj);

        }

        return jsonAppointments;
    }

    // show patient right html page
    @GetMapping("/patienthome")
    public ModelAndView showPatientHome(ModelAndView modelAndView, User user, HttpServletRequest request)
            throws IOException, ParseException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Patient currentPatient = patientRepo.findByEmail(auth.getName());
        JSONObject reportDetails;

        List<Appointment> appointments = appointmentRepository.findByDoctor(currentPatient.getDoctor());

        if (currentPatient.getDoctor() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            modelAndView.addObject("appointments", "");
        } else if (currentPatient.getDoctor().getAvailability() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            appointments = appointmentRepository.findByDoctor(currentPatient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());
        } else {
            modelAndView.addObject("doctorAvailability", currentPatient.getDoctor().getAvailability());
            appointments = appointmentRepository.findByDoctor(currentPatient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());

        }

        try {
            reportDetails = new JSONObject(currentPatient.getReportDetailsJSON());
        } catch (Exception e) {
            reportDetails = null;
        }

        if (reportDetails != null) {
            modelAndView.addObject("temperature",
                    Boolean.parseBoolean(reportDetails.get("temperature").toString().substring(1, 5)));
            modelAndView.addObject("weight",
                    Boolean.parseBoolean(reportDetails.get("weight").toString().substring(1, 5)));

            modelAndView.addObject("fever",
                    Boolean.parseBoolean(reportDetails.get("fever").toString().substring(1, 5)));
            modelAndView.addObject("sneezing",
                    Boolean.parseBoolean(reportDetails.get("sneezing").toString().substring(1, 5)));
            modelAndView.addObject("fatigue",
                    Boolean.parseBoolean(reportDetails.get("fatigue").toString().substring(1, 5)));
            modelAndView.addObject("cough",
                    Boolean.parseBoolean(reportDetails.get("cough").toString().substring(1, 5)));
            modelAndView.addObject("diarrhea",
                    Boolean.parseBoolean(reportDetails.get("diarrhea").toString().substring(1, 5)));
            modelAndView.addObject("headaches",
                    Boolean.parseBoolean(reportDetails.get("headaches").toString().substring(1, 5)));
            modelAndView.addObject("runnyNose",
                    Boolean.parseBoolean(reportDetails.get("runnyNose").toString().substring(1, 5)));
            modelAndView.addObject("soreThroat",
                    Boolean.parseBoolean(reportDetails.get("soreThroat").toString().substring(1, 5)));
            modelAndView.addObject("lossOfTaste",
                    Boolean.parseBoolean(reportDetails.get("lossOfTaste").toString().substring(1, 5)));
            modelAndView.addObject("shortnessOfBreath",
                    Boolean.parseBoolean(reportDetails.get("shortnessOfBreath").toString().substring(1, 5)));
            modelAndView.addObject("lossOfSmell",
                    Boolean.parseBoolean(reportDetails.get("lossOfSmell").toString().substring(1, 5)));
            modelAndView.addObject("chestPains",
                    Boolean.parseBoolean(reportDetails.get("chestPains").toString().substring(1, 5)));

        } else {
            modelAndView.addObject("temperature", true);
            modelAndView.addObject("weight", true);

            modelAndView.addObject("fever", true);
            modelAndView.addObject("sneezing", true);
            modelAndView.addObject("fatigue", true);
            modelAndView.addObject("cough", true);
            modelAndView.addObject("diarrhea", true);
            modelAndView.addObject("headaches", true);
            modelAndView.addObject("runnyNose", true);
            modelAndView.addObject("soreThroat", true);
            modelAndView.addObject("lossOfTaste", true);
            modelAndView.addObject("shortnessOfBreath", true);
            modelAndView.addObject("lossOfSmell", true);
            modelAndView.addObject("chestPains", true);

        }

        List<dailyReport> reports = reportRepository.findAllByPatient(currentPatient);
        Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        Date today = formatter.parse(formatter.format(new Date()));

        if (getReportofDate(reports, today) != null) {
            modelAndView.addObject("recentReport", getReportofDate(reports, today));
            modelAndView.addObject("hasSubmittedReport", true);
        } else {
            modelAndView.addObject("recentReport", new dailyReport());
            modelAndView.addObject("hasSubmittedReport", false);
        }

        User currUser = userRepo.findByEmail(auth.getName());

        dailyReport recentReport;

        if (!currUser.isEnabled()) {
            currentPatient.setDoctor(doctorRepo.getById((long) 10));
            userService.saveUser(currentPatient);
            return new ModelAndView("redirect:/patienthome/edit-profile");
        }

        // Set to right html page
        modelAndView.setViewName("patientDashboard");

        String lastTenReportsDate = "";
        String lastTenReportsRating = "";

        for (int i = 0; i < 10; i++) {
            try {
                dailyReport temp = reports.get(reports.size() - 1 - i);

                lastTenReportsDate = "\"" + temp.getDate() + "\"," + lastTenReportsDate;
                lastTenReportsRating = temp.getStatusRating() + "," + lastTenReportsRating;

            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        try {
            lastTenReportsDate = lastTenReportsDate.substring(0, lastTenReportsDate.length() - 1);
            lastTenReportsRating = lastTenReportsRating.substring(0, lastTenReportsRating.length() - 1);

        } catch (IndexOutOfBoundsException e) {

        }

        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);

        try {
            recentReport = reports.get(reports.size() - 1);
            modelAndView.addObject("report", recentReport);
            modelAndView.addObject("noReports", false);
            modelAndView.addObject("hasFever", recentReport.getSymptoms().contains(",Fever"));
            modelAndView.addObject("hasSneezing", recentReport.getSymptoms().contains(",Sneezing"));
            modelAndView.addObject("hasFatigue", recentReport.getSymptoms().contains(",Fatigue"));
            modelAndView.addObject("hasCough", recentReport.getSymptoms().contains(",Cough"));
            modelAndView.addObject("hasDiarrhea", recentReport.getSymptoms().contains(",Diarrhea"));
            modelAndView.addObject("hasHeadaches", recentReport.getSymptoms().contains(",Headaches"));
            modelAndView.addObject("hasRunnyNose", recentReport.getSymptoms().contains(",Runny Nose"));
            modelAndView.addObject("hasSoreThroat", recentReport.getSymptoms().contains(",Sore Throat"));
            modelAndView.addObject("hasLossOfTaste", recentReport.getSymptoms().contains(",Loss of Taste"));
            modelAndView.addObject("hasShortnessOfBreath", recentReport.getSymptoms().contains(",Shortness of Breath"));
            modelAndView.addObject("hasLossOfSmell", recentReport.getSymptoms().contains(",Loss of Smell"));
            modelAndView.addObject("hasChestPains", recentReport.getSymptoms().contains(",Chest Pains"));
        } catch (IndexOutOfBoundsException e) {
            modelAndView.addObject("report", new dailyReport());
            modelAndView.addObject("noReports", true);
        }

        if (currentPatient.getDoctor() == null) {
            modelAndView.addObject("doctor", "Not Assigned");
        } else {
            modelAndView.addObject("doctor", currentPatient.getDoctor().toString());
        }

        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);
        modelAndView.addObject("listSymptoms", new ArrayList<String>());

        modelAndView.addObject("patient", currentPatient);

        // Get data from Online Sources about COVID-19
        modelAndView.addObject("newInfections", statsService.getCurrentCases());
        modelAndView.addObject("changeInHospitilizations", statsService.getChangeinHospitilizations());
        modelAndView.addObject("deaths", statsService.getDeaths());

        // Set URL for Registration
        String appUrl = request.getScheme() + "://" + request.getServerName();

        modelAndView.addObject("url", appUrl);

        return modelAndView;
    }

    @GetMapping("/patienthome/edit-profile")
    public ModelAndView editProfile(ModelAndView modelAndView, User user) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Patient patient = patientRepo.findByEmail(authentication.getName());

        modelAndView.addObject("patient", patient);

        if (!patient.isEnabled()) {
            modelAndView.addObject("message", "Please Fill Out This Form To Access Your Dashboard!");
            modelAndView.addObject("patient", new Patient());
        } else {
            modelAndView.addObject("message", "Account Settings");
            modelAndView.addObject("patient", patient);
        }

        // Retrieve Appointment
        List<Appointment> appointments = appointmentRepository.findByDoctor(patient.getDoctor());

        if (patient.getDoctor() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            modelAndView.addObject("appointments", "");
        } else if (patient.getDoctor().getAvailability() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            appointments = appointmentRepository.findByDoctor(patient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());
        } else {
            modelAndView.addObject("doctorAvailability", patient.getDoctor().getAvailability());
            appointments = appointmentRepository.findByDoctor(patient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());

        }

        // Set to right html page
        modelAndView.setViewName("patientAccountSettings");
        return modelAndView;
    }

    @PostMapping("/patienthome/edit-profile")
    public ModelAndView editProfilePost(ModelAndView modelAndView, User user, @RequestParam gender gender,
            @RequestParam String address, @RequestParam int numOfDoses, @RequestParam boolean isPositive,
            @RequestParam varientType varientType, @RequestParam infectionStatus infectionStatus,
            @RequestParam String birthDate, @RequestParam String phoneNumber, RedirectAttributes redirectAttributes)
            throws IOException, ParseException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Patient patient = patientRepo.findByEmail(authentication.getName());

        patient.setDob(new SimpleDateFormat("yyyy/MM/dd").parse(birthDate.replace('-', '/')));

        patient.setGender(gender);

        patient.setAddress(address);

        patient.setNumOfDoses(numOfDoses);

        patient.setPhoneNumber(phoneNumber);

        userService.saveUser(patient);

        if (isPositive) {
            patient.setIsPositive(isPositive);
            patient.setVarientType(varientType);
            patient.setInfectionStatus(infectionStatus);
            patient.setEnabled(true);
            userService.saveUser(patient);
            modelAndView = new ModelAndView("redirect:/patienthome/contact-trace-report");
            return modelAndView;
        } else {
            patient.setIsPositive(false);
            patient.setVarientType(io.minicap.covid19trackingApp.appUsers.varientType.none);
            patient.setInfectionStatus(io.minicap.covid19trackingApp.appUsers.infectionStatus.none);
        }

        patient.setEnabled(true);

        userService.saveUser(patient);

        modelAndView = new ModelAndView("redirect:/patienthome");

        redirectAttributes.addFlashAttribute("success", "Profile Was Updated");

        return modelAndView;
    }

    // Mapping for Contract Trace Form Page
    // Get Mapping Contact Trace Report
    @GetMapping("/patienthome/contact-trace-report")
    public ModelAndView contractTraceReport(ModelAndView modelAndView, User user) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Patient patient = patientRepo.findByEmail(authentication.getName());

        List<Patient> contactTracedPatients = patient.getContactTraceTo();

        modelAndView.addObject("contactTracedPatients", contactTracedPatients);

        modelAndView.addObject("patient", patient);

        // Retrieve Appointment
        List<Appointment> appointments = appointmentRepository.findByDoctor(patient.getDoctor());

        if (patient.getDoctor() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            modelAndView.addObject("appointments", "");
        } else if (patient.getDoctor().getAvailability() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            appointments = appointmentRepository.findByDoctor(patient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());
        } else {
            modelAndView.addObject("doctorAvailability", patient.getDoctor().getAvailability());
            appointments = appointmentRepository.findByDoctor(patient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());

        }

        // Set to right html page
        modelAndView.setViewName("patientContactTraceForm");
        return modelAndView;
    }

    // Post Mapping Contract Trace Report
    @PostMapping("/patienthome/contact-trace-report")
    public ModelAndView contractTraceReportPost(ModelAndView modelAndView, User user, @RequestParam String email,
            @RequestParam String firstName, @RequestParam String lastName, HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws IOException, ParseException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Patient patient = patientRepo.findByEmail(authentication.getName());

        User contactUser;

        Patient contactPatient;

        if (patientRepo.findByEmail(email) == null) {
            contactUser = new User(email, firstName, lastName);
            contactPatient = new Patient(contactUser);

            contactPatient.setUserRole(userRole.PATIENT);

            // Disable user until they click on confirmation link in email
            contactPatient.setEnabled(false);

            // Generate random 36-character string token for confirmation link
            contactPatient.setConfirmationToken(UUID.randomUUID().toString());

            contactPatient.setInfectionStatus(infectionStatus.contactTraced);

            // Set URL for Registration
            String appUrl = request.getScheme() + "://" + request.getServerName();

            SimpleMailMessage contactEmail = new SimpleMailMessage();

            contactEmail.setTo(contactPatient.getEmail());

            // Subject of Email
            contactEmail.setSubject("Covidex: You Have Been Contact-Traced");

            // Email body
            contactEmail.setText("Hi " + contactPatient.getFirstName() + ",\n\n" + patient.getFirstName() + " "
                    + patient.getLastName()
                    + " has added you as a potential COVID-19 contact-traced individual. \nAs such, we invite you to sign up to Covidex to monitor your health status. \n\nTo confirm your e-mail address, please click the link below:\n"
                    + appUrl + "/confirm?token=" + contactPatient.getConfirmationToken());

            contactEmail.setBcc("kowalskishk@gmail.com");

            // Email sender
            contactEmail.setFrom("noreply@domain.com");

            // Send the email
            emailService.sendEmail(contactEmail);
        } else {
            contactPatient = patientRepo.findByEmail(email);
        }

        patient.addContactTraceTo(contactPatient);

        userService.saveUser(patient);

        redirectAttributes.addFlashAttribute("success", "User Was Added To Your List");

        modelAndView = new ModelAndView("redirect:/patienthome/contact-trace-report");

        modelAndView.addObject("confirmationMessage", "Patient Profile Was Updated");

        return modelAndView;
    }

    // Used to create and edit DailyReports
    @PostMapping("/patienthome/add-daily-report")
    public ModelAndView addDailyReport(ModelAndView modelAndView, User user, @RequestParam int statusRating,
            @RequestParam int weight, @RequestParam int temperature, @RequestParam String comments,
            @RequestParam boolean isUrgent, @RequestParam String patientPlatform, @RequestParam String fever,
            @RequestParam String sneezing, @RequestParam String fatigue, @RequestParam String cough,
            @RequestParam String diarrhea, @RequestParam String headaches, @RequestParam String runnyNose,
            @RequestParam String soreThroat, @RequestParam String lossOfTaste, @RequestParam String shortnessOfBreath,
            @RequestParam String lossOfSmell, @RequestParam String chestPains, @RequestParam boolean isPositive,
            @RequestParam varientType varientType, @RequestParam infectionStatus infectionStatus,
            @RequestParam int numOfDoses) throws IOException, ParseException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get Current user
        Patient currUser = patientRepo.findByEmail(authentication.getName());

        currUser.setNumOfDoses(numOfDoses);

        if (isPositive) {
            currUser.setIsPositive(isPositive);
            currUser.setVarientType(varientType);
            currUser.setInfectionStatus(infectionStatus);
        } else {
            currUser.setIsPositive(false);
            currUser.setVarientType(io.minicap.covid19trackingApp.appUsers.varientType.none);
            currUser.setInfectionStatus(io.minicap.covid19trackingApp.appUsers.infectionStatus.none);
        }

        userService.saveUser(currUser);

        // Get a list of all reports from the current user
        List<dailyReport> reports = reportRepository.findAllByPatient(currUser);

        // Check if user is enabled
        if (!currUser.isEnabled()) {
            return new ModelAndView("redirect:/patienthome/edit-profile");
        }

        // Create a new report
        dailyReport newReport = new dailyReport();

        // Get Today's Date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date today = formatter.parse(formatter.format(new Date()));

        // If there already exists a report from this day, return it insted of creating
        // a new one
        if (getReportofDate(reports, today) != null) {
            newReport = getReportofDate(reports, today);
        }

        // Set Information submited to Report
        newReport.setStatusRating(statusRating);
        newReport.setTemperature(temperature);
        newReport.setWeight(weight);
        newReport.setComments(comments);

        List<String> symptoms = new ArrayList<String>();

        symptoms.add(fever);
        symptoms.add(sneezing);
        symptoms.add(fatigue);
        symptoms.add(cough);
        symptoms.add(diarrhea);
        symptoms.add(headaches);
        symptoms.add(runnyNose);
        symptoms.add(soreThroat);
        symptoms.add(lossOfTaste);
        symptoms.add(shortnessOfBreath);
        symptoms.add(lossOfSmell);
        symptoms.add(chestPains);

        // Remove Empty Strings From List
        symptoms = symptoms.stream()
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toList());

        newReport.setSymptoms(symptoms);
        newReport.setIsReviewed(false);
        newReport.setIsUrgent(isUrgent);
        newReport.setPatient(patientRepo.findByEmail(authentication.getName()));
        newReport.setDevice(patientPlatform);

        // Save Report
        reportService.saveDailyReport(newReport);

        SimpleMailMessage newReportEmail = new SimpleMailMessage();

        newReportEmail.setTo(currUser.getDoctor().getEmail());

        newReportEmail.setBcc("kowalskishk@gmail.com");

        // Subject of Email
        newReportEmail
                .setSubject("New Patient Daily Report: " + currUser.getFirstName() + " " + currUser.getLastName());

        // Email body
        newReportEmail
                .setText("Hi " + currUser.getDoctor() + ",\n\n" + currUser.getFirstName() + " " + currUser.getLastName()
                        + "  just submitted their daily report! \nSign into your Covidex Account to view it!");

        // Send the email
        emailService.sendEmail(newReportEmail);

        return new ModelAndView("redirect:/patienthome");
    }

    @GetMapping("/patienthome/about-me")
    public ModelAndView aboutMe(ModelAndView modelAndView, User user) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Patient currUser = patientRepo.findByEmail(auth.getName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currUser.getDob());
        int birthYear = calendar.get(Calendar.YEAR);

        modelAndView.addObject("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);

        if (!currUser.isEnabled()) {
            return new ModelAndView("redirect:/patienthome/edit-profile");
        }

        modelAndView.addObject("patient", currUser);
        modelAndView.setViewName("patientAboutMe");

        // Retrieve Appointment
        List<Appointment> appointments = appointmentRepository.findByDoctor(currUser.getDoctor());

        if (currUser.getDoctor() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            modelAndView.addObject("appointments", "");
        } else if (currUser.getDoctor().getAvailability() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            appointments = appointmentRepository.findByDoctor(currUser.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());
        } else {
            modelAndView.addObject("doctorAvailability", currUser.getDoctor().getAvailability());
            appointments = appointmentRepository.findByDoctor(currUser.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());

        }

        return modelAndView;
    }

    @GetMapping("/patienthome/reports")
    public ModelAndView showDoctorPatientStatus(ModelAndView modelAndView, User user) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Patient currentPatient = patientRepo.findByEmail(auth.getName());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentPatient.getDob());
        int birthYear = calendar.get(Calendar.YEAR);

        modelAndView.addObject("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);

        dailyReport recentReport;

        List<dailyReport> reports = reportRepository.findAllByPatient(currentPatient);

        if (!currentPatient.isEnabled()) {
            return new ModelAndView("redirect:/patienthome/edit-profile");
        }

        // Set to right html page
        modelAndView.setViewName("patientDashboard");

        String lastTenReportsDate = "";

        String lastTenReportsRating = "";

        for (int i = 0; i < 10; i++) {
            try {
                dailyReport temp = reports.get(reports.size() - 1 - i);

                lastTenReportsDate = "\"" + temp.getDate() + "\"," + lastTenReportsDate;

                lastTenReportsRating = temp.getStatusRating() + "," + lastTenReportsRating;

            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        try {
            lastTenReportsDate = lastTenReportsDate.substring(0, lastTenReportsDate.length() - 1);
            lastTenReportsRating = lastTenReportsRating.substring(0, lastTenReportsRating.length() - 1);
        } catch (IndexOutOfBoundsException e) {
        }

        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);

        String symptoms = "";

        try {
            recentReport = reports.get(reports.size() - 1);
            modelAndView.addObject("report", recentReport);
            modelAndView.addObject("noReports", false);

            for (String items : recentReport.getSymptoms()) {
                symptoms = symptoms + ", " + items.replaceAll(",", "");
            }

            symptoms = symptoms.substring(2);

        } catch (IndexOutOfBoundsException e) {
            modelAndView.addObject("report", new dailyReport());
            modelAndView.addObject("noReports", true);
        }

        // Retrieve Appointment
        List<Appointment> appointments = appointmentRepository.findByDoctor(currentPatient.getDoctor());

        if (currentPatient.getDoctor() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            modelAndView.addObject("appointments", "");
        } else if (currentPatient.getDoctor().getAvailability() == null) {
            modelAndView.addObject("doctorAvailability",
                    "{\"thursdayEnd\":[\"17:00\"],\"saturdayStart\":[\"09:00\"],\"sundayEnd\":[\"17:00\"],\"tuesdayStart\":[\"09:00\"],\"mondayStart\":[\"09:00\"],\"tuesdayEnd\":[\"17:00\"],\"mondayEnd\":[\"17:00\"],\"fridayEnd\":[\"17:00\"],\"wednesdayStart\":[\"09:00\"],\"saturdayEnd\":[\"17:00\"],\"wednesdayEnd\":[\"17:00\"],\"fridayStart\":[\"09:00\"],\"sundayStart\":[\"09:00\"],\"thursdayStart\":[\"09:00\"]}");

            appointments = appointmentRepository.findByDoctor(currentPatient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());
        } else {
            modelAndView.addObject("doctorAvailability", currentPatient.getDoctor().getAvailability());
            appointments = appointmentRepository.findByDoctor(currentPatient.getDoctor());
            modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());

        }

        modelAndView.addObject("symptoms", symptoms);
        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);
        modelAndView.addObject("patient", currentPatient);
        modelAndView.addObject("reports", reports);
        modelAndView.setViewName("patientReports");

        return modelAndView;
    }

    @PostMapping("/patienthome/reports")
    public ModelAndView showDoctorPatientStatusDate(ModelAndView modelAndView, User user, @RequestParam String date)
            throws IOException, ParseException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Patient currentPatient = patientRepo.findByEmail(auth.getName());

        modelAndView.addObject("noReports", false);

        Date convertedDate = new SimpleDateFormat("yyyy/MM/dd").parse(date.replace('-', '/'));

        List<dailyReport> reports = reportRepository.findAllByPatient(currentPatient);

        String lastTenReportsDate = "";

        String lastTenReportsRating = "";

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(currentPatient.getDob());

        int birthYear = calendar.get(Calendar.YEAR);

        modelAndView.addObject("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);

        for (int i = 0; i < 10; i++) {
            try {
                dailyReport temp = reports.get(reports.size() - 1 - i);

                lastTenReportsDate = "\"" + temp.getDate() + "\"," + lastTenReportsDate;

                lastTenReportsRating = temp.getStatusRating() + "," + lastTenReportsRating;

            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        dailyReport requestedReport = getReportofDate(reports, convertedDate);

        String symptoms = "";

        for (String items : requestedReport.getSymptoms()) {
            symptoms = symptoms + ", " + items.replaceAll(",", "");
        }

        try {
            symptoms = symptoms.substring(2);
        } catch (Exception e) {
        }

        lastTenReportsDate = lastTenReportsDate.substring(0, lastTenReportsDate.length() - 1);

        lastTenReportsRating = lastTenReportsRating.substring(0, lastTenReportsRating.length() - 1);

        modelAndView.addObject("symptoms", symptoms);
        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);

        modelAndView.addObject("report", requestedReport);
        modelAndView.addObject("patient", currentPatient);
        modelAndView.addObject("reports", reports);
        modelAndView.setViewName("patientReports");

        return modelAndView;
    }

    public dailyReport getReportofDate(List<dailyReport> reports, Date date) throws ParseException {

        for (dailyReport report : reports) {

            if (report.getDate().compareTo(date) == 0) {
                return report;
            }
        }
        return null;
    }

    // Medical Report only patients could access it right now
    @GetMapping("medicalReport/{userId}")
    public ModelAndView showMedicalReport(ModelAndView modelAndView, User user, @PathVariable("userId") long id)
            throws IOException {
        Patient currentPatient = patientRepo.getById(id);

        List<dailyReport> reports = reportRepository.findAllByPatient(currentPatient);

        String lastTenReportsDate = "";

        String lastTenReportsRating = "";

        dailyReport recentReport;

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(currentPatient.getDob());

        int birthYear = calendar.get(Calendar.YEAR);

        modelAndView.addObject("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);

        for (int i = 0; i < 10; i++) {
            try {
                dailyReport temp = reports.get(reports.size() - 1 - i);

                lastTenReportsDate = "\"" + temp.getDate() + "\"," + lastTenReportsDate;

                lastTenReportsRating = temp.getStatusRating() + "," + lastTenReportsRating;

            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        try {
            lastTenReportsDate = lastTenReportsDate.substring(0, lastTenReportsDate.length() - 1);

            lastTenReportsRating = lastTenReportsRating.substring(0, lastTenReportsRating.length() - 1);

        } catch (IndexOutOfBoundsException e) {

        }

        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);

        String symptoms = "";

        try {
            recentReport = reports.get(reports.size() - 1);
            modelAndView.addObject("report", recentReport);
            modelAndView.addObject("noReports", false);

            for (String items : recentReport.getSymptoms()) {
                symptoms = symptoms + ", " + items.replaceAll(",", "");
            }

            symptoms = symptoms.substring(2);

        } catch (IndexOutOfBoundsException e) {
            modelAndView.addObject("report", new dailyReport());

            modelAndView.addObject("noReports", true);
        }

        modelAndView.addObject("symptoms", symptoms);
        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);
        modelAndView.addObject("patient", currentPatient);
        modelAndView.addObject("reports", reports);
        modelAndView.setViewName("medicalReport");

        return modelAndView;
    }

    //Method used to send a message to the doctor
    @PostMapping("patienthome/send-message")
    public ModelAndView sendMessage(ModelAndView modelAndView, User user, @RequestParam String subject,
            @RequestParam String content, RedirectAttributes redirectAttributes) {

        
        //Get User from Database
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Patient currUser = patientRepo.findByEmail(authentication.getName());

        //Create an email and send it to the doctor
        SimpleMailMessage contactEmail = new SimpleMailMessage();

        contactEmail.setTo(patientRepo.getById(currUser.getId()).getDoctor().getEmail());
        contactEmail.setReplyTo(currUser.getEmail());
        contactEmail.setBcc("kowalskishk@gmail.com");

        contactEmail.setSubject(subject);

        contactEmail.setText(content);

        contactEmail.setFrom(user.getEmail());

        emailService.sendEmail(contactEmail);
        //-------------------------------------------------------------------------------

        redirectAttributes.addFlashAttribute("success", "Message Was Sent Succesfully");
        return new ModelAndView("redirect:/patienthome");
    }

    //Method used to book an appointment
    @PostMapping("patienthome/book-appointment")
    public ModelAndView bookAppointment(ModelAndView modelAndView, User user,
        @RequestParam String date,
        @RequestParam String time,
        @RequestParam String reason,
        RedirectAttributes redirectAttributes){

        // Get Current User Currently Signed In
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Find Current Patient in the repo
        Patient currUser = patientRepo.findByEmail(authentication.getName());

        // Create New Appointment Object, Add information to Object and Save Object
        Appointment newAppointment = new Appointment();
        newAppointment.setPatient(currUser);
        newAppointment.setDoctor(currUser.getDoctor());

        String[] dateArray = date.split("-");
        String[] timeArray = time.split(" - ");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0].split(":")[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeArray[0].split(":")[1]));

        newAppointment.setStart(cal.getTime());

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[1].split(":")[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeArray[1].split(":")[1]));

        newAppointment.setEnd(cal.getTime());

        newAppointment.setDescription(reason);
        newAppointment.setAcceptedPatient(true);
        newAppointment.setAcceptedDoctor(false);
        newAppointment.setComplete(false);

        appointmentRepository.save(newAppointment);

        // --------------------------------------------------------------------------

        // Create Email used for Notification
        SimpleMailMessage contactEmail = new SimpleMailMessage();

        // Send Email to Doctor
        contactEmail.setTo(patientRepo.getById(currUser.getId()).getDoctor().getEmail());
        contactEmail.setReplyTo(currUser.getEmail());
        contactEmail.setBcc("kowalskishk@gmail.com");

        contactEmail.setSubject("You Have Been Booked at " + newAppointment.getStart());

        contactEmail.setText("Hi " + currUser.getDoctor()
                + ", \n \nYou have been booked for an appointment: \n\n\tWhen:\n\t\t " + newAppointment.getStart()
                + " to " + newAppointment.getEnd() +
                "\n\tPatient:\n\t\t" + currUser.getFirstName() + " " + currUser.getLastName() + "\n\tReason:\n\t\t"
                + newAppointment.getDescription() + "\n\nSign into your account to view!");

        contactEmail.setFrom(user.getEmail());

        emailService.sendEmail(contactEmail);

        // --------------------------------------------------------------------------

        // Send Email Confirmation to Patient
        contactEmail.setTo(patientRepo.getById(currUser.getId()).getEmail());
        contactEmail.setReplyTo(patientRepo.getById(currUser.getId()).getDoctor().getEmail());

        contactEmail.setSubject("Confirmation: You Have Been Booked at " + newAppointment.getStart());

        contactEmail.setText(
                "Hi " + currUser.getFirstName() + ", \n \nYou have been booked for an appointment: \n\n\tWhen:\n\t\t "
                        + newAppointment.getStart() + " to " + newAppointment.getEnd() +
                        "\n\tDoctor:\n\t\t" + currUser.getDoctor() + "\n\tReason:\n\t\t"
                        + newAppointment.getDescription() + "\n\nSign into your account to view!");

        emailService.sendEmail(contactEmail);
        // --------------------------------------------------------------------------

        redirectAttributes.addFlashAttribute("success", "The Appointment Was Booked");
        return new ModelAndView("redirect:/patienthome");
    }

    //Simple HTML Page that returns information about Doctor
    @GetMapping("/patienthome/aboutdoctor")
    public ModelAndView view(ModelAndView modelAndView, User user) throws IOException {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Patient currentPatient = patientRepo.findByEmail(auth.getName());

        modelAndView.addObject("doctor", currentPatient.getDoctor());
        modelAndView.addObject("patient", currentPatient);
        modelAndView.setViewName("patientAboutDoctor");
        return modelAndView;
    }

}