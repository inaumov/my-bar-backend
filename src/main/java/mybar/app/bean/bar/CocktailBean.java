package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import mybar.common.providers.availability.IAvailabilitySettable;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ICocktail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CocktailBean implements ICocktail, IAvailabilitySettable {

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private String id;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private String name;

    @JsonView(View.CocktailWithDetails.class)
    @JsonProperty("relatedToMenu")
    private String menuName;

    @JsonView(View.Cocktail.class)
    @JsonProperty("available")
    private YesNoEnum hasAllIngredients = YesNoEnum.UNDEFINED;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private String imageUrl;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private Map<String, Collection<CocktailIngredientBean>> ingredients = new HashMap<>();

    @JsonView(View.CocktailWithDetails.class)
    private String description;

    @JsonIgnore
    @Override
    public void setAvailable(Boolean isAvailable) {
        this.hasAllIngredients = hasAllIngredients(isAvailable);
    }

    private YesNoEnum hasAllIngredients(Boolean isCocktailAvailable) {
        if (isCocktailAvailable == Boolean.TRUE) {
            return YesNoEnum.YES;
        } else if (isCocktailAvailable == Boolean.FALSE) {
            return YesNoEnum.NO;
        }
        return YesNoEnum.UNDEFINED;
    }

}