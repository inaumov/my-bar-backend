package mybar.service.bar;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import mybar.api.bar.ICocktail;
import mybar.api.bar.ICocktailIngredient;
import mybar.api.bar.IMenu;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.exception.CocktailNotFoundException;
import mybar.exception.UniqueCocktailNameException;
import mybar.exception.UnknownIngredientsException;
import mybar.exception.UnknownMenuException;
import mybar.repository.bar.CocktailDao;
import mybar.repository.bar.IngredientDao;
import mybar.repository.bar.MenuDao;
import mybar.repository.rates.RatesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CocktailsService {

    private final MenuDao menuDao;

    private final CocktailDao cocktailDao;

    private final IngredientDao ingredientDao;

    private final RatesDao ratesDao;

    private Supplier<List<IMenu>> allMenusCached = Suppliers.memoizeWithExpiration(
            this::loadAllMenus, 30, TimeUnit.MINUTES);

    private Cache<String, List<ICocktail>> cocktailsCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .maximumSize(10)
            .build();

    @Autowired
    public CocktailsService(MenuDao menuDao, CocktailDao cocktailDao, IngredientDao ingredientDao, RatesDao ratesDao) {
        this.menuDao = menuDao;
        this.cocktailDao = cocktailDao;
        this.ingredientDao = ingredientDao;
        this.ratesDao = ratesDao;
    }

    // menu

    public Collection<IMenu> getAllMenuItems() {
        return allMenusCached.get();
    }

    private List<IMenu> loadAllMenus() {
        List<Menu> all = menuDao.findAll();
        return all
                .stream()
                .map(Menu::toDto)
                .collect(Collectors.toList());
    }

    // cocktails

    private void ensureAllCocktailsLoaded() {
        if (cocktailsCache.size() == 0) {
            List<Menu> all = menuDao.findAll();
            for (final Menu menu : all) {
                final String menuName = menu.getName();
                cocktailsCache.put(menuName, cocktailsToDtoList(menu, menuName));
            }
        }
    }

    private static List<ICocktail> cocktailsToDtoList(Menu menu, final String menuName) {
        return menu.getCocktails()
                .stream()
                .map(cocktail -> cocktail.toDto(menuName))
                .collect(Collectors.toList());
    }

    private IMenu findMenuById(final int menuId) {
        Optional<IMenu> relatedMenu = allMenusCached.get()
                .stream()
                .filter(menu -> menu.getId() == menuId)
                .findFirst();
        if (!relatedMenu.isPresent()) {
            throw new UnknownMenuException(String.valueOf(menuId));
        }
        return relatedMenu.get();
    }

    private IMenu findMenuByName(final String menuName) {
        Optional<IMenu> relatedMenu = allMenusCached.get()
                .stream()
                .filter(menu -> Objects.equals(menu.getName(), menuName))
                .findFirst();
        if (!relatedMenu.isPresent()) {
            throw new UnknownMenuException(menuName);
        }
        return relatedMenu.get();
    }

    // cocktails per menu

    public List<ICocktail> getAllCocktailsForMenu(final String menuName) {

        if (cocktailsCache.asMap().containsKey(menuName)) {
            return cocktailsCache.asMap().get(menuName);
        }
        IMenu menu = findMenuByName(menuName);
        List<ICocktail> cocktailDtoList = cocktailsToDtoList(menuDao.getOne(menu.getId()), menuName);
        cocktailsCache.put(menuName, cocktailDtoList);
        return cocktailDtoList;
    }

    // all cocktails per menu

    public Map<String, List<ICocktail>> getAllCocktails() {
        ensureAllCocktailsLoaded();
        return Collections.unmodifiableMap(cocktailsCache.asMap());
    }

    public ICocktail saveCocktail(ICocktail cocktail) throws UniqueCocktailNameException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cocktail.getName()), "Cocktail name is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cocktail.getMenuName()), "Menu name is required.");
        checkCocktailExists(cocktail.getName());

        return performSaveOrUpdate(cocktail);
    }

    public ICocktail updateCocktail(ICocktail cocktail) throws CocktailNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cocktail.getId()), "Cocktail id is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cocktail.getName()), "Cocktail name is required.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cocktail.getMenuName()), "Menu name is required.");

        return performSaveOrUpdate(cocktail);
    }

    private ICocktail performSaveOrUpdate(ICocktail cocktail) {
        String menuName = cocktail.getMenuName();
        IMenu menu = findMenuByName(menuName);
        checkIngredientsExist(cocktail.getIngredients());

        Cocktail cocktailEntity = EntityFactory.from(cocktail, menu.getId());
        try {
            Cocktail result = cocktailDao.save(cocktailEntity);
            return result.toDto(menuName);
        } finally {
            cocktailsCache.invalidateAll();
        }
    }

    private void checkIngredientsExist(Map<String, Collection<ICocktailIngredient>> ingredients) {
        Iterable<ICocktailIngredient> newList = Iterables.concat(ingredients.values());

        List<Integer> asInputIds = StreamSupport.stream(newList.spliterator(), false)
                .map(ICocktailIngredient::getIngredientId)
                .collect(Collectors.toList());

        List<Integer> existedIngredientIds = getExistedIngredientIds(asInputIds);
        if (asInputIds.size() != existedIngredientIds.size()) {
            List<Integer> copy = new ArrayList<>(asInputIds);
            copy.removeAll(existedIngredientIds);
            throw new UnknownIngredientsException(copy);
        }
    }

    private List<Integer> getExistedIngredientIds(List<Integer> asIds) {
        List<Ingredient> ingredients = ingredientDao.findIn(asIds);
        return ingredients
                .stream()
                .map(Ingredient::getId)
                .collect(Collectors.toList());
    }

    private void checkCocktailExists(String name) throws UniqueCocktailNameException {
        if (cocktailDao.existsByName(name)) {
            throw new UniqueCocktailNameException(name);
        }
    }

    public void removeCocktail(ICocktail cocktail) throws Exception {
        boolean hasRef = isCocktailInHistory(cocktail);
        if (hasRef) {
            Cocktail entity = EntityFactory.from(cocktail, -1); // TODO: 1/12/2018
            cocktailDao.save(entity);
        } else {
            cocktailDao.deleteById(cocktail.getId());
        }
    }

    public boolean isCocktailExist(ICocktail cocktail) {
        try {
            checkCocktailExists(cocktail.getName());
        } catch (UniqueCocktailNameException e) {
            return true;
        }
        return false;
    }

    public ICocktail findCocktailById(String id) throws CocktailNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "Cocktail id is required.");
        Optional<Cocktail> cocktail = cocktailDao.findById(id);
        return cocktail
                .map(c -> c.toDto(findMenuById(c.getMenuId()).getName()))
                .orElseThrow(() -> new CocktailNotFoundException(id));
    }

    public boolean isCocktailInHistory(ICocktail cocktail) {
        return ratesDao.checkRateExistsForCocktail(cocktail.getId());
    }

    public void deleteCocktailById(String id) throws CocktailNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "Cocktail id is required.");
        cocktailDao.deleteById(id);
        if (cocktailsCache.size() == 0) {
            return;
        }
        for (String menuName : cocktailsCache.asMap().keySet()) {
            List<ICocktail> cocktails = cocktailsCache.getIfPresent(menuName);
            if (CollectionUtils.isEmpty(cocktails)) {
                continue;
            }
            Iterator<ICocktail> iterator = cocktails.iterator();
            while (iterator.hasNext()) {
                ICocktail next = iterator.next();
                if (Objects.equals(next.getId(), id)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

}