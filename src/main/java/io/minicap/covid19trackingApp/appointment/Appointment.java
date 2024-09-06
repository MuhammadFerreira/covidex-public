package io.minicap.covid19trackingApp.appointment;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appUsers.Patient;

@Entity
@PrimaryKeyJoinColumn(referencedColumnName = "id")
public class Appointment 
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String description;

    private Date start;
    private Date end;


    @ManyToOne(fetch=FetchType.LAZY)
    private Doctor doctor;

    @ManyToOne(fetch=FetchType.LAZY)
    private Patient patient;

    private boolean complete;

    private boolean acceptedDoctor;
    private boolean acceptedPatient;

    private String linkToMeeting;


    public Appointment() {
    }


    public Appointment(long id, String description, Date start, Date end, Doctor doctor, Patient patient, boolean complete, boolean acceptedDoctor, boolean acceptedPatient, String linkToMeeting) {
        this.id = id;
        this.description = description;
        this.start = start;
        this.end = end;
        this.doctor = doctor;
        this.patient = patient;
        this.complete = complete;
        this.acceptedDoctor = acceptedDoctor;
        this.acceptedPatient = acceptedPatient;
        this.linkToMeeting = linkToMeeting;
    }


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return this.start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return this.end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public boolean getComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isAcceptedDoctor() {
        return this.acceptedDoctor;
    }

    public boolean getAcceptedDoctor() {
        return this.acceptedDoctor;
    }

    public void setAcceptedDoctor(boolean acceptedDoctor) {
        this.acceptedDoctor = acceptedDoctor;
    }

    public boolean isAcceptedPatient() {
        return this.acceptedPatient;
    }

    public boolean getAcceptedPatient() {
        return this.acceptedPatient;
    }

    public void setAcceptedPatient(boolean acceptedPatient) {
        this.acceptedPatient = acceptedPatient;
    }

    public String getLinkToMeeting() {
        return this.linkToMeeting;
    }

    public void setLinkToMeeting(String linkToMeeting) {
        this.linkToMeeting = linkToMeeting;
    }




    
}
