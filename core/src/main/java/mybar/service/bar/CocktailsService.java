package mybar.service.bar;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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
import mybar.repository.history.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CocktailsService {

    @Autowired(required = false)
    private MenuDao menuDao;

    @Autowired(required = false)
    private CocktailDao cocktailDao;

    @Autowired(required = false)
    private IngredientDao ingredientDao;

    @Autowired(required = false)
    private OrderDao orderDao;

    private Set<IMenu> allMenusCached = new HashSet<>();
    private Map<String, List<ICocktail>> cocktailsCache = new HashMap<>();

    // menu

    public Collection<IMenu> getAllMenuItems() {
        return allMenus();
    }

    private Set<IMenu> allMenus() {
        if (allMenusCached == null || allMenusCached.isEmpty()) {
            List<Menu> all = menuDao.findAll();
            Set<IMenu> asDTOs = all
                    .stream()
                    .map(Menu::toDto)
                    .collect(Collectors.toSet());
            allMenusCached.addAll(asDTOs);
        }
        return allMenusCached;
    }

    // cocktails

    private Map<String, List<ICocktail>> allCocktails() {
        if (cocktailsCache == null || cocktailsCache.isEmpty()) {
            List<Menu> all = menuDao.findAll();
            for (final Menu menu : all) {
                final String menuName = menu.getName();
                if (cocktailsCache.containsKey(menuName)) {
                    continue;
                }
                cocktailsCache.put(menuName, cocktailsToDtoList(menu, menuName));
            }
        }
        return cocktailsCache;
    }

    private static List<ICocktail> cocktailsToDtoList(Menu menu, final String menuName) {
        return menu.getCocktails()
                .stream()
                .map(cocktail -> cocktail.toDto(menuName))
                .collect(Collectors.toList());
    }

    private IMenu findMenuById(final int menuId) {
        Optional<IMenu> relatedMenu = allMenus()
                .stream()
                .filter(menu -> menu.getId() == menuId)
                .findFirst();
        if (!relatedMenu.isPresent()) {
            throw new UnknownMenuException(String.valueOf(menuId));
        }
        return relatedMenu.get();
    }

    private IMenu findMenuByName(final String menuName) {
        Optional<IMenu> relatedMenu = allMenus()
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

        if (cocktailsCache.containsKey(menuName)) {
            return cocktailsCache.get(menuName);
        }
        IMenu menu = findMenuByName(menuName);
        List<ICocktail> cocktailDtoList = cocktailsToDtoList(menuDao.read(menu.getId()), menuName);
        cocktailsCache.put(menuName, cocktailDtoList);
        return cocktailDtoList;
    }

    // all cocktails per menu

    public Map<String, List<ICocktail>> getAllCocktails() {
        return Collections.unmodifiableMap(allCocktails());
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
            Cocktail result;
            if (Strings.isNullOrEmpty(cocktailEntity.getId())) {
                result = cocktailDao.create(cocktailEntity);
            } else {
                result = cocktailDao.update(cocktailEntity);
            }
            return result.toDto(menuName);
        } catch (EntityExistsException e) {
            return null;
        } finally {
            allMenusCached.clear();
            cocktailsCache.clear();
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
        if (cocktailDao.findCocktailByName(name)) {
            throw new UniqueCocktailNameException(name);
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
            checkCocktailExists(cocktail.getName());
        } catch (UniqueCocktailNameException e) {
            return true;
        }
        return false;
    }

    public ICocktail findCocktailById(String id) throws CocktailNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "Cocktail id is required.");
        Cocktail cocktail = cocktailDao.read(id);
        return cocktail.toDto(findMenuById(cocktail.getMenuId()).getName());
    }

    public boolean isCocktailInHistory(ICocktail cocktail) {
        return orderDao.findCocktailInHistory(cocktail);
    }

    public void deleteCocktailById(String id) throws CocktailNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "Cocktail id is required.");
        cocktailDao.delete(id);
        if (cocktailsCache == null || cocktailsCache.isEmpty()) {
            return;
        }
        for (String menu : cocktailsCache.keySet()) {
            Collection<ICocktail> cocktails = cocktailsCache.get(menu);
            Iterator<ICocktail> iterator = cocktails.iterator();
            while (iterator.hasNext()) {
                ICocktail next = iterator.next();
                if (Strings.isNullOrEmpty(next.getId())) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

}