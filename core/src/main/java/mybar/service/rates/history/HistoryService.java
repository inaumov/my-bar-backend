package mybar.service.rates.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.History;
import mybar.repository.rates.RatesDao;

import java.util.Date;
import java.util.List;

@Service
public class HistoryService {

    private final RatesDao ratesDao;

    @Autowired
    public HistoryService(RatesDao ratesDao) {
        this.ratesDao = ratesDao;
    }

    @Transactional
    public List<History> getHistoryForPeriod(Date startDate, Date endDate) {
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(endDate.getTime());
        return ratesDao.getRatedCocktailsForPeriod(start, end);
    }

}