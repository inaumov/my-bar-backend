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

    private Set<IMenu> allMenusCached = new HashSet<>();
    private Map<String, List<ICocktail>> cocktailsCache = new HashMap<>();

    private Function<Menu, IMenu> menuFunction = new Function<Menu, IMenu>() {
        @Override
        public IMenu apply(Menu menu) {
            return menu.toDto();
        }
    };

    // menu

    public Collection<IMenu> getAllMenuItems() {
        return allMenus();
    }

    private Set<IMenu> allMenus() {
        if (allMenusCached == null || allMenusCached.isEmpty()) {
            List<Menu> all = menuDao.findAll();
            for (final Menu menu : all) {
                allMenusCached.add(menuFunction.apply(menu));
            }
        }
        return allMenusCached;
    }

    private Map<String, List<ICocktail>> allCocktails() {
        if (cocktailsCache == null || cocktailsCache.isEmpty()) {
            List<Menu> all = menuDao.findAll();
            for (final Menu menu : all) {
                final String menuName = menu.getName();
                if (cocktailsCache.containsKey(menuName)) {
                    continue;
                }
                cocktailsCache.put(menuName, convertCocktailsToDtoList(menu, menuName));
            }
        }
        return cocktailsCache;
    }

    private static ImmutableList<ICocktail> convertCocktailsToDtoList(Menu menu, final String menuName) {
        return FluentIterable.from(menu.getCocktails()).transform(new Function<Cocktail, ICocktail>() {
            @Override
            public ICocktail apply(Cocktail cocktail) {
                return cocktail.toDto(menuName);
            }
        }).toList();
    }

    private IMenu findMenuById(final int menuId) {
        return Iterables.find(allMenus(), new Predicate<IMenu>() {
            @Override
            public boolean apply(IMenu menu) {
                return menu.getId() == menuId;
            }
        });
    }

    private IMenu findMenuByName(final String menuName) {
        Optional<IMenu> relatedMenu = Iterables.tryFind(allMenus(), new Predicate<IMenu>() {
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

    // cocktails per menu

    public List<ICocktail> getAllCocktailsForMenu(final String menuName) {

        if (cocktailsCache.containsKey(menuName)) {
            return cocktailsCache.get(menuName);
        }
        IMenu menu = findMenuByName(menuName);
        ImmutableList<ICocktail> cocktailDtoList = convertCocktailsToDtoList(menuDao.read(menu.getId()), menuName);
        cocktailsCache.put(menuName, cocktailDtoList);
        return cocktailDtoList;
    }

    // all cocktails per menu

    public Map<String, List<ICocktail>> getAllCocktails() {
        return ImmutableMap.copyOf(allCocktails());
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
        if (cocktailsCache == null || cocktailsCache.isEmpty()) {
            return;
        }
        for (String menu : cocktailsCache.keySet()) {
            Collection<ICocktail> cocktails = cocktailsCache.get(menu);
            Iterator<ICocktail> iterator = cocktails.iterator();
            while (iterator.hasNext()) {
                ICocktail next = iterator.next();
                if (next.getId() == id) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

}