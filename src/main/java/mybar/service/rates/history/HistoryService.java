package mybar.service.rates.history;

import mybar.domain.History;
import mybar.repository.rates.RatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class HistoryService {

    private final RatesDao ratesDao;

    @Autowired
    public HistoryService(RatesDao ratesDao) {
        this.ratesDao = ratesDao;
    }

    public List<History> getHistoryForPeriod(LocalDate startDate, LocalDate endDate) {

        startDate = startDate != null ? startDate : Year.parse("2008").atDay(1);
        endDate = endDate != null ? endDate : LocalDate.now();

        return ratesDao.getRatedCocktailsForPeriod(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

}