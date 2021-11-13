package mybar.domain.rates;

import lombok.Getter;
import lombok.Setter;
import mybar.domain.bar.Cocktail;
import mybar.domain.users.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "RATES")
@AssociationOverrides({
        @AssociationOverride(name = "pk.cocktail", joinColumns = @JoinColumn(name = "COCKTAIL_ID")),
        @AssociationOverride(name = "pk.user", joinColumns = @JoinColumn(name = "USERNAME"))
})
public class Rate {

    @Getter
    @Setter
    @EmbeddedId
    private CocktailToUserPk pk = new CocktailToUserPk();

    @Column(name = "RATED_AT")
    private LocalDateTime ratedAt;

    @Column(name = "STARS")
    private int stars;

    @Transient
    public Cocktail getCocktail() {
        return getPk().getCocktail();
    }

    public void setCocktail(Cocktail cocktail) {
        getPk().setCocktail(cocktail);
    }

    @Transient
    public User getUser() {
        return getPk().getUser();
    }

    public void setUser(User user) {
        getPk().setUser(user);
    }

}