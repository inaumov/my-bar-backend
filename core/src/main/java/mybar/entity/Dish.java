package mybar.entity;

import mybar.ActiveStatus;
import mybar.DishType;
import mybar.api.IDish;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Collection;

@Entity
@SequenceGenerator(name = "DISH_SEQUENCE", sequenceName = "DISH_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Dish implements IDish {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DISH_SEQUENCE")
    private int id;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @OneToMany(mappedBy = "dish", fetch = FetchType.LAZY)
    private Collection<Basis> basisList;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Transient
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISHTYPE")
    private DishType dishType;

    @Column(name = "IS_ACTIVE")
    @Enumerated(EnumType.ORDINAL)
    private ActiveStatus activeStatus;

    @Lob
    @Column(name = "IMAGE", nullable = true)
    private Blob picture;

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

    public Collection<Basis> getBasisList() {
        return basisList;
    }

    public void setBasisList(Collection<Basis> basisList) {
        this.basisList = basisList;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public DishType getDishType() {
        return dishType;
    }

    public void setDishType(DishType dishType) {
        this.dishType = dishType;
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

}