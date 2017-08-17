package mybar.web.rest.bar;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import mybar.UnitOfMeasurement;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.ingredient.IngredientsBean;
import mybar.service.bar.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/ingredients")
public class IngredientsController {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientsController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

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

        Iterable<IBeverage> beverages = Iterables.filter(ingredients, IBeverage.class);
        Iterable<IDrink> drinks = Iterables.filter(ingredients, IDrink.class);
        Iterable<IAdditive> additives = Iterables.filter(ingredients, IAdditive.class);

        ImmutableMap.Builder<String, IngredientsBean> builder = ImmutableMap.builder();
        putIfPresent(builder, IBeverage.GROUP_NAME, UnitOfMeasurement.liquids(), beverages);
        putIfPresent(builder, IDrink.GROUP_NAME, UnitOfMeasurement.liquids(), drinks);
        putIfPresent(builder, IAdditive.GROUP_NAME, UnitOfMeasurement.solidComponents(), additives);

        ImmutableMap<String, IngredientsBean> responseMap = builder.build();
        if (responseMap.size() == 1) {
            return new ResponseEntity<>(responseMap.get(groupNameParam), HttpStatus.OK);
        }
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }

    private void putIfPresent(ImmutableMap.Builder<String, IngredientsBean> builder,
                              String groupName, EnumSet<UnitOfMeasurement> unitsOfMeasurement, Iterable<? extends IIngredient> filtered) {
        if (!filtered.iterator().hasNext()) {
            return;
        }
        ArrayList<IIngredient> items = Lists.newArrayList(Iterables.transform(filtered, ingredientFunction()));
        builder.put(groupName, IngredientsBean.of(unitsOfMeasurement, items));
    }

    private static <DTO, BEAN extends IIngredient> Function<DTO, BEAN> ingredientFunction() {
        return new Function<DTO, BEAN>() {

            @Override
            public BEAN apply(DTO input) {
                IIngredient from = null;
                if (input instanceof IAdditive) {
                    from = RestBeanConverter.from((IAdditive) input);
                } else if (input instanceof IDrink) {
                    from = RestBeanConverter.from((IDrink) input);
                } else if (input instanceof IBeverage) {
                    from = RestBeanConverter.from((IBeverage) input);
                }
                return uncheckedCast(from);
            }

            @SuppressWarnings({"unchecked"})
            private BEAN uncheckedCast(Object obj) {
                return (BEAN) obj;
            }
        };
    }

}