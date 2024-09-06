package io.minicap.covid19trackingApp.repository;

import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.infectionStatus;
import io.minicap.covid19trackingApp.appUsers.userRole;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//General search
@Repository("patientRepository")
public interface PatientRepository extends JpaRepository<Patient, Long> 
{
    public List<Patient> findAllByInfectionStatus(infectionStatus status);

    public List<Patient> findAllBynumOfDoses(int numOfDoses);

    public Patient findByEmail(String email);

    public List<Patient> findByDoctor(Doctor doctor);

    public List<Patient> findByDoctorAndIsPositive(Doctor doctor, boolean value);

    public List<Patient> findByDoctorAndIsFlagged(Doctor doctor, boolean value);

    public List<Patient> findByContactTraceFrom(Patient patient);

    public List<Patient> findByContactTraceTo(Patient patient);

}