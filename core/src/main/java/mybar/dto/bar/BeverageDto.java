package mybar.dto.bar;

import mybar.BeverageType;
import mybar.api.bar.ingredient.IBeverage;

public class BeverageDto implements IBeverage {

    private int id;
    private BeverageType beverageType;
    private String kind;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public BeverageType getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

    @Override
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
    
}