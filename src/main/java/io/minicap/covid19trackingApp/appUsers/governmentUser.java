package io.minicap.covid19trackingApp.appUsers;
import javax.persistence.*;
import static javax.persistence.EnumType.STRING;

import java.util.Date;


//Currently empty, will be further expanded on in sprint 2 when defining roles
@Entity
@PrimaryKeyJoinColumn(referencedColumnName = "id")
public class governmentUser extends User
{
    @Enumerated(STRING)
    private govRole govRole;
    

    public governmentUser(String email, String firstName, String lastName, String password, userRole userRole, String confirmationToken, boolean enabled, Date dob, gender gender, String address, govRole govRole, String phoneNumber) {
        super(email, firstName, lastName, password, userRole, confirmationToken, enabled, dob, gender, address, phoneNumber);
        this.govRole = govRole;
    }

    public governmentUser(User user) 
    {
        super(user);
    }

    public governmentUser() 
    {
    }
    
    public govRole getGovRole() {
        return this.govRole;
    }

    public void setGovRole(govRole govRole) {
        this.govRole = govRole;
    }    
}
