package mybar.dto.bar.ingredient;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mybar.api.bar.ingredient.BeverageType;
import mybar.api.bar.ingredient.IBeverage;

@Getter
@Setter
@ToString(callSuper = true)
public class BeverageDto extends IngredientBaseDto implements IBeverage {

    private BeverageType beverageType;
}