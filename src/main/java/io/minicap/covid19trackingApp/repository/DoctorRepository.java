package io.minicap.covid19trackingApp.repository;

import io.minicap.covid19trackingApp.appUsers.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("doctorRepository")
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor findByEmail(String name);
    
}
