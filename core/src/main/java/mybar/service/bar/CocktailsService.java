package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mybar.State;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.CocktailToIngredient;
import mybar.domain.bar.Menu;
import mybar.exception.CocktailNotFoundException;
import mybar.repository.bar.CocktailDao;
import mybar.repository.bar.MenuDao;
import mybar.repository.history.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CocktailsService {

    @Autowired(required = false)
    private MenuDao menuDao;

    @Autowired(required = false)
    private CocktailDao cocktailDao;

    @Autowired(required = false)
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

    public Map<String, List<ICocktail>> getAllCocktails() {
        Map<String, List<ICocktail>> cocktails = Maps.newHashMap();
        for (Menu menu : allMenusCached) {
            cocktails.put(menu.toString(), Lists.transform((List<Cocktail>) menu.getCocktails(), new Function<Cocktail, ICocktail>() {
                @Override
                public ICocktail apply(Cocktail cocktail) {
                    return cocktail.toDto();
                }
            }));
        }
        return cocktails;
    }

    public ICocktail saveOrUpdateCocktail(ICocktail cocktail) throws CocktailNotFoundException {
        if (cocktail.getId() == 0) {
            try {
                Cocktail entity = EntityFactory.from(cocktail);
                Menu menuById = findMenuById(cocktail.getMenuId());
                menuById.addCocktail(entity);

                Cocktail created = cocktailDao.create(entity);
                //all.add(entity);
                return created.toDto();
            } catch (EntityExistsException e) {
                return null;
            }
        } else {
            Cocktail cocktailFromDb = cocktailDao.read(cocktail.getId());
            cocktailFromDb.getMenu().getCocktails().remove(cocktailFromDb);

            Cocktail entity = EntityFactory.from(cocktail);
            entity.setId(cocktail.getId());
            entity.setName(cocktail.getName());
            entity.setState(cocktail.getState());
            entity.setDescription(cocktail.getDescription());
            entity.setImageUrl(cocktail.getImageUrl());
            Menu menuById = findMenuById(cocktail.getMenuId());
            menuById.addCocktail(entity);

            Cocktail updated = cocktailDao.update(entity);
            return updated.toDto();
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

    public ICocktail findCocktailById(int id) throws CocktailNotFoundException {
        return cocktailDao.read(id).toDto();
    }

    public boolean isCocktailInHistory(ICocktail cocktail) {
        return orderDao.findCocktailInHistory(cocktail);
    }

    public void deleteCocktailById(int id) throws CocktailNotFoundException {
        cocktailDao.delete(id);
        if (allMenusCached == null) {
            return;
        }
        for (Menu m : allMenusCached) {
            Collection<Cocktail> cocktails = m.getCocktails();
            for (Cocktail cocktail : cocktails) {
                if (cocktail.getId() == id) {
                    cocktails.remove(cocktail);
                    break;
                }
            }
        }
    }

}