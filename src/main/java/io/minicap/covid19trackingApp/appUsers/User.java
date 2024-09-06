package io.minicap.covid19trackingApp.appUsers;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.Formula;

import static javax.persistence.EnumType.STRING;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User 
{

    //Defining that this attribute is a ID, Auto generated and associated with the "id" column in the DB
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    //Defining that this attribute is a email, must be unique in the DB, applies not Null to database schema, max lenght 45.
    @Column(name = "email", nullable = false, unique = true, length = 45)
    @Email(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;
    
    //Associating which column each of these are associated with.
    @Column(name = "first_name", length = 20)
    @NotEmpty(message = "Please provide your first name")
    private String firstName;
    
    @Column(name = "last_name", length = 20)
    @NotEmpty(message = "Please provide your last name")
    private String lastName;
    
    @Column(name = "password", length = 64)
    private String password;
    
    @Column(name = "user_role")
    @Enumerated(STRING)
    private userRole userRole;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dob;

    @Column(name = "gender")
    @Enumerated(STRING)
    private gender gender;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public void setId(long id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Date getDob() {
        return this.dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public gender getGender() {
        return this.gender;
    }

    public void setGender(gender gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User(String email, String firstName, String lastName, String password, userRole userRole, String confirmationToken, boolean enabled, Date dob, gender gender, String address, String phoneNumber) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.userRole = userRole;
        this.confirmationToken = confirmationToken;
        this.enabled = enabled;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public User(User user) 
    {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    } 

    public User() 
    {
    
    }

    public User(String email, String firstName, String lastName) 
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getPid() {
        return id;
    }

    public long getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public userRole getUserRole() {
        return userRole;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }
    
    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public void setPid(long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserRole(userRole appUserRole) {
        this.userRole = appUserRole;
    }

    //Temporary method to print out User details
    public String toString()
    {
        return firstName;
    }

}
