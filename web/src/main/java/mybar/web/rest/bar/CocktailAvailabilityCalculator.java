package mybar.web.rest.bar;

import com.google.common.collect.Iterables;
import common.providers.availability.IAvailabilityCalculator;
import lombok.Setter;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.CocktailIngredientBean;
import mybar.service.bar.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Component
@Qualifier("CocktailAvailabilityCalculator")
public class CocktailAvailabilityCalculator implements IAvailabilityCalculator<CocktailBean> {

    @Setter
    @Autowired
    private ShelfService shelfService;

    @Override
    public void doUpdate(CocktailBean cocktail) {
        cocktail.setAvailable(calculateAvailability(cocktail.getIngredients()));
    }

    private Boolean calculateAvailability(Map<String, Collection<CocktailIngredientBean>> cocktailIngredients) {
        Collection<Collection<CocktailIngredientBean>> values = cocktailIngredients.values();
        Iterable<CocktailIngredientBean> beans = Iterables.concat(values);
        if (!beans.iterator().hasNext()) {
            return null;
        }
        boolean isCocktailAvailable = true;
        Iterator<Map.Entry<String, Collection<CocktailIngredientBean>>> entries = cocktailIngredients.entrySet().iterator();
        for (; entries.hasNext(); ) {
            for (CocktailIngredientBean cocktailIngredientBean : entries.next().getValue()) {
                boolean isBottleAvailable = shelfService.isBottleAvailable(cocktailIngredientBean.getIngredientId());
                cocktailIngredientBean.setAvailable(isBottleAvailable);
                isCocktailAvailable &= isBottleAvailable;
            }
        }
        return isCocktailAvailable;
    }

}