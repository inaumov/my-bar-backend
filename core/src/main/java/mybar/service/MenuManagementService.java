package mybar.service;

import mybar.api.IMenu;
import mybar.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.ActiveStatus;
import mybar.api.EntityFactory;
import mybar.api.IDrink;
import mybar.dao.MenuDao;
import mybar.dao.DrinkDao;
import mybar.dao.OrderDao;
import mybar.entity.Drink;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuManagementService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private DrinkDao drinkDao;

    @Autowired
    private OrderDao orderDao;

    // menu

    @Transactional
    public void saveOrUpdateMenu(Menu menu) {
        if (menu.getId() == 0) {
            menuDao.create(menu);
        } else {
            menuDao.update(menu);
        }
    }

    @Transactional
    public void removeMenu(Menu menu) throws Exception {
        try {
            if (menu.getDrinks().isEmpty())
                menuDao.delete(menu);
            else
                throw new Exception(MessageFormat.format("The menu {0} is not empty", menu.getName()));
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
    public void moveDrinkToMenu(Drink d, Menu c) {
        d.setMenu(c);
        drinkDao.update(d);
    }

    @Transactional
    public List<IMenu> getMenus() {
        return new ArrayList<IMenu>(menuDao.findAll());
    }

    public IDrink findDrink(int id) {
        return drinkDao.read(id);
    }

}