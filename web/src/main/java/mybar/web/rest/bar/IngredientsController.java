package mybar.web.rest.bar;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import mybar.api.bar.Measurement;
import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.app.RestBeanConverter;
import mybar.app.bean.bar.ingredient.GroupedIngredientsBean;
import mybar.service.bar.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/ingredients")
@Secured("ROLE_USER")
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

        if (Strings.isNullOrEmpty(groupNameParam)) {
            List<IIngredient> allIngredients = ingredientService.findAll();
            return new ResponseEntity<>(toMapByGroup(allIngredients), HttpStatus.OK);
        }

        List<IIngredient> ingredientsByGroupName = ingredientService.findByGroupName(groupNameParam);
        GroupedIngredientsBean groupedIngredientsBean = toMapByGroup(ingredientsByGroupName).get(groupNameParam);
        return new ResponseEntity<>(groupedIngredientsBean, HttpStatus.OK);
    }

    private static ImmutableMap<String, GroupedIngredientsBean> toMapByGroup(List<IIngredient> ingredients) {

        List<IBeverage> beverages = filter(ingredients, IBeverage.class);
        List<IDrink> drinks = filter(ingredients, IDrink.class);
        List<IAdditive> additives = filter(ingredients, IAdditive.class);

        ImmutableMap.Builder<String, GroupedIngredientsBean> builder = ImmutableMap.builder();
        putIfPresent(builder, IBeverage.GROUP_NAME, true, beverages);
        putIfPresent(builder, IDrink.GROUP_NAME, true, drinks);
        putIfPresent(builder, IAdditive.GROUP_NAME, false, additives);

        return builder.build();
    }

    private static <T extends IIngredient> List<T> filter(List<IIngredient> ingredients, Class<T> aClass) {
        return ingredients
                .stream()
                .filter(aClass::isInstance)
                .map(aClass::cast)
                .collect(Collectors.toList());
    }

    private static void putIfPresent(ImmutableMap.Builder<String, GroupedIngredientsBean> builder,
                                     String groupName, boolean isLiquid, List<? extends IIngredient> filtered) {
        if (!filtered.iterator().hasNext()) {
            builder.put(groupName, GroupedIngredientsBean.of(Collections.emptyList(), Collections.emptyList(), null));
            return;
        }
        List<IIngredient> items = filtered
                .stream()
                .map(IngredientsController::toRestEntity)
                .collect(Collectors.toList());

        builder.put(groupName, GroupedIngredientsBean.of(isLiquid ? Measurement.liquids() : Measurement.solidComponents(), items, isLiquid));
    }

    private static <T> IIngredient toRestEntity(T input) {
        if (input instanceof IAdditive) {
            return RestBeanConverter.from((IAdditive) input);
        } else if (input instanceof IDrink) {
            return RestBeanConverter.from((IDrink) input);
        } else if (input instanceof IBeverage) {
            return RestBeanConverter.from((IBeverage) input);
        }
        return null;
    }

}