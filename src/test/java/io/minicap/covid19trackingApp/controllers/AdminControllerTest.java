// package io.minicap.covid19trackingApp.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.mock.web.MockHttpServletRequest;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.security.test.context.support.WithUserDetails;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MockMvcBuilder;
// import org.springframework.test.web.servlet.ResultMatcher;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.ui.Model;

// import io.minicap.covid19trackingApp.appUsers.Patient;
// import io.minicap.covid19trackingApp.Covid19trackingAppApplication;
// import io.minicap.covid19trackingApp.appUsers.Administrator;
// import io.minicap.covid19trackingApp.appUsers.Doctor;

// import io.minicap.covid19trackingApp.appUsers.User;
// import io.minicap.covid19trackingApp.appUsers.gender;
// import io.minicap.covid19trackingApp.appUsers.infectionStatus;
// import io.minicap.covid19trackingApp.appUsers.userRole;
// import io.minicap.covid19trackingApp.appUsers.varientType;
// import io.minicap.covid19trackingApp.repository.AdminRepository;
// import io.minicap.covid19trackingApp.repository.DoctorRepository;
// import io.minicap.covid19trackingApp.repository.PatientRepository;
// import io.minicap.covid19trackingApp.repository.UserRepository;
// import io.minicap.covid19trackingApp.service.EmailService;
// import io.minicap.covid19trackingApp.service.UserService;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.text.ParseException;
// import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.Calendar;
// import java.util.Collection;
// import java.util.List;

// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
// import org.springframework.security.core.Authentication;
// import org.junit.jupiter.api.Test;
// import org.mockito.Answers;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;

// @SpringBootTest
// @AutoConfigureMockMvc(addFilters = false)
// public class AdminControllerTest {
    
//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserRepository userRepo;

//     @MockBean
//     private AdminRepository adminRepo;

//     @MockBean
//     private PatientRepository patientRepo;

//     @MockBean
//     private DoctorRepository doctorRepo;

//     @Test
//     public void contextLoads()
//     {
//         assertNotNull(mockMvc);
//         assertNotNull(adminRepo);
//         assertNotNull(userRepo);
//         assertNotNull(patientRepo);
//         assertNotNull(doctorRepo);
//     }



// @Test
// public void showAdminHomeTest() throws Exception
// {
//     List<User> patientList = new ArrayList<>();
//     Patient p1 = new Patient();
//     Patient p2 = new Patient();
//     Patient p3 = new Patient();

//     //InfectionStatus symptomatic = new infectionStatus();


//    // p1.editProfilePost()
   
//     List<User> doctorList = new ArrayList<>();
//     Doctor d1 = new Doctor();
//     Doctor d2 = new Doctor();
//     Doctor d3 = new Doctor();

//     Patient p4 = new Patient();
//     Patient p5 = new Patient();
//     Patient p6 = new Patient();
    
//     //set dummy patients num of doses
//    // p1.setNumOfDoses(2);
//     //p2.setNumOfDoses(4);
//     //p3.setNumOfDoses(1);
//     //p4.setNumOfDoses(3);
//     //p5.setNumOfDoses(0);
//    // p6.setNumOfDoses(2);

//     patientList.add(p1);
//     patientList.add(p2);
//     patientList.add(p3);

//     doctorList.add(d1);
//     doctorList.add(d2);
//     doctorList.add(d3);

//     //int patients = ;

//     Mockito.when(userRepo.findAllByUserRole(userRole.PATIENT)).thenReturn(patientList);

//     Mockito.when(userRepo.findAllByUserRole(userRole.DOCTOR)).thenReturn(doctorList);

//     // mockMvc.perform(MockMvcRequestBuilders.get("/govhome"))
//     // .andExpect(MockMvcResultMatchers.status().isOk());
//     // .andExpect(MockMvcResultMatchers.view().name("governmentDashboard"));

// }




//     @Test
//     @WithMockUser (roles = {})
//     public void addDailyReportNullUserTest () throws Exception
//     {
//         mockMvc.perform(MockMvcRequestBuilders.post("/patienthome/add-daily-report")).andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();

//     }

