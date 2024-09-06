package io.minicap.covid19trackingApp.appUsers;
import javax.persistence.*;

import org.json.JSONObject;

import io.minicap.covid19trackingApp.appointment.Appointment;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;

import static javax.persistence.EnumType.STRING;

import java.util.Date;
import java.util.List;


//Currently empty, will be further expanded on in sprint 2 when defining roles
@Entity
@PrimaryKeyJoinColumn(referencedColumnName = "id")
public class Patient extends User 
{
    private boolean isFlagged;

    @OneToMany(mappedBy = "id")
    private List<Appointment> appointments;

    @Column(length = 500)
    private String reportDetailsJSON;

    @ManyToOne(fetch=FetchType.LAZY)
    private Doctor doctor;

    private int numOfDoses;
    private boolean isPositive;

    @Enumerated(STRING)
    private varientType varientType;

    @Enumerated(STRING)
    private infectionStatus infectionStatus;

    @OneToMany(mappedBy = "id")
    private List<dailyReport> reports;

    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(
            name = "patient_patient",
            joinColumns =  { @JoinColumn(name = "patient_from_id") },
            inverseJoinColumns = { @JoinColumn(name = "patient_to_id") },
            uniqueConstraints = {
                    @UniqueConstraint(
                            columnNames = { "patient_from_id", "patient_to_id" }
                    )
            }

    )
    private List<Patient> contactTraceTo;

    @ManyToMany(mappedBy = "contactTraceTo")
    private List<Patient> contactTraceFrom;

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public List<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<Patient> getContactTraceFrom() {
        return this.contactTraceFrom;
    }

    public void addContactTraceFrom(Patient contactTraceFrom) {
        this.contactTraceFrom.add(contactTraceFrom);
    }

    public List<Patient> getContactTraceTo() {
        return this.contactTraceTo;
    }

    public void addContactTraceTo(Patient contactTraceTo) {
        this.contactTraceTo.add(contactTraceTo);
    }

    public List<dailyReport> getReports() {
        return this.reports;
    }

    public void setReports(List<dailyReport> reports) {
        this.reports = reports;
    }
    
    public String getReportDetailsJSON() {
        return this.reportDetailsJSON;
    }

    public void setReportDetailsJSON(String reportDetailsJSON) {
        this.reportDetailsJSON = reportDetailsJSON;
    }
    public Patient(String email, String firstName, String lastName, String password, userRole userRole, String confirmationToken, boolean enabled, Date dob, gender gender, String address, boolean isFlagged, Doctor doctor, int numOfDoses, boolean isPositive, varientType varientType, infectionStatus infectionStatus, String phoneNumber) 
    {
        super(email, firstName, lastName, password, userRole, confirmationToken, enabled, dob, gender, address, phoneNumber);
        this.isFlagged = isFlagged;
        this.doctor = doctor;
        this.numOfDoses = numOfDoses;
        this.isPositive = isPositive;
        this.varientType = varientType;
        this.infectionStatus = infectionStatus;
    }

    public infectionStatus getInfectionStatus() {
        return this.infectionStatus;
    }

    public void setInfectionStatus(infectionStatus infectionStatus) {
        this.infectionStatus = infectionStatus;
    }

    public Patient(User user) 
    {
        super(user);  
    }



    public boolean isIsFlagged() {
        return this.isFlagged;
    }

    public boolean getIsFlagged() {
        return this.isFlagged;
    }

    public void setIsFlagged(boolean isFlagged) {
        this.isFlagged = isFlagged;
    }

    public Doctor getDoctor() {
        return this.doctor;
    }

    public Patient() 
    {
    
    }

    public void setDoctor(Doctor doctor) 
    {
        this.doctor = doctor;

        if(doctor.getPatients().size() > 10)
        {
            doctor.setIsFull(true);
        }
        else
        {
            doctor.setIsFull(false);
        }
    }

    public int getNumOfDoses() {
        return this.numOfDoses;
    }

    public void setNumOfDoses(int numOfDoses) {
        this.numOfDoses = numOfDoses;
    }

    public boolean isIsPositive() {
        return this.isPositive;
    }

    public boolean getIsPositive() {
        return this.isPositive;
    }

    public void setIsPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }

    public void setContactTraceTo(List<Patient> contactTraceTo)
    {
        this.contactTraceTo = contactTraceTo;
    }

    public void setContactTraceFrom(List<Patient> contactTraceFrom)
    {
        this.contactTraceFrom = contactTraceFrom;
    }

    public varientType getVarientType() {
        return this.varientType;
    }

    public void setVarientType(varientType varientType) {
        this.varientType = varientType;
    } 
    
}
