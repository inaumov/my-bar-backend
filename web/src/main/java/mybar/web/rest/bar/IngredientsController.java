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
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.ingredient.AdditiveBean;
import mybar.app.bean.bar.ingredient.BeverageBean;
import mybar.app.bean.bar.ingredient.DrinkBean;
import mybar.service.bar.IngredientService;
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
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/ingredients")
public class IngredientsController {

    private Logger logger = LoggerFactory.getLogger(IngredientsController.class);

    @Autowired
    private IngredientService ingredientService;

    //-------------------Retrieve Ingredients--------------------------------------------------------

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity listIngredients(
            @RequestParam(value = "filter", required = false) String groupNameParam) {

        List<IIngredient> ingredients;

        if (Strings.isNullOrEmpty(groupNameParam)) {
            ingredients = ingredientService.findAll();
        } else {
            ingredients = ingredientService.findByGroupName(groupNameParam);
        }

        if (ingredients.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }

        Collection beverages = filter(ingredients, IBeverage.class);
        Collection drinks = filter(ingredients, IDrink.class);
        Collection additives = filter(ingredients, IAdditive.class);

        ImmutableMap.Builder<String, Collection<IIngredient>> builder = ImmutableMap.builder();
        putIfPresent(builder, IBeverage.GROUP_NAME, new IngredientsMapper<>(BeverageBean.class).map(beverages));
        putIfPresent(builder, IDrink.GROUP_NAME, new IngredientsMapper<>(DrinkBean.class).map(drinks));
        putIfPresent(builder, IAdditive.GROUP_NAME, new IngredientsMapper<>(AdditiveBean.class).map(additives));

        ImmutableMap<String, Collection<IIngredient>> responseMap = builder.build();
        if (responseMap.size() == 1) {
            return new ResponseEntity<>(responseMap.get(groupNameParam), HttpStatus.OK);
        }
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    private void putIfPresent(ImmutableMap.Builder<String, Collection<IIngredient>> builder, String groupName, Collection beans) {
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
                    if (type == AdditiveBean.class) {
                        return (OUT) RestBeanConverter.from((IAdditive) input);
                    } else if (type == DrinkBean.class) {
                        return (OUT) RestBeanConverter.from((IDrink) input);
                    } else if (type == BeverageBean.class) {
                        return (OUT) RestBeanConverter.from((IBeverage) input);
                    }
                    return null;
                }
            });

        }
    }

}