package mybar.web.rest.rates.history;

import mybar.History;
import mybar.service.rates.history.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

// TODO: 2/6/2018 implement endpoint
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    public List<History> generateHistory(Date startDate, Date endDate) {
        if (startDate == null || endDate == null)
            return null;
        return historyService.getHistoryForPeriod(startDate, endDate);
    }

}