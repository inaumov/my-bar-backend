package mybar.dto.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.Measurement;
import mybar.api.bar.ICocktailIngredient;

@Getter
@Setter
public class CocktailToIngredientDto implements ICocktailIngredient {

    private int ingredientId;
    private double volume;
    private Measurement measurement;
}