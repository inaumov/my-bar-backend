package mybar.entity;

import mybar.api.ICategory;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Category implements ICategory {

    @Id
    private int id;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private Collection<Dish> dishes;

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

    public Collection<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(Collection<Dish> dishes) {
        this.dishes = dishes;
    }

}