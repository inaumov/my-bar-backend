package mybar.app.managedbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mybar.Report;
import mybar.service.ReportService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.util.Date;
import java.util.List;

@Component
@ManagedBean(name = "reportBean")
@RequestScoped
public class ReportBean {

    private Date startDate, endDate;

    private List<Report> report;

    @Autowired
    private ReportService reportService;

    public ReportBean() {
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