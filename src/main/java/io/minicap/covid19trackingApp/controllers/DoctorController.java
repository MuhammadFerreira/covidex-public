package io.minicap.covid19trackingApp.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.gender;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.appUsers.userRole;
import io.minicap.covid19trackingApp.appointment.Appointment;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;
import io.minicap.covid19trackingApp.repository.AppointmentRepository;
import io.minicap.covid19trackingApp.repository.DoctorRepository;
import io.minicap.covid19trackingApp.repository.PatientRepository;
import io.minicap.covid19trackingApp.repository.ReportRepository;
import io.minicap.covid19trackingApp.repository.UserRepository;
import io.minicap.covid19trackingApp.service.ReportService;
import io.minicap.covid19trackingApp.service.EmailService;
import io.minicap.covid19trackingApp.service.PatientService;
import io.minicap.covid19trackingApp.service.UserService;
import io.minicap.covid19trackingApp.service.statsService;

//Simple controller used specifically just for login
@Controller
public class DoctorController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepo;

    private EmailService emailService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private DoctorRepository doctorRepo;

    private UserService userService;
    private ReportService reportService;
    private PatientService patientService;

    @Autowired
    public DoctorController(UserService userService, ReportService reportService, EmailService emailService,
            PatientService patientService) {
        this.userService = userService;
        this.reportService = reportService;
        this.emailService = emailService;
        this.patientService = patientService;
    }

    // Display Doctor Dashboard
    @GetMapping("/doctorhome")
    public ModelAndView showDoctorHome(ModelAndView modelAndView, User user) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get User Object of Logged In User
        Doctor currUser = doctorRepo.findByEmail(authentication.getName());

        //Return all Appointments related to this doctor
        List<Appointment> appointments = appointmentRepository.findByDoctor(currUser);

        //Create a JSONArray with information of appointments to be read by JavaScript
        JSONArray arrayAppointments = new JSONArray();

        for (Appointment a : appointments) 
        {
            JSONObject app = new JSONObject();
            Patient currPatient = a.getPatient();
            String isoDatePattern = "yyyy-MM-dd'T'HH:mm:00";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
            String dateString = simpleDateFormat.format(a.getStart());

            app.put("start", dateString);
            dateString = simpleDateFormat.format(a.getEnd());
            app.put("end", dateString);
            app.put("id", a.getId());
            app.put("text", "Appointment with " + currPatient.getFirstName() + " " + currPatient.getLastName());

            arrayAppointments.put(app);
        }

        modelAndView.addObject("appointments", arrayAppointments.toString());
        //--------------------------------------------------------------------------

        //Send information about doctor availability to the template
        try 
        {
            JSONObject jsonDetails = new JSONObject(currUser.getAvailability());

            modelAndView.addObject("mondayStart", jsonDetails.get("mondayStart").toString().substring(2, 7));
            modelAndView.addObject("mondayEnd", jsonDetails.get("mondayEnd").toString().substring(2, 7));
            modelAndView.addObject("tuesdayStart", jsonDetails.get("tuesdayStart").toString().substring(2, 7));
            modelAndView.addObject("tuesdayEnd", jsonDetails.get("tuesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("wednesdayStart", jsonDetails.get("wednesdayStart").toString().substring(2, 7));
            modelAndView.addObject("wednesdayEnd", jsonDetails.get("wednesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("thursdayStart", jsonDetails.get("thursdayStart").toString().substring(2, 7));
            modelAndView.addObject("thursdayEnd", jsonDetails.get("thursdayEnd").toString().substring(2, 7));
            modelAndView.addObject("fridayStart", jsonDetails.get("fridayStart").toString().substring(2, 7));
            modelAndView.addObject("fridayEnd", jsonDetails.get("fridayEnd").toString().substring(2, 7));
            modelAndView.addObject("saturdayStart", jsonDetails.get("saturdayStart").toString().substring(2, 7));
            modelAndView.addObject("saturdayEnd", jsonDetails.get("saturdayEnd").toString().substring(2, 7));
            modelAndView.addObject("sundayStart", jsonDetails.get("sundayStart").toString().substring(2, 7));
            modelAndView.addObject("sundayEnd", jsonDetails.get("sundayEnd").toString().substring(2, 7));
        } 
        catch (Exception e) 
        {

            modelAndView.addObject("mondayStart", "09:00");
            modelAndView.addObject("mondayEnd", "17:00");
            modelAndView.addObject("tuesdayStart", "09:00");
            modelAndView.addObject("tuesdayEnd", "17:00");
            modelAndView.addObject("wednesdayStart", "09:00");
            modelAndView.addObject("wednesdayEnd", "17:00");
            modelAndView.addObject("thursdayStart", "09:00");
            modelAndView.addObject("thursdayEnd", "17:00");
            modelAndView.addObject("fridayStart", "09:00");
            modelAndView.addObject("fridayEnd", "17:00");
            modelAndView.addObject("saturdayStart", "09:00");
            modelAndView.addObject("saturdayEnd", "17:00");
            modelAndView.addObject("sundayStart", "09:00");
            modelAndView.addObject("sundayEnd", "17:00");

        }
        //--------------------------------------------------------------------------

        // Return List of Patients that are managed by doctor
        List<Patient> patients = patientRepo.findByDoctor(doctorRepo.findByEmail(authentication.getName()));

        // Initialize pending reports
        int pendingDailyStatusReports = 0;

        // For each patient, add the number of status unread for that user and add it to
        // the total
        for (Patient patient : patients) {
            pendingDailyStatusReports = reportRepository.findAllByPatientAndIsReviewed(patient, false).size()
                    + pendingDailyStatusReports;
        }

        // Add Number of Unread Reports
        modelAndView.addObject("unreadDailyStatus", pendingDailyStatusReports);

        // Check if the user is enabled
        if (!currUser.isEnabled()) {
            modelAndView.addObject("message", "Please Fill Out This Form To Access Your Dashboard!");
        } else {
            modelAndView.addObject("message", "Account Settings");
        }

        // Add List of Patient of the doctor
        modelAndView.addObject("patients", patients);

        // Add Number of Infected Patients
        modelAndView.addObject("infectedPatients",
                patientRepo.findByDoctorAndIsPositive(doctorRepo.findByEmail(authentication.getName()), true));

        // Add Flagged Patients
        modelAndView.addObject("flaggedPatients",
                patientRepo.findByDoctorAndIsFlagged(doctorRepo.findByEmail(authentication.getName()), true));

        // Set the view to doctorDashboard.html
        modelAndView.setViewName("doctorDashboard");

        // Get data from Online Sources about COVID-19
        modelAndView.addObject("newInfections", statsService.getCurrentCases());

        // Get Change in Hospitilizations
        modelAndView.addObject("changeInHospitilizations", statsService.getChangeinHospitilizations());

        // Get number of deaths
        modelAndView.addObject("deaths", statsService.getDeaths());

        return modelAndView;
    }

    // Used to Return Edit Profile
    @GetMapping("/doctorhome/edit-profile")
    public ModelAndView editProfile(ModelAndView modelAndView, User user) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currUser = userRepo.findByEmail(authentication.getName());

        Doctor doctor = doctorRepo.findByEmail(authentication.getName());

        modelAndView.addObject("doctor", doctor);

        if (!currUser.isEnabled()) {
            modelAndView.addObject("message", "Please Fill Out This Form To Access Your Dashboard!");
        } else {
            modelAndView.addObject("message", "Account Settings");
        }

        // Set to right html page
        modelAndView.setViewName("doctorAccountSettings");
        return modelAndView;
    }

    // Used to return information about user
    @GetMapping("/doctorhome/about-me")
    public ModelAndView viewProfile(ModelAndView modelAndView, User user) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Doctor doctor = doctorRepo.findByEmail(authentication.getName());

        // Get User Object of Logged In User
        Doctor currUser = doctorRepo.findByEmail(authentication.getName());

        //Return all Appointments related to this doctor
        List<Appointment> appointments = appointmentRepository.findByDoctor(currUser);

        //Create a JSONArray with information of appointments to be read by JavaScript
        JSONArray arrayAppointments = new JSONArray();

        for (Appointment a : appointments) 
        {
            JSONObject app = new JSONObject();
            Patient currPatient = a.getPatient();
            String isoDatePattern = "yyyy-MM-dd'T'HH:mm:00";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
            String dateString = simpleDateFormat.format(a.getStart());

            app.put("start", dateString);
            dateString = simpleDateFormat.format(a.getEnd());
            app.put("end", dateString);
            app.put("id", a.getId());
            app.put("text", "Appointment with " + currPatient.getFirstName() + " " + currPatient.getLastName());

            arrayAppointments.put(app);
        }

        modelAndView.addObject("appointments", arrayAppointments.toString());
        //--------------------------------------------------------------------------

        //Send information about doctor availability to the template
        try 
        {
            JSONObject jsonDetails = new JSONObject(currUser.getAvailability());

            modelAndView.addObject("mondayStart", jsonDetails.get("mondayStart").toString().substring(2, 7));
            modelAndView.addObject("mondayEnd", jsonDetails.get("mondayEnd").toString().substring(2, 7));
            modelAndView.addObject("tuesdayStart", jsonDetails.get("tuesdayStart").toString().substring(2, 7));
            modelAndView.addObject("tuesdayEnd", jsonDetails.get("tuesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("wednesdayStart", jsonDetails.get("wednesdayStart").toString().substring(2, 7));
            modelAndView.addObject("wednesdayEnd", jsonDetails.get("wednesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("thursdayStart", jsonDetails.get("thursdayStart").toString().substring(2, 7));
            modelAndView.addObject("thursdayEnd", jsonDetails.get("thursdayEnd").toString().substring(2, 7));
            modelAndView.addObject("fridayStart", jsonDetails.get("fridayStart").toString().substring(2, 7));
            modelAndView.addObject("fridayEnd", jsonDetails.get("fridayEnd").toString().substring(2, 7));
            modelAndView.addObject("saturdayStart", jsonDetails.get("saturdayStart").toString().substring(2, 7));
            modelAndView.addObject("saturdayEnd", jsonDetails.get("saturdayEnd").toString().substring(2, 7));
            modelAndView.addObject("sundayStart", jsonDetails.get("sundayStart").toString().substring(2, 7));
            modelAndView.addObject("sundayEnd", jsonDetails.get("sundayEnd").toString().substring(2, 7));
        } 
        catch (Exception e) 
        {

            modelAndView.addObject("mondayStart", "09:00");
            modelAndView.addObject("mondayEnd", "17:00");
            modelAndView.addObject("tuesdayStart", "09:00");
            modelAndView.addObject("tuesdayEnd", "17:00");
            modelAndView.addObject("wednesdayStart", "09:00");
            modelAndView.addObject("wednesdayEnd", "17:00");
            modelAndView.addObject("thursdayStart", "09:00");
            modelAndView.addObject("thursdayEnd", "17:00");
            modelAndView.addObject("fridayStart", "09:00");
            modelAndView.addObject("fridayEnd", "17:00");
            modelAndView.addObject("saturdayStart", "09:00");
            modelAndView.addObject("saturdayEnd", "17:00");
            modelAndView.addObject("sundayStart", "09:00");
            modelAndView.addObject("sundayEnd", "17:00");

        }
        //--------------------------------------------------------------------------


        modelAndView.addObject("doctor", doctor);

        // Set to right html page
        modelAndView.setViewName("doctorAboutMe");
        return modelAndView;
    }

    // Edit Doctor profile
    @PostMapping("/doctorhome/edit-profile")
    public ModelAndView editProfilePost(ModelAndView modelAndView, User user, @RequestParam gender gender,
            @RequestParam String address, @RequestParam String birthDate, @RequestParam String certification,
            @RequestParam int seniority, @RequestParam String hospital, @RequestParam String department,
            @RequestParam String phoneNumber) throws IOException, ParseException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Doctor doctor = doctorRepo.findByEmail(authentication.getName());

        doctor.setDob(new SimpleDateFormat("yyyy/MM/dd").parse(birthDate.replace('-', '/')));
        doctor.setPhoneNumber(phoneNumber);
        doctor.setGender(gender);
        doctor.setAddress(address);
        doctor.setCertification(certification);
        doctor.setSeniority(seniority);
        doctor.setHospital(hospital);
        doctor.setDepartment(department);

        userService.saveUser(doctor);

        modelAndView = new ModelAndView("redirect:/doctorhome");
        modelAndView.addObject("confirmationMessage", "Doctor Profile Was Updated");

        return modelAndView;
    }

    // Display Doctor's patient list
    @GetMapping("/doctorhome/patients")
    public ModelAndView showDoctorPatient(ModelAndView modelAndView, User user) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Set view to doctorPatientList.html
        modelAndView.setViewName("doctorPatientList");

        List<Patient> patients = patientRepo.findByDoctor(doctorRepo.findByEmail(authentication.getName()));

        // Get User Object of Logged In User
        Doctor currUser = doctorRepo.findByEmail(authentication.getName());

        //Return all Appointments related to this doctor
        List<Appointment> appointments = appointmentRepository.findByDoctor(currUser);

        //Create a JSONArray with information of appointments to be read by JavaScript
        JSONArray arrayAppointments = new JSONArray();

        for (Appointment a : appointments) 
        {
            JSONObject app = new JSONObject();
            Patient currPatient = a.getPatient();
            String isoDatePattern = "yyyy-MM-dd'T'HH:mm:00";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
            String dateString = simpleDateFormat.format(a.getStart());

            app.put("start", dateString);
            dateString = simpleDateFormat.format(a.getEnd());
            app.put("end", dateString);
            app.put("id", a.getId());
            app.put("text", "Appointment with " + currPatient.getFirstName() + " " + currPatient.getLastName());

            arrayAppointments.put(app);
        }

        modelAndView.addObject("appointments", arrayAppointments.toString());
        //--------------------------------------------------------------------------

        //Send information about doctor availability to the template
        try 
        {
            JSONObject jsonDetails = new JSONObject(currUser.getAvailability());

            modelAndView.addObject("mondayStart", jsonDetails.get("mondayStart").toString().substring(2, 7));
            modelAndView.addObject("mondayEnd", jsonDetails.get("mondayEnd").toString().substring(2, 7));
            modelAndView.addObject("tuesdayStart", jsonDetails.get("tuesdayStart").toString().substring(2, 7));
            modelAndView.addObject("tuesdayEnd", jsonDetails.get("tuesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("wednesdayStart", jsonDetails.get("wednesdayStart").toString().substring(2, 7));
            modelAndView.addObject("wednesdayEnd", jsonDetails.get("wednesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("thursdayStart", jsonDetails.get("thursdayStart").toString().substring(2, 7));
            modelAndView.addObject("thursdayEnd", jsonDetails.get("thursdayEnd").toString().substring(2, 7));
            modelAndView.addObject("fridayStart", jsonDetails.get("fridayStart").toString().substring(2, 7));
            modelAndView.addObject("fridayEnd", jsonDetails.get("fridayEnd").toString().substring(2, 7));
            modelAndView.addObject("saturdayStart", jsonDetails.get("saturdayStart").toString().substring(2, 7));
            modelAndView.addObject("saturdayEnd", jsonDetails.get("saturdayEnd").toString().substring(2, 7));
            modelAndView.addObject("sundayStart", jsonDetails.get("sundayStart").toString().substring(2, 7));
            modelAndView.addObject("sundayEnd", jsonDetails.get("sundayEnd").toString().substring(2, 7));
        } 
        catch (Exception e) 
        {

            modelAndView.addObject("mondayStart", "09:00");
            modelAndView.addObject("mondayEnd", "17:00");
            modelAndView.addObject("tuesdayStart", "09:00");
            modelAndView.addObject("tuesdayEnd", "17:00");
            modelAndView.addObject("wednesdayStart", "09:00");
            modelAndView.addObject("wednesdayEnd", "17:00");
            modelAndView.addObject("thursdayStart", "09:00");
            modelAndView.addObject("thursdayEnd", "17:00");
            modelAndView.addObject("fridayStart", "09:00");
            modelAndView.addObject("fridayEnd", "17:00");
            modelAndView.addObject("saturdayStart", "09:00");
            modelAndView.addObject("saturdayEnd", "17:00");
            modelAndView.addObject("sundayStart", "09:00");
            modelAndView.addObject("sundayEnd", "17:00");

        }
        //--------------------------------------------------------------------------
        
        modelAndView.addObject("listPatients", patients);

        return modelAndView;
    }

    // Method used to return information specific to a Patient and Daily
    // Reports
    @GetMapping("/doctorhome/patients/{userId}")
    public ModelAndView showDoctorPatientStatus(ModelAndView modelAndView, User user, @PathVariable("userId") long id,
            HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        String referer = request.getHeader("Referer");

        // Get the patient by ID and Use it to return all Daily Reports Associated to
        // Them
        Patient currentPatient = patientRepo.getById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Doctor currUser = doctorRepo.findByEmail(authentication.getName());

        if (currentPatient.getEnabled() == false) {
            redirectAttributes.addFlashAttribute("error", currentPatient.getFirstName() + " has not been enabled.");
            return new ModelAndView("redirect:" + referer);
        }

        List<dailyReport> reports = reportRepository.findAllByPatient(currentPatient);

        List<Appointment> appointments;

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

        JSONObject reportDetails;

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

        // Initialize the Strings Used to Display 10-Day Status Graph
        String lastTenReportsDate = "";
        String lastTenReportsRating = "";

        // Declare recent report
        dailyReport recentReport;

        // Get Age of Patient
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentPatient.getDob());
        int birthYear = calendar.get(Calendar.YEAR);
        modelAndView.addObject("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear);

        // Loop through last 10 status reports. This will be used in the 10 day Status
        // Graph
        for (int i = 0; i < 10; i++) {
            try {
                dailyReport temp = reports.get(reports.size() - 1 - i);
                lastTenReportsDate = "\"" + temp.getDate() + "\"," + lastTenReportsDate;
                lastTenReportsRating = temp.getStatusRating() + "," + lastTenReportsRating;

            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        // Format Last 10 Report information
        try {
            lastTenReportsDate = lastTenReportsDate.substring(0, lastTenReportsDate.length() - 1);
            lastTenReportsRating = lastTenReportsRating.substring(0, lastTenReportsRating.length() - 1);

        } catch (IndexOutOfBoundsException e) {
        }

        // Add Reports to Model
        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);

        // Initialize the Symptoms
        String symptoms = "";

        // Try Catch Used to retrieve and format all symptoms
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

        List<Appointment> doctorAppointments = appointmentRepository.findByDoctor(currUser);

        JSONArray arrayAppointments = new JSONArray();

        for (Appointment a : doctorAppointments) {
            JSONObject app = new JSONObject();

            Patient currPatient = a.getPatient();

            String isoDatePattern = "yyyy-MM-dd'T'HH:mm:00";

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);

            String dateString = simpleDateFormat.format(a.getStart());

            app.put("start", dateString);

            dateString = simpleDateFormat.format(a.getEnd());

            app.put("end", dateString);

            app.put("id", a.getId());

            app.put("text", "Appointment with " + currPatient.getFirstName() + " " + currPatient.getLastName());

            arrayAppointments.put(app);
        }

        modelAndView.addObject("appointments", arrayAppointments.toString());

        try {
            JSONObject jsonDetails = new JSONObject(currUser.getAvailability());

            modelAndView.addObject("mondayStart", jsonDetails.get("mondayStart").toString().substring(2, 7));
            modelAndView.addObject("mondayEnd", jsonDetails.get("mondayEnd").toString().substring(2, 7));
            modelAndView.addObject("tuesdayStart", jsonDetails.get("tuesdayStart").toString().substring(2, 7));
            modelAndView.addObject("tuesdayEnd", jsonDetails.get("tuesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("wednesdayStart", jsonDetails.get("wednesdayStart").toString().substring(2, 7));
            modelAndView.addObject("wednesdayEnd", jsonDetails.get("wednesdayEnd").toString().substring(2, 7));
            modelAndView.addObject("thursdayStart", jsonDetails.get("thursdayStart").toString().substring(2, 7));
            modelAndView.addObject("thursdayEnd", jsonDetails.get("thursdayEnd").toString().substring(2, 7));
            modelAndView.addObject("fridayStart", jsonDetails.get("fridayStart").toString().substring(2, 7));
            modelAndView.addObject("fridayEnd", jsonDetails.get("fridayEnd").toString().substring(2, 7));
            modelAndView.addObject("saturdayStart", jsonDetails.get("saturdayStart").toString().substring(2, 7));
            modelAndView.addObject("saturdayEnd", jsonDetails.get("saturdayEnd").toString().substring(2, 7));
            modelAndView.addObject("sundayStart", jsonDetails.get("sundayStart").toString().substring(2, 7));
            modelAndView.addObject("sundayEnd", jsonDetails.get("sundayEnd").toString().substring(2, 7));
        } catch (Exception e) {

            modelAndView.addObject("mondayStart", "09:00");
            modelAndView.addObject("mondayEnd", "17:00");
            modelAndView.addObject("tuesdayStart", "09:00");
            modelAndView.addObject("tuesdayEnd", "17:00");
            modelAndView.addObject("wednesdayStart", "09:00");
            modelAndView.addObject("wednesdayEnd", "17:00");
            modelAndView.addObject("thursdayStart", "09:00");
            modelAndView.addObject("thursdayEnd", "17:00");
            modelAndView.addObject("fridayStart", "09:00");
            modelAndView.addObject("fridayEnd", "17:00");
            modelAndView.addObject("saturdayStart", "09:00");
            modelAndView.addObject("saturdayEnd", "17:00");
            modelAndView.addObject("sundayStart", "09:00");
            modelAndView.addObject("sundayEnd", "17:00");

        }
        // Add all items to Model for use in HTML
        modelAndView.addObject("symptoms", symptoms);
        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);
        modelAndView.addObject("patient", currentPatient);
        modelAndView.addObject("reports", reports);
        modelAndView.setViewName("doctorPatientStatus");

        return modelAndView;
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

    // Refer to GetMapping version. Will be used to get Daily Report from a Specific
    // Date Selected From Drop Down Menu
    @PostMapping("/doctorhome/patients/{userId}")
    public ModelAndView showDoctorPatientStatusDate(ModelAndView modelAndView, User user,
            @PathVariable("userId") long id, @RequestParam String date, HttpServletRequest request)
            throws IOException, ParseException {
        Patient currentPatient = patientRepo.getById(id);

        String referer = request.getHeader("Referer");

        if (currentPatient.getEnabled() == false) {
            return new ModelAndView("redirect:" + referer);
        }

        JSONObject reportDetails;

        List<Appointment> appointments = appointmentRepository.findByDoctor(currentPatient.getDoctor());

        modelAndView.addObject("doctorAvailability", currentPatient.getDoctor().getAvailability());
        modelAndView.addObject("appointments", appointmentsToJSON(appointments).toString());

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

        try {
            lastTenReportsDate = lastTenReportsDate.substring(0, lastTenReportsDate.length() - 1);
            lastTenReportsRating = lastTenReportsRating.substring(0, lastTenReportsRating.length() - 1);
        } catch (IndexOutOfBoundsException e) {
        }

        modelAndView.addObject("lastTenReportDates", lastTenReportsDate);
        modelAndView.addObject("lastTenReportRatings", lastTenReportsRating);

        String symptoms = "";

        try {
            dailyReport requestedReport = getReportofDate(reports, convertedDate);

            modelAndView.addObject("report", requestedReport);
            modelAndView.addObject("noReports", false);

            for (String items : requestedReport.getSymptoms()) {
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
        modelAndView.setViewName("doctorPatientStatus");

        return modelAndView;
    }

    // Helper method used to return the dailyReport for a specific date
    public dailyReport getReportofDate(List<dailyReport> reports, Date date) {
        for (dailyReport report : reports) {

            if (report.getDate().compareTo(date) == 0) {
                return report;
            }
        }
        return null;
    }

    // Add functionality to flag a patient
    @PostMapping("/doctorhome/patients/flag")
    public ModelAndView flagPatients(ModelAndView modelAndView, @RequestParam String patientEmail,
            HttpServletRequest request) {

        String referer = request.getHeader("Referer");

        Patient patientInDB = patientRepo.getById(userRepo.findByEmail(patientEmail).getId());

        patientInDB.setIsFlagged(!patientInDB.getIsFlagged());
        patientRepo.save(patientInDB);

        modelAndView.addObject("confirmationMessage", "Patient status updated");

        return new ModelAndView("redirect:" + referer);
    }

    // Add functionality to add a comment to a Daily Report and Mark it
    // as Read.
    @PostMapping("/doctorhome/patients/post")
    public ModelAndView postReview(ModelAndView modelAndView, @RequestParam long reportId,
            @RequestParam String response, HttpServletRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String referer = request.getHeader("Referer");

        dailyReport report = reportRepository.findById(reportId).orElse(null);

        report.setDoctorComments(response);
        report.setIsReviewed(true);
        report.setDoctorThatReviewed(doctorRepo.findByEmail(authentication.getName()));

        reportService.saveDailyReport(report);

        modelAndView.addObject("confirmationMessage", "Patient status updated");

        return new ModelAndView("redirect:" + referer);
    }

    //Functionality to customize a Daily Report.
    @PostMapping("/doctorhome/patients/details")
    public ModelAndView editReportDetails(ModelAndView modelAndView, HttpServletRequest request,
            @RequestParam long patientID,
            @RequestParam boolean temperature,
            @RequestParam boolean weight,
            @RequestParam boolean fever,
            @RequestParam boolean sneezing,
            @RequestParam boolean fatigue,
            @RequestParam boolean cough,
            @RequestParam boolean diarrhea,
            @RequestParam boolean headaches,
            @RequestParam boolean runnyNose,
            @RequestParam boolean soreThroat,
            @RequestParam boolean lossOfTaste,
            @RequestParam boolean shortnessOfBreath,
            @RequestParam boolean lossOfSmell,
            @RequestParam boolean chestPains) {

        //Get Current Patient
        Patient currPatient = patientRepo.getById(patientID);

        //Create a new JSON object based on what was inputed
        JSONObject jsonDetails = new JSONObject();

        //Append Information
        jsonDetails.append("temperature", temperature);
        jsonDetails.append("weight", weight);
        jsonDetails.append("fever", fever);
        jsonDetails.append("sneezing", sneezing);
        jsonDetails.append("fatigue", fatigue);
        jsonDetails.append("cough", cough);
        jsonDetails.append("diarrhea", diarrhea);
        jsonDetails.append("headaches", headaches);
        jsonDetails.append("runnyNose", runnyNose);
        jsonDetails.append("soreThroat", soreThroat);
        jsonDetails.append("lossOfTaste", lossOfTaste);
        jsonDetails.append("shortnessOfBreath", shortnessOfBreath);
        jsonDetails.append("lossOfSmell", lossOfSmell);
        jsonDetails.append("chestPains", chestPains);

        //Set the JSON to the Patient Object. Save Patient
        currPatient.setReportDetailsJSON(jsonDetails.toString());
        userService.saveUser(currPatient);

        String referer = request.getHeader("Referer");

        return new ModelAndView("redirect:" + referer);

    }

    //Functionality to customize Availability.
    @PostMapping("doctorhome/set-availability")
    public ModelAndView editAvailability(ModelAndView modelAndView, User user, HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam String mondayStart,
            @RequestParam String mondayEnd,
            @RequestParam String tuesdayStart,
            @RequestParam String tuesdayEnd,
            @RequestParam String wednesdayStart,
            @RequestParam String wednesdayEnd,
            @RequestParam String thursdayStart,
            @RequestParam String thursdayEnd,
            @RequestParam String fridayStart,
            @RequestParam String fridayEnd,
            @RequestParam String saturdayStart,
            @RequestParam String saturdayEnd,
            @RequestParam String sundayStart,
            @RequestParam String sundayEnd) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Doctor currPatient = doctorRepo.findByEmail(auth.getName());

        JSONObject jsonDetails = new JSONObject();

        jsonDetails.append("mondayStart", mondayStart);
        jsonDetails.append("mondayEnd", mondayEnd);
        jsonDetails.append("tuesdayStart", tuesdayStart);
        jsonDetails.append("tuesdayEnd", tuesdayEnd);
        jsonDetails.append("wednesdayStart", wednesdayStart);
        jsonDetails.append("wednesdayEnd", wednesdayEnd);
        jsonDetails.append("thursdayStart", thursdayStart);
        jsonDetails.append("thursdayEnd", thursdayEnd);
        jsonDetails.append("fridayStart", fridayStart);
        jsonDetails.append("fridayEnd", fridayEnd);
        jsonDetails.append("saturdayStart", saturdayStart);
        jsonDetails.append("saturdayEnd", saturdayEnd);
        jsonDetails.append("sundayStart", sundayStart);
        jsonDetails.append("sundayEnd", sundayEnd);

        currPatient.setAvailability(jsonDetails.toString());

        userService.saveUser(currPatient);

        redirectAttributes.addFlashAttribute("success", "Availability Was Updated");

        String referer = request.getHeader("Referer");
        return new ModelAndView("redirect:" + referer);

    }

    //Send Message to a Patient
    @PostMapping("doctorhome/send-message")
    public ModelAndView sendMessage(ModelAndView modelAndView, User user, @RequestParam String subject,
            @RequestParam String content, RedirectAttributes redirectAttributes, @RequestParam long patientID,
            HttpServletRequest request) {
        Patient currPatient = patientRepo.getById(patientID);

        SimpleMailMessage recipientEmail = new SimpleMailMessage();

        recipientEmail.setBcc("kowalskishk@gmail.com");

        recipientEmail.setTo(currPatient.getEmail());

        // Subject of Email
        recipientEmail.setSubject(subject);

        // Email body
        recipientEmail.setText(content);

        // Email sender
        recipientEmail.setFrom(user.getEmail());

        // Send the email
        emailService.sendEmail(recipientEmail);

        redirectAttributes.addFlashAttribute("success", "Email was Sent");

        String referer = request.getHeader("Referer");
        return new ModelAndView("redirect:" + referer);
    }

    @PostMapping("doctorhome/book-appointment")
    public ModelAndView bookAppointment(ModelAndView modelAndView, User user, HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String reason,
            @RequestParam String patient) {

        Patient currUser = patientRepo.findByEmail(patient);

        Appointment newAppointment = new Appointment();

        newAppointment.setPatient(currUser);
        newAppointment.setDoctor(currUser.getDoctor());

        // 2022-04-20
        // 9:00 - 9:30

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
        newAppointment.setAcceptedPatient(false);
        newAppointment.setAcceptedDoctor(true);
        newAppointment.setComplete(false);

        appointmentRepository.save(newAppointment);

        SimpleMailMessage registrationEmail = new SimpleMailMessage();

        registrationEmail.setTo(currUser.getEmail());

        registrationEmail.setReplyTo(currUser.getDoctor().getEmail());

        registrationEmail.setBcc("kowalskishk@gmail.com");

        // Subject of Email
        registrationEmail.setSubject("You Have Been Booked at " + newAppointment.getStart());

        // Email body
        registrationEmail.setText(
                "Hi " + currUser.getFirstName() + ", \n \nYou have been booked for an appointment: \n\n\tWhen:\n\t\t "
                        + newAppointment.getStart() + " to " + newAppointment.getEnd() +
                        "\n\tDoctor:\n\t\tDr. " + currUser.getDoctor().getFirstName() + " "
                        + currUser.getDoctor().getLastName() + "\n\tReason:\n\t\t" + newAppointment.getDescription()
                        + "\n\nSign into your account to view more details!");

        // Email sender
        registrationEmail.setFrom(user.getEmail());

        // Send the email
        emailService.sendEmail(registrationEmail);

        redirectAttributes.addFlashAttribute("success", "Appointment Was Booked");

        String referer = request.getHeader("Referer");
        return new ModelAndView("redirect:" + referer);
    }

    @GetMapping("/doctorhome/contact-trace/{userId}")
    public ModelAndView getContactTrace(ModelAndView modelAndView, User user, @PathVariable("userId") long id)
            throws IOException {

        // Set view to govPatientList.html
        modelAndView.setViewName("contactTraceDoctor");

        long num = id;

        Patient test = patientRepo.getById(num);

        JSONObject elements = patientService.produceJSONforNetworkGraph(test);

        modelAndView.addObject("elements", elements.toString());

        modelAndView.addObject("url", "/doctorhome/");

        return modelAndView;
    }

    // Add functionality to flag a patient
    @GetMapping("/doctorhome/flag/{userId}")
    public ModelAndView flagPatients(ModelAndView modelAndView, HttpServletRequest request,
            @PathVariable("userId") long id, RedirectAttributes redirectAttributes) {

        String referer = request.getHeader("Referer");

        Patient patientInDB = patientRepo.getById(id);

        if (patientInDB.getEnabled() == false) {
            redirectAttributes.addFlashAttribute("error", patientInDB.getFirstName() + " has not been enabled.");
            return new ModelAndView("redirect:" + referer);
        }

        patientInDB.setIsFlagged(!patientInDB.getIsFlagged());

        patientRepo.save(patientInDB);

        if (patientInDB.getIsFlagged()) {
            redirectAttributes.addFlashAttribute("success", patientInDB.getFirstName() + " was flagged.");
        } else {
            redirectAttributes.addFlashAttribute("success", patientInDB.getFirstName() + " was unflagged.");
        }

        return new ModelAndView("redirect:" + referer);
    }

}