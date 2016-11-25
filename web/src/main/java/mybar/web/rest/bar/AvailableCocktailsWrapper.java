package mybar.web.rest.bar;

import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mybar.State;
import mybar.app.bean.bar.CocktailBean;
import mybar.app.bean.bar.InsideBean;
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

    public Map<String, List<CocktailBean>> get(Map<String, List<CocktailBean>> cocktails) {
        Iterator<Map.Entry<String, List<CocktailBean>>> entries = cocktails.entrySet().iterator();
        for (; entries.hasNext(); ) {
            for (CocktailBean cocktail : entries.next().getValue()) {
                cocktail.setState(calculateAvailability(cocktail.getInsideItems()));
            }
        }
        return cocktails;
    }

    private State calculateAvailability(Map<String, Collection<InsideBean>> insideItems) {
        Collection<Collection<InsideBean>> values = insideItems.values();
        Iterable<InsideBean> beans = Iterables.concat(values);
        if (!beans.iterator().hasNext()) { // TODO improve this check
            return State.UNDEFINED;
        }
        boolean isCocktailAvailable = true;
        Iterator<Map.Entry<String, Collection<InsideBean>>> entries = insideItems.entrySet().iterator();
        for (; entries.hasNext(); ) {
            for (InsideBean inside : entries.next().getValue()) {
                boolean isBottleAvailable = shelfService.isBottleAvailable(inside.getIngredientId());
                inside.setMissing(!isBottleAvailable);
                isCocktailAvailable &= isBottleAvailable;
            }
        }
        return isCocktailAvailable ? State.AVAILABLE : State.NOT_AVAILABLE;
    }

}