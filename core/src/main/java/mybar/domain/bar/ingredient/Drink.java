package mybar.domain.bar.ingredient;

import mybar.DrinkType;
import mybar.api.bar.ingredient.IDrink;

import javax.persistence.*;

@Entity
@DiscriminatorValue(value = "Drink")
public class Drink extends Ingredient implements IDrink {

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private DrinkType drinkType;

    @Override
    public DrinkType getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(DrinkType drinkType) {
        this.drinkType = drinkType;
    }

}