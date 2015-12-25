package mybar.dto.bar;

import mybar.State;
import mybar.api.bar.ICocktail;

import java.util.List;
import java.util.Map;

public class CocktailDto implements ICocktail {

    private int id;
    private String name;
    private Map<String, List<InsideDto>> insides;
    private int menuId;
    private String description;
    private State state;
    private String imageUrl;

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
    public Map<String, List<InsideDto>> getInsides() {
        return insides;
    }

    public void setInsides(Map<String, List<InsideDto>> insides) {
        this.insides = insides;
    }

    @Override
    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}