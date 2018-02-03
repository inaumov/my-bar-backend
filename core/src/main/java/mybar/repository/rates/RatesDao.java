package mybar.repository.rates;

import mybar.repository.GenericDaoImpl;
import org.springframework.stereotype.Repository;
import mybar.History;
import mybar.api.bar.ICocktail;
import mybar.domain.rates.Rate;

import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Repository
public class RatesDao extends GenericDaoImpl<Rate> {

    public boolean checkRateExistsForCocktail(ICocktail cocktail) {
        TypedQuery<Rate> q = em.createQuery("SELECT r FROM Rate r WHERE r.pk.cocktail.id = :cocktail_id", Rate.class);
        q.setParameter("cocktail_id", cocktail.getId());
        q.setMaxResults(1);
        List<Rate> rates = q.getResultList();
        return !rates.isEmpty();
    }

    public List<Rate> findAllRatesForCocktail(ICocktail cocktail) {
        TypedQuery<Rate> q = em.createQuery("SELECT r FROM Rate r WHERE r.pk.cocktail.id = :cocktail_id", Rate.class);
        q.setParameter("cocktail_id", cocktail.getId());
        return q.getResultList();
    }

    public List<History> getRatedCocktailsForPeriod(Date startDate, Date endDate) {
        TypedQuery<History> q = em.createQuery("SELECT new mybar.History(c.name, r.stars) FROM Cocktail c, Rate r WHERE r.pk.cocktail.id = c.id AND r.ratedAt >= :startDate AND r.ratedAt <= :endDate", History.class);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        return q.getResultList();
    }

}