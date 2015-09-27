package mybar.service.bar;

import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mybar.State;
import mybar.domain.EntityFactory;
import mybar.repository.bar.MenuDao;
import mybar.repository.bar.CocktailDao;
import mybar.repository.history.OrderDao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CocktailsService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CocktailDao cocktailDao;

    @Autowired
    private OrderDao orderDao;

    // menu

    @Transactional
    public List<IMenu> getMenus() {
        return new ArrayList<IMenu>(menuDao.findAll());
    }

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
    public void saveOrUpdateCocktail(ICocktail c) {
        Cocktail cocktail = EntityFactory.from(c);
        if (cocktail.getId() == 0) {
            cocktailDao.create(cocktail);
        } else {
            cocktailDao.update(cocktail);
        }
    }

    // todo : not for the first version.

    @Transactional
    public void removeCocktail(ICocktail d) throws Exception {
        boolean hasRef = isCocktailInHistory(d);
        if (hasRef) {
            Cocktail cocktail = EntityFactory.from(d);
            cocktail.setState(State.NOT_AVAILABLE);
            cocktailDao.update(cocktail);
        } else {
            cocktailDao.delete(d);
        }
    }

    public ICocktail findCocktail(int id) {
        return cocktailDao.read(id);
    }

    public boolean isCocktailInHistory(ICocktail cocktail) {
        return orderDao.findCocktailInHistory(cocktail);
    }

    @Transactional
    public void moveCocktailToMenu(Cocktail d, Menu c) {
        d.setMenu(c);
        cocktailDao.update(d);
    }

}