package mybar.dto.bar.ingredient;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ingredient.BeverageType;
import mybar.api.bar.ingredient.IBeverage;

@Getter
@Setter
public class BeverageDto extends IngredientBaseDto implements IBeverage {

    private BeverageType beverageType;
}