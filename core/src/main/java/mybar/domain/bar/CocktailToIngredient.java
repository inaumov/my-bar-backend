package mybar.domain.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.UnitOfMeasurement;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import mybar.domain.bar.ingredient.Ingredient;

import javax.persistence.*;

@Entity
@Table(name = "COCKTAIL_TO_INGREDIENT")
@AssociationOverrides({
        @AssociationOverride(name = "pk.cocktail", joinColumns = @JoinColumn(name = "COCKTAIL_ID")),
        @AssociationOverride(name = "pk.ingredient", joinColumns = @JoinColumn(name = "INGREDIENT_ID"))
})
public class CocktailToIngredient {

    @Getter
    @Setter
    @EmbeddedId
    private CocktailToIngredientPk pk = new CocktailToIngredientPk();

    @Getter
    @Setter
    @Column(name = "VOLUME")
    private double volume;

    @Getter
    @Setter
    @Column(name = "UNITS")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasurement unitOfMeasurement;

    @PrePersist
    @PreUpdate
    public void setDefaults() {
        if (unitOfMeasurement == null) {
            boolean isLiquid = pk.getIngredient() instanceof Beverage || pk.getIngredient() instanceof Drink;
            unitOfMeasurement = isLiquid ? UnitOfMeasurement.ML : UnitOfMeasurement.PCS;
        }
    }

    @Transient
    public Cocktail getCocktail() {
        return getPk().getCocktail();
    }

    public void setCocktail(Cocktail cocktail) {
        getPk().setCocktail(cocktail);
    }

    @Transient
    public Ingredient getIngredient() {
        return getPk().getIngredient();
    }

    public void setIngredient(Ingredient ingredient) {
        getPk().setIngredient(ingredient);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CocktailToIngredient that = (CocktailToIngredient) o;

        return getPk() != null ? getPk().equals(that.getPk()) : that.getPk() == null;
    }

    public int hashCode() {
        return (getPk() != null ? getPk().hashCode() : 0);
    }

}