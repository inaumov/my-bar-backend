package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import mybar.State;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IInside;
import org.modelmapper.*;

import java.util.*;

public class CocktailBean implements ICocktail {

    @JsonView(View.Cocktail.class)
    private int id;

    @JsonView(View.Cocktail.class)
    private String name;

    @JsonView(View.CocktailWithDetails.class)
    private int menuId;

    @JsonView(View.Cocktail.class)
    private State state;

    @JsonView(View.Cocktail.class)
    private String imageUrl;

    @JsonView(View.CocktailWithDetails.class)
    private Map<String, Collection<InsideBean>> insideItems = new HashMap<>();

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

    @Override
    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    @Override
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Map<String, Collection<InsideBean>> getInsideItems() {
        return insideItems;
    }

    public void setInsideItems(Map<String, Collection<InsideBean>> insides) {
        this.insideItems = insides;
    }

    public static CocktailBean from(final ICocktail cocktail) {
        ModelMapper modelMapper = new ModelMapper();
        CocktailBean cocktailBean = modelMapper.map(cocktail, CocktailBean.class);
        if (cocktail.getInsideItems() != null) {
            cocktailBean.setInsideItems(Maps.transformValues(cocktail.getInsideItems(),
                    new Function<Collection<? extends IInside>, Collection<InsideBean>>() {
                        @Override
                        public Collection<InsideBean> apply(Collection<? extends IInside> insideItems) {
                            return Collections2.transform(insideItems, new Function<IInside, InsideBean>() {
                                @Override
                                public InsideBean apply(IInside inside) {
                                    return InsideBean.from(inside);
                                }
                            });
                        }
                    }));
        }
        return cocktailBean;
    }

}