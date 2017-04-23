package mybar.dto.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.ICocktail;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class CocktailDto implements ICocktail {

    private int id;
    private String name;
    private Map<String, Collection<CocktailToIngredientDto>> ingredients;
    private String menuName;
    private String description;
    private String imageUrl;
}