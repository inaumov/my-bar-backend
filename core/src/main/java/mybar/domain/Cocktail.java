package mybar.domain;

import mybar.ActiveStatus;
import mybar.api.ICocktail;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Collection;

@Entity
@SequenceGenerator(name = "COCKTAIL_SEQUENCE", sequenceName = "COCKTAIL_SEQUENCE", allocationSize = 3, initialValue = 1)
public class Cocktail implements ICocktail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COCKTAIL_SEQUENCE")
    private int id;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "MENU_ID")
    private Menu menu;

    @OneToMany(mappedBy = "cocktail", fetch = FetchType.EAGER) // TODO: fetch lazily
    private Collection<Basis> basisList;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Transient
    private double price;

    @Column(name = "ACTIVE")
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
    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
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

}