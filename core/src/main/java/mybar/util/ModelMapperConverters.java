package mybar.util;

import mybar.State;
import mybar.api.bar.IInside;
import mybar.domain.bar.Inside;
import mybar.domain.bar.ingredient.Additive;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelMapperConverters {

    public static final Converter<State, Boolean> STATE_CONVERTER = new Converter<State, Boolean>() {

        @Override
        public Boolean convert(MappingContext<State, Boolean> context) {
            State source = context.getSource();
            return source == State.AVAILABLE;
        }

    };

    public static final Converter<List<Inside>, Map<String, List<Inside>>> INSIDES_CONVERTER = new Converter<List<Inside>, Map<String, List<Inside>>>() {

        private Map<String, List<Inside>> insides = new HashMap<>();

        @Override
        public Map<String, List<Inside>> convert(MappingContext<List<Inside>, Map<String, List<Inside>>> context) {

            List<Inside> source = context.getSource();

            for (Inside inside : source) {
                if (inside.getIngredient() instanceof Beverage) {
                    this.addBeverage(inside);
                } else if (inside.getIngredient() instanceof Drink) {
                    this.addDrink(inside);
                } else if (inside.getIngredient() instanceof Additive) {
                    this.addAdditional(inside);
                }
            }

            return insides;
        }

        private void addBeverage(Inside inside) {
            addInside("beverages", inside);
        }

        private void addDrink(Inside inside) {
            addInside("drinks", inside);
        }

        private void addAdditional(Inside inside) {
            addInside("additives", inside);
        }

        private void addInside(String key, Inside inside) {
            if (!insides.containsKey(key)) {
                insides.put(key, new ArrayList<Inside>());
            }
            insides.get(key).add(inside);
        }

    };

}