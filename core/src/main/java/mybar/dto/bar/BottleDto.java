package mybar.dto.bar;

import lombok.Getter;
import lombok.Setter;
import mybar.api.bar.IBottle;
import mybar.dto.bar.ingredient.BeverageDto;

import java.math.BigDecimal;

@Getter
@Setter
public class BottleDto implements IBottle {

    private String id;
    private BeverageDto beverage;
    private String brandName;
    private double volume;
    private BigDecimal price;
    private boolean inShelf;
    private String imageUrl;
}