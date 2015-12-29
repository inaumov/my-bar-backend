package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import mybar.State;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.repository.bar.CocktailDao;
import mybar.repository.bar.MenuDao;
import mybar.repository.history.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CocktailsService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CocktailDao cocktailDao;

    @Autowired
    private OrderDao orderDao;

    private List<Menu> allMenusCached;

    // menu

    public List<IMenu> getAllMenuItems() {
        return new ArrayList<IMenu>(getAllMenus());
    }

    private List<Menu> getAllMenus() {
        if (allMenusCached == null) {
            allMenusCached = menuDao.findAll();
        }
        return allMenusCached;
    }

    private Menu findMenuById(int menuId) {
        for (Menu menu : getAllMenus()) {
            if (menu.getId() == menuId) {
                return menu;
            }
        }
        return null;
    }

    private void saveOrUpdateMenu(IMenu menu) {
        Menu entity = EntityFactory.from(menu);
        if (menu.getId() == 0) {
            menuDao.create(entity);
        } else {
            menuDao.update(entity);
        }
    }

    private void removeMenu(Menu menu) throws Exception {
        try {
            if (menu.getCocktails().isEmpty())
                menuDao.delete(menu);
            else
                throw new Exception(MessageFormat.format("The menu {0} is not empty", menu.getName()));
        } finally {
        }
    }

    // cocktails

    public List<ICocktail> getAllCocktailsForMenu(Integer menuId) {
        List<Menu> menuList = getAllMenus();
        for (Menu menu : menuList) {
            if (menu.getId() == menuId) {
                List<Cocktail> cocktails = new ArrayList<>(menu.getCocktails());
                return Lists.transform(cocktails, new Function<Cocktail, ICocktail>() {
                    @Override
                    public ICocktail apply(Cocktail cocktail) {
                        return cocktail.toDto();
                    }
                });
            }
        }
        return Collections.emptyList();
    }

    public void saveOrUpdateCocktail(ICocktail cocktail) {
        Cocktail entity = EntityFactory.from(cocktail);
        entity.setMenu(findMenuById(cocktail.getMenuId()));
        if (entity.getId() == 0) {
            cocktailDao.create(entity);
        } else {
            cocktailDao.update(entity);
        }
    }

    public void removeCocktail(ICocktail cocktail) throws Exception {
        boolean hasRef = isCocktailInHistory(cocktail);
        if (hasRef) {
            Cocktail entity = EntityFactory.from(cocktail);
            entity.setState(State.NOT_AVAILABLE);
            cocktailDao.update(entity);
        } else {
            cocktailDao.delete(cocktail);
        }
    }

    public boolean isCocktailExist(ICocktail cocktail) {
        return false;
    }

    public ICocktail findCocktailById(int id) {
        return cocktailDao.read(id).toDto();
    }

    public boolean isCocktailInHistory(ICocktail cocktail) {
        return orderDao.findCocktailInHistory(cocktail);
    }

}