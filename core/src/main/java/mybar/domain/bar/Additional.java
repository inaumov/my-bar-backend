package mybar.domain.bar;

import mybar.api.bar.IAdditional;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "Additional")
public class Additional extends Ingredient implements IAdditional {

}