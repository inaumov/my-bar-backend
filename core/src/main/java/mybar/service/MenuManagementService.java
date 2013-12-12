package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.ActiveStatus;
import mybar.api.EntityFactory;
import mybar.api.IDish;
import mybar.dao.CategoryDAO;
import mybar.dao.DishDAO;
import mybar.dao.OrderDAO;
import mybar.entity.Category;
import mybar.entity.Dish;

import java.util.List;

@Service
public class MenuManagementService {

    @Autowired
    private CategoryDAO categoryDao;

    @Autowired
    private DishDAO dishDao;

    @Autowired
    private OrderDAO orderDao;

    // categories

    @Transactional
    public void saveOrUpdateCategory(Category c) {
        if (c.getId() == 0) {
            categoryDao.create(c);
        } else {
            categoryDao.update(c);
        }
    }

    @Transactional
    public void removeCategory(Category c) throws Exception {
        try {
            if (c.getDishes().isEmpty())
                categoryDao.delete(c);
            else
                throw new Exception("The menu for this category " + c.getName() + " is not empty");
        } finally {
        }
    }

    // dishes

    @Transactional
    public void saveOrUpdateDish(IDish d) {
        Dish dish = EntityFactory.from(d);
        if (dish.getId() == 0) {
            dishDao.create(dish);
        } else {
            dishDao.update(dish);
        }
    }

    @Transactional
    public void removeDish(IDish d) throws Exception {
        boolean hasRef = orderDao.findDishInHistory(d);
        if (hasRef) {
            Dish dish = EntityFactory.from(d);
            dish.setActiveStatus(ActiveStatus.DISABLED);
            dishDao.update(dish);
        } else {
            dishDao.delete(d);
        }
    }

    public boolean dishIsInHistory(IDish dish) {
        boolean hasRef = orderDao.findDishInHistory(dish);
        return hasRef;
    }

    @Transactional
    public void moveDishToCategory(Dish d, Category c) {
        d.setCategory(c);
        dishDao.update(d);
    }

    @Transactional
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    public IDish findDish(int id) {
        return dishDao.read(id);
    }
}