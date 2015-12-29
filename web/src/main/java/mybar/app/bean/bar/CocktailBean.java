package mybar.app.bean.bar;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import mybar.State;
import mybar.api.bar.ICocktail;
import mybar.api.bar.IInside;
import org.modelmapper.*;
import org.modelmapper.spi.MappingContext;

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

        PropertyMap<ICocktail, CocktailBean> propertyMap = new PropertyMap<ICocktail, CocktailBean>() {
            @Override
            protected void configure() {
                using(CONVERTER).map(source.getInsideItems()).setInsideItems(null);
                //map().setInsideItems(convert(source.getInsideItems()));
            }
        };

        ModelMapper modelMapper = new ModelMapper();
        //modelMapper.addMappings(propertyMap);

        CocktailBean cocktailBean = modelMapper.map(cocktail, CocktailBean.class);
        cocktailBean.setInsideItems(convert(cocktail.getInsideItems()));
        return cocktailBean;
    }

    public static final Converter<Map<String, Collection<IInside>>, Map<String, Collection<InsideBean>>> CONVERTER =
            new Converter<Map<String, Collection<IInside>>, Map<String, Collection<InsideBean>>>() {

                @Override
                public Map<String, Collection<InsideBean>> convert(MappingContext<Map<String, Collection<IInside>>, Map<String, Collection<InsideBean>>> context) {

                    Map<String, Collection<IInside>> source = context.getSource();
                    Map<String, Collection<InsideBean>> stringListMap = Maps.transformValues(source, new Function<Collection<IInside>, Collection<InsideBean>>() {
                        @Override
                        public Collection<InsideBean> apply(Collection<IInside> list) {
                            return Collections2.transform(list, new Function<IInside, InsideBean>() {
                                @Override
                                public InsideBean apply(IInside iInside) {
                                    return InsideBean.from(iInside);
                                }
                            });
/*
                    ModelMapper modelMapper = new ModelMapper();
                    List<InsideBean> map = modelMapper.map(list, new TypeToken<List<InsideBean>>() {
                    }.getType());
                    return map;
*/
                        }
                    });
                    return stringListMap;
                }
            };

    private static Map<String, Collection<InsideBean>> convert(Map<String, ? extends Collection<? extends IInside>> map) {

        return Maps.transformValues(map, new Function<Collection<? extends IInside>, Collection<InsideBean>>() {

            @Override
            public Collection<InsideBean> apply(Collection<? extends IInside> insideItems) {

                return Collections2.transform(insideItems, new Function<IInside, InsideBean>() {
                    @Override
                    public InsideBean apply(IInside inside) {
                        return InsideBean.from(inside);
                    }
                });
            }

        });

    }

}