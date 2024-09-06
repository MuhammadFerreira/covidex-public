package io.minicap.covid19trackingApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.minicap.covid19trackingApp.appUsers.Patient;
import io.minicap.covid19trackingApp.dailyReport.dailyReport;
import io.minicap.covid19trackingApp.repository.ReportRepository;

@Service("reportService")
public class ReportService 
{
    @Autowired
    private ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public ReportService() {
    }

    public void saveDailyReport(dailyReport dailyReport) 
    {
        reportRepository.save(dailyReport);
    }
    
}
