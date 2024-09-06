package io.minicap.covid19trackingApp.appUsers;

import java.util.Date;

import javax.persistence.*;


//Currently empty, will be further expanded on in sprint 2 when defining roles
@Entity
@PrimaryKeyJoinColumn(referencedColumnName = "id")
public class Administrator extends User
{

    public Administrator(String email, String firstName, String lastName, String password, userRole userRole, String confirmationToken, boolean enabled, Date dob, gender gender, String address, String phoneNumber) 
    {
        super(email, firstName, lastName, password, userRole, confirmationToken, enabled, dob, gender,address, phoneNumber);
    }

    public Administrator(User user) 
    {
        super(user);
    }

    public Administrator() 
    {
    }

    
}
