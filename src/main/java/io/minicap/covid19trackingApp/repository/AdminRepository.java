package io.minicap.covid19trackingApp.repository;

import io.minicap.covid19trackingApp.appUsers.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//General search
@Repository("adminRepository")
public interface AdminRepository extends JpaRepository<Administrator, Long> 
{
    public Administrator findByEmail(String email); 
}