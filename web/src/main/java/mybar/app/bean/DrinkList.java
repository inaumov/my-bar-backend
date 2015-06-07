package mybar.app.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlRootElement(name = "drink.list")
public class DrinkList {

    private Collection<DrinkBean> drinks;

    public DrinkList() {
    }

    public DrinkList(Collection<DrinkBean> drinks) {
        this.drinks = drinks;
    }

    @XmlElement(name = "drink")
    @XmlElementWrapper
    public Collection<DrinkBean> getDrinks() {
        return drinks;
    }

    public void setDrinks(Collection<DrinkBean> drinks) {
        this.drinks = drinks;
    }

}