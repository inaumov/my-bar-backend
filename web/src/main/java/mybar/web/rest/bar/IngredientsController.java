package mybar.web.rest.bar;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.app.bean.bar.ingredient.AdditiveBean;
import mybar.app.bean.bar.ingredient.BeverageBean;
import mybar.app.bean.bar.ingredient.DrinkBean;
import mybar.service.bar.IngredientService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
public class IngredientsController<DTO extends IIngredient, BEAN extends IIngredient> {

    private Logger logger = LoggerFactory.getLogger(IngredientsController.class);

    @Autowired
    private IngredientService ingredientService;

    //-------------------Retrieve Ingredients--------------------------------------------------------

    @RequestMapping(value = "/ingredients", method = RequestMethod.GET)
    public ResponseEntity listIngredients(
            @RequestParam(value = "filter", required = false) String groupName) {

        List<DTO> ingredients;

        if (Strings.isNullOrEmpty(groupName)) {
            ingredients = ingredientService.findAll();
        } else {
            ingredients = ingredientService.findByGroupName(groupName);
        }

        if (ingredients.isEmpty()) {
            return new ResponseEntity<>(ingredients, HttpStatus.NO_CONTENT);
        }

        Collection<DTO> beverages = filter(ingredients, IBeverage.class);
        Collection<DTO> drinks = filter(ingredients, IDrink.class);
        Collection<DTO> additives = filter(ingredients, IAdditive.class);

        ImmutableMap.Builder<String, Collection<BEAN>> builder = ImmutableMap.builder();
        putIfPresent(builder, IBeverage.GROUP_NAME, new IngredientsMapper<>(BeverageBean.class).map(beverages));
        putIfPresent(builder, IDrink.GROUP_NAME, new IngredientsMapper<>(DrinkBean.class).map(drinks));
        putIfPresent(builder, IAdditive.GROUP_NAME, new IngredientsMapper<>(AdditiveBean.class).map(additives));

        return new ResponseEntity<>(builder.build(), HttpStatus.OK);
    }

    private void putIfPresent(ImmutableMap.Builder<String, Collection<BEAN>> builder, String groupName, Collection beans) {
        if (beans.isEmpty()) {
            return;
        }
        builder.put(groupName, beans);
    }

    private <T extends IIngredient> Collection<T> filter(List<T> all, final Class c) {
        return Collections2.filter(all, new Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return c.isInstance(t);
            }
        });
    }

    private class IngredientsMapper<OUT> {

        private final Class<OUT> type;
        private ModelMapper mapper = new ModelMapper();

        private IngredientsMapper() {
            Type t = this.getClass().getGenericSuperclass();
            ParameterizedType pt = (ParameterizedType) t;
            type = (Class) pt.getActualTypeArguments()[0];
        }

        private IngredientsMapper(Class<OUT> type) {
            this.type = type;
        }

        private <IN extends IIngredient> Collection<OUT> map(Collection<IN> filtered) {
            return Collections2.transform(filtered, new Function<IN, OUT>() {
                @Override
                public OUT apply(IN input) {
                    return mapper.map(input, type);
                }
            });

        }
    }

}