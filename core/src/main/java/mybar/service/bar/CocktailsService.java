package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
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
import mybar.exception.UniqueCocktailNameException;
import mybar.repository.bar.CocktailDao;
import mybar.repository.bar.MenuDao;
import mybar.repository.history.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    Function<Cocktail, ICocktail> cocktailFunction = new Function<Cocktail, ICocktail>() {
        @Override
        public ICocktail apply(Cocktail cocktail) {
            return cocktail.toDto();
        }
    };
    Function<Menu, IMenu> menuFunction = new Function<Menu, IMenu>() {
        @Override
        public IMenu apply(Menu menu) {
            return menu.toDto();
        }
    };

    // menu

    public List<IMenu> getAllMenuItems() {
        return Lists.transform(allMenus(), menuFunction);
    }

    private List<Menu> allMenus() {
        if (allMenusCached == null || allMenusCached.isEmpty()) {
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
        for (Menu menu : allMenus()) {
            cocktails.put(menu.getName(), FluentIterable.from(menu.getCocktails()).transform(cocktailFunction).toList());
        }
        return cocktails;
    }

    public ICocktail saveCocktail(ICocktail cocktail) throws UniqueCocktailNameException {
        checkCocktailExists(cocktail);
        Cocktail entity = EntityFactory.from(cocktail);
        Menu menuById = findMenuById(cocktail.getMenuId());
        menuById.addCocktail(entity);
        try {
            Cocktail created = performSaveOrUpdate(entity);
            return created.toDto();
        } catch (EntityExistsException e) {
            return null;
        }
    }

    public ICocktail updateCocktail(ICocktail cocktail) throws CocktailNotFoundException {
        Cocktail cocktailFromDb = cocktailDao.read(cocktail.getId());
        copyCocktailFieldsForUpdate(cocktailFromDb, cocktail);
        Cocktail updated = performSaveOrUpdate(cocktailFromDb);
        return updated.toDto();
    }

    private void copyCocktailFieldsForUpdate(Cocktail destination, ICocktail source) {
        Cocktail existedEntity = (Cocktail) destination;
        Objects.requireNonNull(existedEntity.getId());
        existedEntity.setName(source.getName());
        existedEntity.setDescription(source.getDescription());
        existedEntity.setImageUrl(source.getImageUrl());
        existedEntity.setMenu(findMenuById(source.getMenuId()));
        existedEntity.getCocktailToIngredientList().clear();
        List<CocktailToIngredient> cocktailToIngredientList = EntityFactory.from(source).getCocktailToIngredientList();
        for (CocktailToIngredient cocktailToIngredient : cocktailToIngredientList) {
            existedEntity.addCocktailToIngredient(cocktailToIngredient);
        }
    }

    private Cocktail performSaveOrUpdate(Cocktail cocktail) {
        allMenusCached.clear();
        if (cocktail.getId() == 0) {
            Cocktail created = cocktailDao.create(cocktail);
            return created;
        } else {
            Cocktail updated = cocktailDao.update(cocktail);
            return updated;
        }
    }

    private void checkCocktailExists(ICocktail cocktail) throws UniqueCocktailNameException {
        if (cocktailDao.findCocktailByName(cocktail.getName())) {
            throw new UniqueCocktailNameException(cocktail.getName());
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
        try {
            checkCocktailExists(cocktail);
        } catch (UniqueCocktailNameException e) {
            return true;
        }
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