//     @Test
//     @WithMockUser(username = "Jynx@gmail.com", roles = {"ADMINISTRATOR"})
//     public void editProfilePostTest() throws Exception
//     {
//         //given an admin
//         String email = "Jynx@gmail.com";
//         Administrator testAdmin = new Administrator();
//         testAdmin.setEmail(email);
//         testAdmin.setUserRole(userRole.ADMINISTRATOR);
//         //when
//         Authentication auth = Mockito.mock(Authentication.class);
//         when(auth.getName()).thenReturn(email);
//         when(adminRepo.findByEmail(email)).thenReturn(testAdmin);
//         //then
//         mockMvc.perform(MockMvcRequestBuilders.post("/adminhome/edit-profile")
//             .param("gender", "female")
//             .param("address", "420 Avenue Jynx")
//             .param("birthDate", "1969-04-20")
//             .param("phoneNumber", "4180123456"))
//             .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//             .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "Administrator Profile Was Updated"));
//     }

//     @Test
//     @WithMockUser(username = "Jynx@gmail.com", roles = {"ADMINISTRATOR"})
//     public void showPatientsTest() throws Exception
//     {
//         //given a list of patients and doctors
//         Administrator admin = new Administrator();
//         admin.setEmail("Jynx@gmail.com");

//         List<Doctor> doctorList = new ArrayList<>();
//         Doctor d1 = new Doctor();
//         d1.setEmail("doc1@gmail.com");
//         d1.setFirstName("Doctor");

//         Doctor d2 = new Doctor();
//         d2.setEmail("doc2@gmail.com");
//         d2.setFirstName("Doctor");

//         Doctor d3 = new Doctor();
//         d3.setEmail("doc3@gmail.com");
//         d3.setFirstName("Doctor");

//         List<Patient> patientList = new ArrayList<>();
//         Patient p1 = new Patient();
//         p1.setDoctor(d1);
//         p1.setIsPositive(true);
//         p1.setIsFlagged(true);
//         p1.setEmail("pat1@gmail.com");
//         p1.setVarientType(varientType.alpha);
//         p1.setInfectionStatus(infectionStatus.symptomatic);
//         p1.setFirstName("Patrick");

//         Patient p2 = new Patient();
//         p2.setDoctor(d2);
//         p2.setIsPositive(false);
//         p2.setIsFlagged(false);
//         p2.setEmail("pat2@gmail.com");
//         p2.setVarientType(varientType.none);
//         p2.setInfectionStatus(infectionStatus.none);
//         p2.setFirstName("Patrick");

//         Patient p3 = new Patient();
//         p3.setDoctor(d3);
//         p3.setIsPositive(true);
//         p3.setIsFlagged(true);
//         p3.setEmail("pat3@gmail.com");
//         p3.setVarientType(varientType.beta);
//         p3.setInfectionStatus(infectionStatus.contactTraced);
//         p1.setFirstName("Patrick");

//         patientList.add(p1);
//         patientList.add(p2);
//         patientList.add(p3);    

//         doctorList.add(d1);
//         doctorList.add(d2);
//         doctorList.add(d3);

//         //when
//         when(adminRepo.findByEmail("Jynx@gmail.com")).thenReturn(admin);
//         when(patientRepo.findAll()).thenReturn(patientList);
//         when(doctorRepo.findAll()).thenReturn(doctorList);

//         //then
//         mockMvc.perform(MockMvcRequestBuilders.get("/adminhome/patients"))
//             .andExpect(MockMvcResultMatchers.status().isOk())
//             .andExpect(MockMvcResultMatchers.model().attribute("admin", admin))
//             .andExpect(MockMvcResultMatchers.view().name("adminPatientList"))
//             .andExpect(MockMvcResultMatchers.model().attribute("listPatients", patientList))
//             .andExpect(MockMvcResultMatchers.model().attribute("listDoctors", doctorList));
//     }

//     @Test
//     @WithMockUser(username = "Jynx@gmail.com", roles = {"ADMINISTRATOR"})
//     public void processAssignUsersTest() throws Exception
//     {
//         //given a patient and a doctor
//         Patient pat = new Patient();
//         pat.setId(10005);
//         pat.setEmail("Bulbasaur@gmail.com");

