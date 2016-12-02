package mybar.dto.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.IBottle;
import mybar.dto.bar.ingredient.BeverageDto;

@Getter
@Setter
public class BottleDto implements IBottle {

    private int id;
    private BeverageDto beverage;
    private String brandName;
    private double volume;
    private double price;
    private boolean inShelf;
    private String imageUrl;
}