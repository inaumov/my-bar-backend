package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.Report;
import mybar.dao.OrderDAO;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private OrderDAO orderDao;

    @Transactional
    public List<Report> getOrderListFromPeriod(Date startDate, Date endDate) {
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(endDate.getTime());
        List<Report> orders = orderDao.getReportForPeriod(start, end);
        return orders;
    }

}