package mybar.web.rest.bar;

import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mybar.State;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.CocktailIngredientBean;
import mybar.service.bar.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Component
public class AvailableCocktailsWrapper {

    @Autowired // TODO fix setter or autowired???
    private ShelfService shelfService;
    // TODO extract or refactor
    public Map<String, List<CocktailBean>> get(Map<String, List<CocktailBean>> cocktails) {
        Iterator<Map.Entry<String, List<CocktailBean>>> entries = cocktails.entrySet().iterator();
        for (; entries.hasNext(); ) {
            updateWithState(entries.next().getValue());
        }
        return cocktails;
    }

    public void updateWithState(List<CocktailBean> cocktails) {
        for (CocktailBean cocktail : cocktails) {
            cocktail.setState(calculateAvailability(cocktail.getIngredients()));
        }
    }

    private State calculateAvailability(Map<String, Collection<CocktailIngredientBean>> insideItems) {
        Collection<Collection<CocktailIngredientBean>> values = insideItems.values();
        Iterable<CocktailIngredientBean> beans = Iterables.concat(values);
        if (!beans.iterator().hasNext()) { // TODO improve this check
            return State.UNDEFINED;
        }
        boolean isCocktailAvailable = true;
        Iterator<Map.Entry<String, Collection<CocktailIngredientBean>>> entries = insideItems.entrySet().iterator();
        for (; entries.hasNext(); ) {
            for (CocktailIngredientBean inside : entries.next().getValue()) {
                boolean isBottleAvailable = shelfService.isBottleAvailable(inside.getIngredientId());
                inside.setMissing(!isBottleAvailable);
                isCocktailAvailable &= isBottleAvailable;
            }
        }
        return isCocktailAvailable ? State.AVAILABLE : State.NOT_AVAILABLE;
    }

}