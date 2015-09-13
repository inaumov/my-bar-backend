package mybar.service;

import mybar.api.ICocktail;
import mybar.api.IMenu;
import mybar.domain.Cocktail;
import mybar.domain.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.State;
import mybar.domain.EntityFactory;
import mybar.repository.MenuDao;
import mybar.repository.CocktailDao;
import mybar.repository.OrderDao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuManagementService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CocktailDao cocktailDao;

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
            if (menu.getCocktails().isEmpty())
                menuDao.delete(menu);
            else
                throw new Exception(MessageFormat.format("The menu {0} is not empty", menu.getName()));
        } finally {
        }
    }

    // cocktails

    @Transactional
    public void saveOrUpdateCocktail(ICocktail d) {
        Cocktail cocktail = EntityFactory.from(d);
        if (cocktail.getId() == 0) {
            cocktailDao.create(cocktail);
        } else {
            cocktailDao.update(cocktail);
        }
    }

    @Transactional
    public void removeCocktail(ICocktail d) throws Exception {
        boolean hasRef = orderDao.findCocktailInHistory(d);
        if (hasRef) {
            Cocktail cocktail = EntityFactory.from(d);
            cocktail.setState(State.NOT_AVAILABLE);
            cocktailDao.update(cocktail);
        } else {
            cocktailDao.delete(d);
        }
    }

    public boolean cocktailIsInHistory(ICocktail cocktail) {
        boolean hasRef = orderDao.findCocktailInHistory(cocktail);
        return hasRef;
    }

    @Transactional
    public void moveCocktailToMenu(Cocktail d, Menu c) {
        d.setMenu(c);
        cocktailDao.update(d);
    }

    @Transactional
    public List<IMenu> getMenus() {
        return new ArrayList<IMenu>(menuDao.findAll());
    }

    public ICocktail findCocktail(int id) {
        return cocktailDao.read(id);
    }

}