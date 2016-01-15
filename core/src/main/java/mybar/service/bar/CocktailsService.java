package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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

import java.util.ArrayList;
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
        return Lists.transform(allMenus(), new Function<Menu, IMenu>() {
            @Override
            public IMenu apply(Menu menu) {
                return menu.toDto();
            }
        });
    }

    private List<Menu> allMenus() {
        if (allMenusCached == null) {
            allMenusCached = menuDao.findAll();
        }
        return allMenusCached;
    }

    private Menu findMenuById(final int menuId) {
        return Iterables.find(allMenus(), new Predicate<Menu>() {
            @Override
            public boolean apply(Menu menu) {
                return menu.getId() == menuId;
            }
        });
    }

    // cocktails

    public List<ICocktail> getAllCocktailsForMenu(final Integer menuId) {
        Menu menu = findMenuById(menuId);
        List<Cocktail> cocktails = new ArrayList<>(menu.getCocktails());
        return Lists.transform(cocktails, new Function<Cocktail, ICocktail>() {
            @Override
            public ICocktail apply(Cocktail cocktail) {
                return cocktail.toDto();
            }
        });
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