package io.minicap.covid19trackingApp.repository;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


//General search
@Repository("reportRepository")
public interface ReportRepository extends JpaRepository<dailyReport, Long> 
{
    public List <dailyReport> findAllByPatient(Patient patient);

    public List <dailyReport> findAllByPatientAndIsReviewed(Patient patient, boolean value);
}