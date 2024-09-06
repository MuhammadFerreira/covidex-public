package io.minicap.covid19trackingApp.repository;

import io.minicap.covid19trackingApp.appUsers.Administrator;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.governmentUser;
import io.minicap.covid19trackingApp.appUsers.userRole;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//General search
@Repository("govRepository")
public interface GovRepository extends JpaRepository<governmentUser, Long> 
{
    public governmentUser findByEmail(String email); 

    

}