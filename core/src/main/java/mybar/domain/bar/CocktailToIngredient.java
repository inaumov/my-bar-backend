package mybar.domain.bar;

import mybar.UnitsValue;
import mybar.domain.bar.ingredient.Ingredient;
import mybar.dto.bar.CocktailToIngredientDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.persistence.*;

@Entity
@Table(name = "COCKTAIL_TO_INGREDIENT")
@AssociationOverrides({
        @AssociationOverride(name = "pk.cocktail", joinColumns = @JoinColumn(name = "cocktail_id")),
        @AssociationOverride(name = "pk.ingredient", joinColumns = @JoinColumn(name = "ingredient_id"))
})
public class CocktailToIngredient {

    private CocktailToIngredientPk pk = new CocktailToIngredientPk();
    private double volume;
    private UnitsValue unitsValue;

    @EmbeddedId
    private CocktailToIngredientPk getPk() {
        return pk;
    }

    private void setPk(CocktailToIngredientPk pk) {
        this.pk = pk;
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

    @Column(name = "VOLUME")
    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Column(name = "UNITS")
    @Enumerated(EnumType.STRING)
    public UnitsValue getUnitsValue() {
        return unitsValue;
    }

    public void setUnitsValue(UnitsValue unitsValue) {
        this.unitsValue = unitsValue;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CocktailToIngredient that = (CocktailToIngredient) o;

        if (getPk() != null ? !getPk().equals(that.getPk()) : that.getPk() != null) return false;

        return true;
    }

    public int hashCode() {
        return (getPk() != null ? getPk().hashCode() : 0);
    }

    public CocktailToIngredientDto toDto() {
        PropertyMap<CocktailToIngredient, CocktailToIngredientDto> insideMap = new PropertyMap<CocktailToIngredient, CocktailToIngredientDto>() {
            @Override
            protected void configure() {
                map().setIngredientId(source.getIngredient().getId());
                skip().setMissing(false);
            }
        };
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(insideMap);

        return modelMapper.map(this, CocktailToIngredientDto.class);
    }

}