package io.minicap.covid19trackingApp.dailyReport;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;

import io.minicap.covid19trackingApp.appUsers.Doctor;
import io.minicap.covid19trackingApp.appUsers.Patient;

@Entity
@PrimaryKeyJoinColumn(referencedColumnName = "id")
public class dailyReport 
{
    //Defining that this attribute is a ID, Auto generated and associated with the "id" column in the DB
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private Patient patient;

    @ElementCollection
    private List<String> symptoms;

    private boolean isUrgent;    
    private boolean isReviewed;
    private int statusRating;
    private String comments;
    private int temperature;
    private int weight;
    private String doctorComments;
    private String device;

    @ManyToOne(fetch=FetchType.LAZY)
    private Doctor doctorThatReviewed;


    public Doctor getDoctorThatReviewed() {
        return this.doctorThatReviewed;
    }

    public void setDoctorThatReviewed(Doctor doctorThatReviewed) {
        this.doctorThatReviewed = doctorThatReviewed;
    }

    public String getDoctorComments() {
        return this.doctorComments;
    }

    public void setDoctorComments(String doctorComments) {
        this.doctorComments = doctorComments;
    }

    public String getDevice() {
        return this.device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
    
    @CreatedDate
    @Temporal(TemporalType.DATE)
    private Date date;


    public boolean isIsUrgent() {
        return this.isUrgent;
    }

    public boolean getIsUrgent() {
        return this.isUrgent;
    }

    public void setIsUrgent(boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public List<String> getSymptoms() {
        return this.symptoms;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public boolean isIsReviewed() {
        return this.isReviewed;
    }

    public boolean getIsReviewed() {
        return this.isReviewed;
    }

    public void setIsReviewed(boolean isReviewed) {
        this.isReviewed = isReviewed;
    }

    public int getStatusRating() {
        return this.statusRating;
    }

    public void setStatusRating(int statusRating) {
        this.statusRating = statusRating;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public dailyReport(List<String> symptoms, boolean isReviewed, int statusRating, String comments, int temperature, int weight, Date date) {
        this.symptoms = symptoms;
        this.isReviewed = isReviewed;
        this.statusRating = statusRating;
        this.comments = comments;
        this.temperature = temperature;
        this.weight = weight;
        this.date = date;
    }

    public dailyReport() 
    {
        date = Calendar.getInstance().getTime();
    }
    
}
