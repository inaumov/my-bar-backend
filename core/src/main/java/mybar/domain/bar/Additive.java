package mybar.domain.bar;

import mybar.api.bar.IAdditive;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "Additive")
public class Additive extends Ingredient implements IAdditive {

}