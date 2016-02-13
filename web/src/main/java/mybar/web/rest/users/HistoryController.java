package mybar.web.rest.users;

import mybar.History;
import mybar.service.history.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class HistoryController {

    private Date startDate, endDate;

    private List<History> history;

    @Autowired
    private HistoryService historyService;

    public HistoryController() {
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

    public void generateHistory() {
        if(startDate == null || endDate == null)
            return;
        history = historyService.getHistoryForPeriod(startDate, endDate);
    }

    public List<History> getHistory() {
        return history;
    }

    public boolean isNotEmpty() {
        return history != null && !history.isEmpty();
    }

}