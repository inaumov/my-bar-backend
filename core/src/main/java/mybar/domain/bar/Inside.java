package mybar.domain.bar;

import mybar.UnitsValue;
import mybar.api.bar.ingredient.IIngredient;
import mybar.api.bar.IInside;
import mybar.domain.bar.ingredient.Ingredient;

import javax.persistence.*;

@Entity
public class Inside implements IInside {

    @Id
    private int id;

    @ManyToOne(cascade=CascadeType.ALL)
    private Ingredient ingredient;

    @Column(name = "VOLUME")
    private double volume;

    @Column(name = "UNITS")
    @Enumerated(EnumType.STRING)
    private UnitsValue unitsValue;

    @ManyToOne
    @JoinColumn(name = "COCKTAIL_ID")
    private Cocktail cocktail;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public IIngredient getIngredient() {
        return ingredient;
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public UnitsValue getUnitsValue() {
        return unitsValue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setUnitsValue(UnitsValue unitsValue) {
        this.unitsValue = unitsValue;
    }

}