package mybar.app.cocktails;

import lombok.Setter;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.CocktailIngredientBean;
import mybar.common.providers.availability.IAvailabilityCalculator;
import mybar.service.bar.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("CocktailAvailabilityCalculator")
public class CocktailAvailabilityCalculator implements IAvailabilityCalculator<CocktailBean> {

    @Setter
    @Autowired
    private ShelfService shelfService;

    @Override
    public void doUpdate(CocktailBean cocktail) {
        Boolean isAvailable = calculateAvailability(cocktail.getIngredients());
        cocktail.setAvailable(isAvailable);
    }

    private Boolean calculateAvailability(Map<String, Collection<CocktailIngredientBean>> cocktailIngredients) {
        List<CocktailIngredientBean> allIngredients = new ArrayList<>();
        cocktailIngredients.values()
                .forEach(allIngredients::addAll);
        Iterator<CocktailIngredientBean> ingredientsIterator = allIngredients.iterator();
        if (!ingredientsIterator.hasNext()) {
            return null;
        }
        boolean isCocktailAvailable = true;

        while (ingredientsIterator.hasNext()) {
            CocktailIngredientBean ingredientBean = ingredientsIterator.next();
            boolean isBottleAvailable = shelfService.isBottleAvailable(ingredientBean.getIngredientId());
            ingredientBean.setAvailable(isBottleAvailable);
            isCocktailAvailable &= isBottleAvailable;
        }
        return isCocktailAvailable;
    }

}