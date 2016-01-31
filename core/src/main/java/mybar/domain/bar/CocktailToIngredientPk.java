package mybar.domain.bar;

import mybar.domain.bar.ingredient.Ingredient;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class CocktailToIngredientPk implements Serializable {

    private Cocktail cocktail;
    private Ingredient ingredient;

    @ManyToOne
    public Cocktail getCocktail() {
        return cocktail;
    }

    public void setCocktail(Cocktail cocktail) {
        this.cocktail = cocktail;
    }

    @ManyToOne
    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CocktailToIngredientPk that = (CocktailToIngredientPk) o;

        if (cocktail != null ? !cocktail.equals(that.cocktail) : that.cocktail != null)
            return false;
        if (ingredient != null ? !ingredient.equals(that.ingredient) : that.ingredient != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (cocktail != null ? cocktail.hashCode() : 0);
        result = 31 * result + (ingredient != null ? ingredient.hashCode() : 0);
        return result;
    }

}