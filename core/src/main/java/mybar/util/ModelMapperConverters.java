package mybar.util;

import mybar.State;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ModelMapperConverters {

    public static final Converter<State, Boolean> STATE_CONVERTER = new Converter<State, Boolean>() {
        @Override
        public Boolean convert(MappingContext<State, Boolean> context) {
            State source = context.getSource();
            return source == State.AVAILABLE;
        }
    };

}