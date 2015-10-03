package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.BeverageType;
import mybar.api.bar.IBeverage;

public class BeverageBean implements IBeverage {

    @JsonView(View.CocktailWithDetails.class)
    private int id;

    @JsonView(View.CocktailWithDetails.class)
    private String name;

    private BeverageType beverageType;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getKind() {
        return name;
    }

    public void setKind(String name) {
        this.name = name;
    }

    @Override
    public BeverageType getBeverageType() {
        return beverageType;
    }

    public void setBeverageType(BeverageType beverageType) {
        this.beverageType = beverageType;
    }

    public static BeverageBean from(IBeverage drink) {
        BeverageBean bean = new BeverageBean();
        bean.setId(drink.getId());
        bean.setKind(drink.getKind());
        bean.setBeverageType(drink.getBeverageType());
        return bean;
    }

}