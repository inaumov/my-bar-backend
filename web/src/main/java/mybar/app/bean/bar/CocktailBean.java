package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ICocktail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CocktailBean implements ICocktail {

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private int id;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private String name;

    @JsonView(View.CocktailWithDetails.class)
    private int menuId;

    @JsonView(View.Cocktail.class)
    @JsonProperty("available")
    private YesNoEnum hasAllIngredients = YesNoEnum.UNDEFINED;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private String imageUrl;

    @JsonView({View.Cocktail.class, View.CocktailWithDetails.class})
    private Map<String, Collection<CocktailIngredientBean>> ingredients = new HashMap<>();

    @JsonView(View.CocktailWithDetails.class)
    private String description;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("name", name)
                .add("imageUrl", imageUrl)
                .add("description", description)
                .toString();
    }

}