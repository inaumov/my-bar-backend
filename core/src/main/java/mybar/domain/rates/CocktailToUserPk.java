package mybar.domain.rates;

import lombok.Getter;
import lombok.Setter;
import mybar.domain.bar.Cocktail;
import mybar.domain.users.User;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class CocktailToUserPk implements Serializable {

    @Getter
    @Setter
    @ManyToOne
    private Cocktail cocktail;

    @Getter
    @Setter
    @ManyToOne
    private User user;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CocktailToUserPk that = (CocktailToUserPk) o;

        if (cocktail != null ? !cocktail.equals(that.cocktail) : that.cocktail != null)
            return false;
        if (user != null ? !user.equals(that.user) : that.user != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (cocktail != null ? cocktail.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

}