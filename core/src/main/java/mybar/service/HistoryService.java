package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.History;
import mybar.repository.OrderDao;

import java.util.Date;
import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private OrderDao orderDao;

    @Transactional
    public List<History> getHistoryForPeriod(Date startDate, Date endDate) {
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(endDate.getTime());
        List<History> historyList = orderDao.getHistoryForPeriod(start, end);
        return historyList;
    }

}