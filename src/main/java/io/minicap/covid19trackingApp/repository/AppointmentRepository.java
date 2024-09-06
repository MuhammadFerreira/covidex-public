package io.minicap.covid19trackingApp.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appointment.Appointment;

@Repository("appointmentRepository")
public interface AppointmentRepository extends JpaRepository<Appointment, Long>  
{

    public List<Appointment> findByDoctor(Doctor doctor);
    
}
