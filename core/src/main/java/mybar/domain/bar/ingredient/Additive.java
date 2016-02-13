package mybar.domain.bar.ingredient;

import mybar.api.bar.ingredient.IAdditive;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "Additive")
public class Additive extends Ingredient implements IAdditive {

}