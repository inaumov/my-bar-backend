package mybar.app.bean;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.State;
import mybar.api.ICocktail;
import mybar.api.IInside;

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

    @JsonView(View.Cocktail.class)
    private State state;

    @JsonView(View.Cocktail.class)
    private String coverUrl;

    private Blob picture;

    @JsonView(View.CocktailWithDetails.class)
    private Collection<InsideBean> ingredients;

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

    public Collection<InsideBean> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<InsideBean> ingredients) {
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
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String getCover() {
        return coverUrl;
    }

    public void setCover(String coverUrl) {
        this.coverUrl = coverUrl;
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
        List<InsideBean> ingredientBeans = new ArrayList<>();
        for (IInside ingredient : cocktail.getIngredients()) {
            ingredientBeans.add(InsideBean.from(ingredient));
        }
        bean.setIngredients(ingredientBeans);
        bean.setState(cocktail.getState());
        bean.setCover(cocktail.getCover());

        return bean;
    }

}