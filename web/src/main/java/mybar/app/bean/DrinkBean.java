package mybar.app.bean;

import mybar.ActiveStatus;
import mybar.Preparation;
import mybar.api.IDrink;
import mybar.entity.Basis;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.sql.Blob;
import java.util.Collection;

@XmlRootElement(name = "drink")
public class DrinkBean implements IDrink {

    private int id;

    private String name;

    private MenuBean menu;

    private Collection<Basis> basisList;

    private String description;

    private double price;

    private Preparation preparation;

    private ActiveStatus activeStatus;

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
    public Preparation getPreparation() {
        return preparation;
    }

    public void setPreparation(Preparation preparation) {
        this.preparation = preparation;
    }

    @Override
    public ActiveStatus getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(ActiveStatus activeStatus) {
        this.activeStatus = activeStatus;
    }

    @XmlTransient
    @Override
    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

    public static DrinkBean from(IDrink d) {
        DrinkBean bean = new DrinkBean();
        return bean;
    }

}