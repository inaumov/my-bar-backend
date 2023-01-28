package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import mybar.common.providers.availability.IAvailabilitySettable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ICocktailIngredient;
import mybar.api.bar.Measurement;

@Getter
@Setter
public class CocktailIngredientBean implements ICocktailIngredient, IAvailabilitySettable {

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private int ingredientId;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private double volume;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private Measurement measurement;

    @Getter(AccessLevel.NONE)
    @JsonView(View.Cocktail.class)
    private Boolean missing;

    public Boolean isMissing() {
        return missing;
    }

    @JsonIgnore
    @Override
    public void setAvailable(Boolean isAvailable) {
        this.missing = isAvailable == null || !isAvailable;
    }

}