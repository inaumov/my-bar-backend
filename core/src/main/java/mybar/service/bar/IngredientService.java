package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IngredientService {

    private ImmutableSet<String> GROUP_NAMES = ImmutableSet.of(
            IBeverage.GROUP_NAME,
            IDrink.GROUP_NAME,
            IAdditive.GROUP_NAME
    );

    @Autowired(required = false)
    private IngredientDao ingredientDao;

    public List<IIngredient> findByGroupName(String groupName) {
        Preconditions.checkArgument(GROUP_NAMES.contains(groupName), "Unknown group name: " + groupName);
        try {
            List<Ingredient> ingredients = ingredientDao.findByGroupName(groupName);
            return Lists.newArrayList(Lists.transform(ingredients, ingredientFunction()));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<IIngredient> findAll() {
        List<Ingredient> ingredients = ingredientDao.findAll();
        return Lists.newArrayList(Lists.transform(ingredients, ingredientFunction()));
    }

    private static <ENTITY, DTO extends IIngredient> Function<ENTITY, DTO> ingredientFunction() {
        return new Function<ENTITY, DTO>() {

            @Override
            public DTO apply(ENTITY input) {
                IIngredient from = null;
                if (input instanceof IAdditive) {
                    from = DtoFactory.toDto((IAdditive) input);
                } else if (input instanceof IDrink) {
                    from = DtoFactory.toDto((IDrink) input);
                } else if (input instanceof IBeverage) {
                    from = DtoFactory.toDto((IBeverage) input);
                }
                return uncheckedCast(from);
            }

            @SuppressWarnings({"unchecked"})
            private DTO uncheckedCast(Object obj) {
                return (DTO) obj;
            }
        };
    }

}