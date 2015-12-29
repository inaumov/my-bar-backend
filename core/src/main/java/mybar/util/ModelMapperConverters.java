package mybar.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import mybar.State;
import mybar.api.bar.IInside;
import mybar.domain.bar.Inside;
import mybar.domain.bar.ingredient.Additive;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Collection;
import java.util.Map;

public class ModelMapperConverters {

    public static final Converter<State, Boolean> STATE_CONVERTER = new Converter<State, Boolean>() {

        @Override
        public Boolean convert(MappingContext<State, Boolean> context) {
            State source = context.getSource();
            return source == State.AVAILABLE;
        }

    };

    public static final Converter<Collection<Inside>, Map<String, Collection<IInside>>> INSIDES_CONVERTER = new Converter<Collection<Inside>, Map<String, Collection<IInside>>>() {

        @Override
        public Map<String, Collection<IInside>> convert(MappingContext<Collection<Inside>, Map<String, Collection<IInside>>> mappingContext) {

            ListMultimap<String, IInside> insidesMultimap = ArrayListMultimap.create();

            Collection<Inside> source = mappingContext.getSource();
            for (Inside inside : source) {
                if (inside.getIngredient() instanceof Beverage) {
                    insidesMultimap.put("beverages", inside.toDto());
                } else if (inside.getIngredient() instanceof Drink) {
                    insidesMultimap.put("drinks", inside.toDto());
                } else if (inside.getIngredient() instanceof Additive) {
                    insidesMultimap.put("additives", inside.toDto());
                }
            }
            return insidesMultimap.asMap();
        }
    };

}