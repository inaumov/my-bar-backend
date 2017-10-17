package mybar.app.bean.bar.ingredient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mybar.api.bar.Measurement;
import mybar.api.bar.ingredient.IIngredient;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class GroupedIngredientsBean {

    @JsonSerialize(using = MeasurementsSerializer.class)
    private Collection<Measurement> measurements;

    private Collection<? extends IIngredient> items;

    private Boolean isLiquid;
}