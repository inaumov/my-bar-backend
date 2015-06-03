package mybar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.ActiveStatus;
import mybar.api.EntityFactory;
import mybar.api.IDrink;
import mybar.dao.CategoryDao;
import mybar.dao.DrinkDao;
import mybar.dao.OrderDao;
import mybar.entity.Category;
import mybar.entity.Drink;

import java.util.List;

@Service
public class MenuManagementService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private DrinkDao drinkDao;

    @Autowired
    private OrderDao orderDao;

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
            if (c.getDrinks().isEmpty())
                categoryDao.delete(c);
            else
                throw new Exception("The menu for this category " + c.getName() + " is not empty");
        } finally {
        }
    }

    // drinks

    @Transactional
    public void saveOrUpdateDrink(IDrink d) {
        Drink drink = EntityFactory.from(d);
        if (drink.getId() == 0) {
            drinkDao.create(drink);
        } else {
            drinkDao.update(drink);
        }
    }

    @Transactional
    public void removeDrink(IDrink d) throws Exception {
        boolean hasRef = orderDao.findDrinkInHistory(d);
        if (hasRef) {
            Drink drink = EntityFactory.from(d);
            drink.setActiveStatus(ActiveStatus.DISABLED);
            drinkDao.update(drink);
        } else {
            drinkDao.delete(d);
        }
    }

    public boolean drinkIsInHistory(IDrink drink) {
        boolean hasRef = orderDao.findDrinkInHistory(drink);
        return hasRef;
    }

    @Transactional
    public void moveDrinkToCategory(Drink d, Category c) {
        d.setCategory(c);
        drinkDao.update(d);
    }

    @Transactional
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    public IDrink findDrink(int id) {
        return drinkDao.read(id);
    }
}