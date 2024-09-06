package io.minicap.covid19trackingApp.appUsers;

import javax.persistence.*;

import io.minicap.covid19trackingApp.appointment.Appointment;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;

import java.util.*;

//Currently empty, will be further expanded on in sprint 2 when defining roles
@Entity
@PrimaryKeyJoinColumn(referencedColumnName = "id")
public class Doctor extends User {
    private String certification;
    private int seniority;
    private String hospital;
    private String department;

    @Column(length = 500)
    private String availabilityJSON;

    public String getAvailability() {
        return this.availabilityJSON;
    }

    public void setAvailability(String availability) {
        this.availabilityJSON = availability;
    }

    public List<dailyReport> getReportsReviewed() {
        return this.reportsReviewed;
    }

    public void setReportsReviewed(List<dailyReport> reportsReviewed) {
        this.reportsReviewed = reportsReviewed;
    }

    @OneToMany(mappedBy = "id")
    private List<Appointment> appointments;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor manager;

    @OneToMany(mappedBy = "id")
    private List<Doctor> manages;

    @OneToMany(mappedBy = "id")
    private List<Patient> patients = new ArrayList<Patient>();

    @OneToMany(mappedBy = "id")
    private List<dailyReport> reportsReviewed;

    private boolean isFull;

    public Doctor(String certification, int seniority, String hospital, String department, String availability,
            List<Appointment> appointments, Doctor manager, List<Doctor> manages, List<Patient> patients,
            List<dailyReport> reportsReviewed, boolean isFull) {
        this.certification = certification;
        this.seniority = seniority;
        this.hospital = hospital;
        this.department = department;
        this.availabilityJSON = availability;
        this.appointments = appointments;
        this.manager = manager;
        this.manages = manages;
        this.patients = patients;
        this.reportsReviewed = reportsReviewed;
        this.isFull = isFull;
    }

    public Doctor(User user) {
        super(user);
    }

    public Doctor() {
    }

    public Doctor(String email, String firstName, String lastName, String password, userRole userRole,
            String confirmationToken, boolean enabled, Date dob, gender gender, String address, String phoneNumber,
            String certification, int seniority, String hospital, String department, String availability,
            List<Appointment> appointments, Doctor manager, List<Doctor> manages, List<Patient> patients,
            List<dailyReport> reportsReviewed, boolean isFull) {
        super(email, firstName, lastName, password, userRole, confirmationToken, enabled, dob, gender, address,
                phoneNumber);
        this.certification = certification;
        this.seniority = seniority;
        this.hospital = hospital;
        this.department = department;
        this.availabilityJSON = availability;
        this.appointments = appointments;
        this.manager = manager;
        this.manages = manages;
        this.patients = patients;
        this.reportsReviewed = reportsReviewed;
        this.isFull = isFull;
    }

    public int numberOfPatient() {
        return this.patients.size();
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public List<Appointment> getAppointments() {
        return this.appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public String getCertification() {
        return this.certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public int getSeniority() {
        return this.seniority;
    }

    public void setSeniority(int seniority) {
        this.seniority = seniority;
    }

    public String getHospital() {
        return this.hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Patient> getPatients() {
        return this.patients;

    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public boolean isIsFull() {
        return this.isFull;
    }

    public boolean getIsFull() {
        return this.isFull;
    }

    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }

    public Doctor getManager() {
        return this.manager;
    }

    public void setManager(Doctor manager) {
        this.manager = manager;
    }

    public List<Doctor> getManages() {
        return this.manages;
    }

    public void setManages(List<Doctor> manages) {
        this.manages = manages;
    }

    public String toString() {
        return "Dr. " + getLastName();
    }
}
