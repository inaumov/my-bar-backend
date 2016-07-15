package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.dto.bar.ingredient.AdditiveDto;
import mybar.dto.bar.ingredient.BeverageDto;
import mybar.dto.bar.ingredient.DrinkDto;
import mybar.repository.bar.IngredientDao;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IngredientService {

    @Autowired(required = false)
    private IngredientDao ingredientDao;

    public <T extends IIngredient> List<T> findByGroupName(String groupName) {
        List<Ingredient> ingredients = null;
        try {
            ingredients = ingredientDao.findByGroupName(groupName);
        } catch (Exception e) {
            Throwables.propagate(e);
        }
        return transformToDto(ingredients);
    }

    public <T extends IIngredient> List<T> findAll() {
        List<Ingredient> ingredients = ingredientDao.findAll();
        return transformToDto(ingredients);
    }

    private static  <T extends IIngredient> List<T> transformToDto(List<Ingredient> ingredients) {

        Function<Ingredient, T> function = new Function<Ingredient, T>() {

            ModelMapper mapper = new ModelMapper();

            @Override
            public T apply(Ingredient ingredient) {
                if (ingredient instanceof IAdditive)
                    return (T) mapper.map(ingredient, AdditiveDto.class);
                if (ingredient instanceof IDrink)
                    return (T) mapper.map(ingredient, DrinkDto.class);
                if (ingredient instanceof IBeverage)
                    return (T) mapper.map(ingredient, BeverageDto.class);
                return null;
            }
        };

        return Lists.transform(ingredients, function);
    }

}