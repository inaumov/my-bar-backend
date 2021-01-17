package mybar.service.bar;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.dto.DtoFactory;
import mybar.repository.bar.IngredientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class IngredientService {

    private Set<String> GROUP_NAMES = new HashSet<>(Arrays.asList(
            IBeverage.GROUP_NAME,
            IDrink.GROUP_NAME,
            IAdditive.GROUP_NAME
    ));

    private final IngredientDao ingredientDao;

    private Supplier<List<IIngredient>> allIngredientsCached = Suppliers.memoizeWithExpiration(
            this::loadAllIngredients, 30, TimeUnit.MINUTES);

    @Autowired
    public IngredientService(IngredientDao ingredientDao) {
        this.ingredientDao = ingredientDao;
    }

    public List<IIngredient> findByGroupName(String groupName) throws IllegalArgumentException {
        Preconditions.checkArgument(GROUP_NAMES.contains(groupName), "Unknown group name: " + groupName);
        if (allIngredientsCached.get().isEmpty()) {
            return loadByGroupName(groupName);
        }
        Map<String, List<IIngredient>> groupedByClass = allIngredientsCached.get()
                .stream()
                .collect(Collectors.groupingBy(IIngredient::getGroupName));
        List<? extends IIngredient> list = groupedByClass.get(groupName);
        return Collections.unmodifiableList(list);
    }

    private List<IIngredient> loadByGroupName(String groupName) throws IllegalArgumentException {
        try {
            List<Ingredient> ingredients = ingredientDao.findByGroupName(groupName);
            return ingredients
                    .stream()
                    .map(IngredientService::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<IIngredient> findAll() {
        return Collections.unmodifiableList(allIngredientsCached.get());
    }

    private List<IIngredient> loadAllIngredients() {
        List<Ingredient> ingredients = ingredientDao.findAll();
        return ingredients
                .stream()
                .map(IngredientService::toDto)
                .collect(Collectors.toList());
    }

    private static <T> IIngredient toDto(T input) {
        if (input instanceof IAdditive) {
            return DtoFactory.toDto((IAdditive) input);
        } else if (input instanceof IDrink) {
            return DtoFactory.toDto((IDrink) input);
        } else if (input instanceof IBeverage) {
            return DtoFactory.toDto((IBeverage) input);
        }
        return null;
    }

}