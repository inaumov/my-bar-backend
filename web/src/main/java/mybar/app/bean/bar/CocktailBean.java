package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import mybar.State;
import mybar.api.bar.*;

import java.sql.Blob;
import java.util.*;

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
    private Map<String, List<InsideBean>> insides = new HashMap<>();

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
        Collection<List<InsideBean>> lists = insides.values();
        List<InsideBean> beans = new ArrayList<>();
        for (List<InsideBean> insides : lists) {
            beans.addAll(insides);
        }
        return beans;
    }

    public void setIngredients(Collection<InsideBean> ingredients) {
        //this.ingredients = ingredients; TODO
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
        for (IInside inside : cocktail.getIngredients()) {
            InsideBean insideBean = InsideBean.from(inside);
            if (inside.getIngredient() instanceof IBeverage) {
                bean.addBeverage(insideBean);
            } else if (inside.getIngredient() instanceof IDrink) {
                bean.addDrink(insideBean);
            } else if (inside.getIngredient() instanceof IAdditional) {
                bean.addAdditional(insideBean);
            }
        }
        bean.setState(cocktail.getState());
        bean.setCover(cocktail.getCover());
        return bean;
    }

    private void addBeverage(InsideBean inside) {
        addInside("beverages", inside);
    }

    private void addDrink(InsideBean inside) {
        addInside("drinks", inside);
    }

    private void addAdditional(InsideBean inside) {
        addInside("additional", inside);
    }

    private void addInside(String key, InsideBean inside) {
        if (!insides.containsKey(key)) {
            insides.put(key, new ArrayList<InsideBean>());
        }
        insides.get(key).add(inside);
    }

}