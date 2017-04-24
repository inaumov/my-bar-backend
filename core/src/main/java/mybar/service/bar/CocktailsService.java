package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IMenu;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.exception.CocktailNotFoundException;
import mybar.exception.UnknownMenuException;
import mybar.exception.UniqueCocktailNameException;
import mybar.repository.bar.CocktailDao;
import mybar.repository.bar.MenuDao;
import mybar.repository.history.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.*;

@Service
@Transactional
public class CocktailsService {

    @Autowired(required = false)
    private MenuDao menuDao;

    @Autowired(required = false)
    private CocktailDao cocktailDao;

    @Autowired(required = false)
    private OrderDao orderDao;

    private Map<IMenu, Collection<ICocktail>> allMenusCached = new HashMap<>();

    private Function<Menu, IMenu> menuFunction = new Function<Menu, IMenu>() {
        @Override
        public IMenu apply(Menu menu) {
            return menu.toDto();
        }
    };

    // menu

    public Collection<IMenu> getAllMenuItems() {
        return allMenusCached.keySet();
    }

    private Map<IMenu, Collection<ICocktail>> allMenus() {
        if (allMenusCached == null || allMenusCached.isEmpty()) {
            List<Menu> all = menuDao.findAll();
            for (final Menu menu : all) {
                allMenusCached.put(
                        menuFunction.apply(menu),
                        FluentIterable.from(menu.getCocktails()).transform(new Function<Cocktail, ICocktail>() {
                            @Override
                            public ICocktail apply(Cocktail cocktail) {
                                return cocktail.toDto(menu.getName());
                            }
                        }).toList());
            }
        }
        return allMenusCached;
    }

    private IMenu findMenuById(final int menuId) {
        return Iterables.find(allMenus().keySet(), new Predicate<IMenu>() {
            @Override
            public boolean apply(IMenu menu) {
                return menu.getId() == menuId;
            }
        });
    }

    private IMenu findMenuByName(final String menuName) {
        Optional<IMenu> relatedMenu = Iterables.tryFind(allMenus().keySet(), new Predicate<IMenu>() {
            @Override
            public boolean apply(IMenu menu) {
                return menu.getName().equals(menuName);
            }
        });
        if (!relatedMenu.isPresent()) {
            throw new UnknownMenuException(menuName);
        }
        return relatedMenu.get();
    }

    // cocktails

    public List<ICocktail> getAllCocktailsForMenu(final String menuName) {
        Optional<IMenu> menuOptional = Iterables.tryFind(allMenus().keySet(), new Predicate<IMenu>() {
            @Override
            public boolean apply(IMenu menu) {
                return menu.getName().equals(menuName);
            }
        });
        if(menuOptional.isPresent()) {
            return ImmutableList.copyOf(allMenus().get(menuOptional.get()));
        }
        return Collections.emptyList();
    }

    public Map<String, List<ICocktail>> getAllCocktails() {
        Map<String, List<ICocktail>> cocktails = Maps.newHashMap();
        for (IMenu menu : allMenus().keySet()) {
            cocktails.put(menu.getName(), ImmutableList.copyOf(allMenus().get(menu)));
        }
        return cocktails;
    }

    public ICocktail saveCocktail(ICocktail cocktail) throws UniqueCocktailNameException {
        Objects.requireNonNull(cocktail.getMenuName());
        checkCocktailExists(cocktail);

        return performSaveOrUpdate(cocktail);
    }

    public ICocktail updateCocktail(ICocktail cocktail) throws CocktailNotFoundException {
        Objects.requireNonNull(cocktail.getId());
        Objects.requireNonNull(cocktail.getMenuName());

        return performSaveOrUpdate(cocktail);
    }

    private ICocktail performSaveOrUpdate(ICocktail cocktail) {
        String menuName = cocktail.getMenuName();
        IMenu menu = findMenuByName(menuName);
        Cocktail cocktailEntity = EntityFactory.from(cocktail, menu.getId());
        try {
            Cocktail result;
            if (cocktailEntity.getId() == 0) {
                result = cocktailDao.create(cocktailEntity);
            } else {
                result = cocktailDao.update(cocktailEntity);
            }
            return result.toDto(menuName);
        } catch (EntityExistsException e) {
            return null;
        } finally {
            allMenusCached.clear();
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
            Cocktail entity = EntityFactory.from(cocktail, -1);
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
        Cocktail cocktail = cocktailDao.read(id);
        return cocktail.toDto(findMenuById(cocktail.getMenuId()).getName());
    }

    public boolean isCocktailInHistory(ICocktail cocktail) {
        return orderDao.findCocktailInHistory(cocktail);
    }

    public void deleteCocktailById(int id) throws CocktailNotFoundException {
        cocktailDao.delete(id);
        if (allMenusCached == null || allMenusCached.isEmpty()) {
            return;
        }
        for (IMenu m : allMenusCached.keySet()) {
            Collection<ICocktail> cocktails = allMenus().get(m);
            for (ICocktail cocktail : cocktails) {
                if (cocktail.getId() == id) {
                    cocktails.remove(cocktail);
                    break;
                }
            }
        }
    }

}