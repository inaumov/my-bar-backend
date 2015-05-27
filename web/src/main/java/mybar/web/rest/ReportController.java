package mybar.web.rest;

import mybar.Report;
import mybar.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class ReportController {

    private Date startDate, endDate;

    private List<Report> report;

    @Autowired
    private ReportService reportService;

    public ReportController() {
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void generateReport() {
        if(startDate == null || endDate == null)
            return;
        report = reportService.getOrderListFromPeriod(startDate, endDate);
    }

    public List<Report> getReport() {
        return report;
    }

    public boolean isNotEmpty() {
        return report != null && !report.isEmpty();
    }

}