package mybar.dto.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.UnitOfMeasurement;
import mybar.api.bar.ICocktailIngredient;

@Getter
@Setter
public class CocktailToIngredientDto implements ICocktailIngredient {

    private int ingredientId;
    private double volume;
    private UnitOfMeasurement unitOfMeasurement;
}