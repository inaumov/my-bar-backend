package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.domain.bar.ingredient.Additive;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.repository.bar.IngredientDao;
import mybar.dto.DtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IngredientService {

    @Autowired(required = false)
    private IngredientDao ingredientDao;

    public <T extends IIngredient> List<T> findByGroupName(String groupName) {
        try {
            List<Ingredient> ingredients = ingredientDao.findByGroupName(groupName);
            return transformToDto(ingredients);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public <T extends IIngredient> List<T> findAll() {
        List<Ingredient> ingredients = ingredientDao.findAll();
        return transformToDto(ingredients);
    }

    private static <T extends IIngredient> List<T> transformToDto(List<Ingredient> ingredients) {

        Function<Ingredient, T> function = new Function<Ingredient, T>() {

            @Override
            public T apply(Ingredient ingredient) {
                if (ingredient instanceof IAdditive)
                    return (T) DtoFactory.toDto((Additive) ingredient);
                if (ingredient instanceof IDrink)
                    return (T) DtoFactory.toDto((Drink) ingredient);
                if (ingredient instanceof Beverage)
                    return (T) DtoFactory.toDto((Beverage) ingredient);
                return null;
            }
        };

        return Lists.transform(ingredients, function);
    }

}