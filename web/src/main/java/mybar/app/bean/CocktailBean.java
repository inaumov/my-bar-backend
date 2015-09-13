package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import mybar.ActiveStatus;
import mybar.api.IIngredient;
import mybar.api.ICocktail;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CocktailBean implements ICocktail {

    @JsonView(View.Cocktail.class)
    private int id;

    @JsonView(View.Cocktail.class)
    private String name;

    private MenuBean menu;

    @JsonView(View.Cocktail.class)
    private double price;

    private ActiveStatus activeStatus;

    private Blob picture;

    @JsonView(View.CocktailWithDetails.class)
    @JsonProperty("ingredients")
    private Collection<IngredientBean> ingredients;

    @JsonView(View.CocktailWithDetails.class)
    private String description;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<IngredientBean> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<IngredientBean> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public MenuBean getMenu() {
        return menu;
    }

    public void setMenu(MenuBean menu) {
        this.menu = menu;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public ActiveStatus getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(ActiveStatus activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

    public static CocktailBean from(ICocktail cocktail) {
        CocktailBean bean = new CocktailBean();
        bean.setId(cocktail.getId());
        bean.setName(cocktail.getName());
        bean.setPrice(cocktail.getPrice());
        bean.setDescription(cocktail.getDescription());
        List<IngredientBean> ingredientBeans = new ArrayList<>();
        for (IIngredient ingredient : cocktail.getIngredients()) {
            ingredientBeans.add(IngredientBean.from(ingredient));
        }
        bean.setIngredients(ingredientBeans);
        bean.setActiveStatus(cocktail.getActiveStatus());

        return bean;
    }

}