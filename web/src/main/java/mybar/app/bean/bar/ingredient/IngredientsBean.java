package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mybar.UnitOfMeasurement;
import mybar.api.bar.ingredient.IIngredient;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class IngredientsBean {
    @JsonSerialize(using = UnitOfMeasurementsSerializer.class)
    private Collection<UnitOfMeasurement> unitsOfMeasurement;
    private Collection<IIngredient> items;

}