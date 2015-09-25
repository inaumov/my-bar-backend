package mybar.domain;

import mybar.DrinkType;
import mybar.api.IAdditional;
import mybar.api.IDrink;

import javax.persistence.*;

@Entity
@DiscriminatorValue(value = "Additional")
public class Additional extends Ingredient implements IAdditional {

}