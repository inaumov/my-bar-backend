package mybar.dto;

import mybar.api.bar.ingredient.IAdditive;
import mybar.api.bar.ingredient.IBeverage;
import mybar.api.bar.ingredient.IDrink;
import mybar.api.bar.ingredient.IIngredient;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.Cocktail;
import mybar.domain.bar.CocktailToIngredient;
import mybar.domain.bar.Menu;
import mybar.dto.bar.BottleDto;
import mybar.dto.bar.CocktailDto;
import mybar.dto.bar.CocktailToIngredientDto;
import mybar.dto.bar.MenuDto;
import mybar.dto.bar.ingredient.AdditiveDto;
import mybar.dto.bar.ingredient.BeverageDto;
import mybar.dto.bar.ingredient.DrinkDto;
import mybar.dto.bar.ingredient.IngredientBaseDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DtoFactory {

    public static MenuDto toDto(Menu entity) {
        MenuDto dto = new MenuDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }

    public static CocktailDto toDto(Cocktail entity, String relatedToMenuName) {
        CocktailDto dto = new CocktailDto();
        dto.setId(entity.getId());
        dto.setMenuName(relatedToMenuName);
        dto.setName(entity.getName());
        dto.setImageUrl(entity.getImageUrl());
        dto.setDescription(entity.getDescription());
        dto.setIngredients(convert(entity.getCocktailToIngredientList()));
        return dto;
    }

    private static Map<String, List<CocktailToIngredientDto>> convert(Collection<CocktailToIngredient> source) {
        return source
                .stream()
                .collect(Collectors.groupingBy(entity -> entity.getIngredient().getGroupName(),
                        Collectors.mapping(DtoFactory::toDto, Collectors.toList()))
                );
    }

    private static CocktailToIngredientDto toDto(CocktailToIngredient entity) {
        CocktailToIngredientDto dto = new CocktailToIngredientDto();
        dto.setIngredientId(entity.getIngredient().getId());
        dto.setMeasurement(entity.getMeasurement());
        dto.setVolume(entity.getVolume());
        return dto;
    }

    public static BottleDto toDto(Bottle entity) {
        BottleDto dto = new BottleDto();
        dto.setId(entity.getId());
        dto.setBeverage(toDto(entity.getBeverage()));
        dto.setPrice(entity.getPrice());
        dto.setVolume(entity.getVolume());
        dto.setBrandName(entity.getBrandName());
        dto.setInShelf(entity.isInShelf());
        dto.setImageUrl(entity.getImageUrl());
        return dto;
    }

    public static BeverageDto toDto(IBeverage entity) {
        BeverageDto dto = new BeverageDto();
        fillBaseIngredientDto(entity, dto);
        dto.setBeverageType(entity.getBeverageType());
        return dto;
    }

    private static void fillBaseIngredientDto(IIngredient entity, IngredientBaseDto dto) {
        dto.setId(entity.getId());
        dto.setKind(entity.getKind());
    }

    public static AdditiveDto toDto(IAdditive entity) {
        AdditiveDto dto = new AdditiveDto();
        fillBaseIngredientDto(entity, dto);
        return dto;
    }

    public static DrinkDto toDto(IDrink entity) {
        DrinkDto dto = new DrinkDto();
        fillBaseIngredientDto(entity, dto);
        dto.setDrinkType(entity.getDrinkType());
        return dto;
    }

}