//         Doctor doc = new Doctor();
//         doc.setId(10009);
//         doc.setEmail("Exeggutor@gmail.com");

//         //when
//         when(userRepo.findByEmail("Bulbasaur@gmail.com")).thenReturn(pat);
//         when(patientRepo.getById((long) 10005)).thenReturn(pat);
//         when(userRepo.findByEmail("Exeggutor@gmail.com")).thenReturn(doc);
//         when(doctorRepo.getById((long) 10009)).thenReturn(doc);

//         //then
//         mockMvc.perform(MockMvcRequestBuilders.post("/adminhome/patients")
//             .param("patientEmail", "Bulbasaur@gmail.com")
//             .param("doctorEmail", "Exeggutor@gmail.com"))
//             .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//             .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "Patient has Been Assigned"));

//         /*--------------------*/
//         //when patient or doctor doesn't exist
//         mockMvc.perform(MockMvcRequestBuilders.post("/adminhome/patients")
//             .param("patientEmail", "haha@gmail.com")
//             .param("doctorEmail", "jokes@gmail.com"))
//             .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//             .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "Unable to Process Request"));
        

//     }

//     @Test
//     public void flagPatientsTest() throws Exception
//     {
//         //given a patient and a doctor
//         Patient pat = new Patient();
//         pat.setId(10005);
//         pat.setEmail("Bulbasaur@gmail.com");
//         pat.setIsFlagged(false);
        
//         //when
//         when(userRepo.findByEmail("Bulbasaur@gmail.com")).thenReturn(pat);
//         when(patientRepo.getById((long) 10005)).thenReturn(pat);

//         //then
//         mockMvc.perform(MockMvcRequestBuilders.post("/adminhome/patients/flag")
//             .param("patientEmail", "Bulbasaur@gmail.com"))
//             .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

//     }

//     @Test
//     @WithMockUser(username = "Jynx@gmail.com", roles = {"ADMINISTRATOR"})
//     public void showDoctorPatientStatusTest() throws Exception
//     {
//         //given a patient
//         Patient pat = new Patient();
//         String birthDate = "1990-08-12";
//         pat.setDob(new SimpleDateFormat("yyyy/MM/dd").parse(birthDate.replace('-', '/')));
//         long id = 1005;
//         pat.setId(id);
//         Calendar calendar = Calendar.getInstance();
//         calendar.setTime(pat.getDob());
//         int birthYear = calendar.get(Calendar.YEAR);

//         Administrator admin = new Administrator();
//         admin.setEmail("Jynx@gmail.com");

//         //when
//         when(adminRepo.findByEmail("Jynx@gmail.com")).thenReturn(admin);
//         when(patientRepo.getById(id)).thenReturn(pat);

//         //then
//         mockMvc.perform(MockMvcRequestBuilders.get("/adminhome/patients/{userId}", id))
//             .andExpect(MockMvcResultMatchers.status().isOk())
//             .andExpect(MockMvcResultMatchers.model().attribute("patientAge", Calendar.getInstance().get(Calendar.YEAR) - birthYear))
//             .andExpect(MockMvcResultMatchers.model().attribute("patient", pat))
//             .andExpect(MockMvcResultMatchers.view().name("adminPatientStatus"));
//     }

//     @Test
//     public void markDoctorFullTest() throws Exception
//     {
//         //given a doctor
//         String email = "Exeggutor@gmail.com";
//         long id = 10007;

//         Doctor doctor = new Doctor();
//         doctor.setEmail(email);
//         doctor.setIsFull(false);
//         doctor.setId(id);

//         //when
//         when(userRepo.findByEmail(email)).thenReturn(doctor);
//         when(doctorRepo.getById(id)).thenReturn(doctor);

//         //then
//         mockMvc.perform(MockMvcRequestBuilders.post("/adminhome/doctors/markFull")
//             .param("doctorEmail", "Exeggutor@gmail.com"))
//             .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
//             .andExpect(MockMvcResultMatchers.model().attribute("confirmationMessage", "Doctor status updated"));
//     }

// }

