package mybar.repository.rates;

import mybar.History;
import mybar.api.bar.ICocktail;
import mybar.domain.bar.Cocktail;
import mybar.domain.rates.Rate;
import mybar.domain.users.User;
import mybar.repository.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RatesDao extends GenericDaoImpl<Rate> {

    public boolean checkRateExistsForCocktail(ICocktail cocktail) {
        TypedQuery<Rate> q = em.createQuery("SELECT r FROM Rate r WHERE r.pk.cocktail.id = :cocktail_id", Rate.class);
        q.setParameter("cocktail_id", cocktail.getId());
        q.setMaxResults(1);
        List<Rate> rates = q.getResultList();
        return !rates.isEmpty();
    }

    public List<Rate> findAllRatesForCocktail(Cocktail cocktail) {
        TypedQuery<Rate> q = em.createQuery("SELECT r FROM Rate r WHERE r.pk.cocktail = :cocktail", Rate.class);
        q.setParameter("cocktail", cocktail);
        return q.getResultList();
    }

    public List<Rate> findAllRatesForUser(User user) {
        TypedQuery<Rate> q = em.createQuery("SELECT r FROM Rate r WHERE r.pk.user = :user", Rate.class);
        q.setParameter("user", user);
        return q.getResultList();
    }

    public List<History> getRatedCocktailsForPeriod(LocalDate startDate, LocalDate endDate) {
        TypedQuery<History> q = em.createQuery("SELECT new mybar.History(c.name, r.stars, r.pk.user.id) FROM Cocktail c, Rate r WHERE r.pk.cocktail.id = c.id AND r.ratedAt >= :startDate AND r.ratedAt <= :endDate", History.class);
        q.setParameter("startDate", Date.valueOf(startDate));
        q.setParameter("endDate", Date.valueOf(endDate));
        return q.getResultList();
    }

    public Map<String, Double> findAllAverageRates() {
        TypedQuery<Tuple> q = em.createQuery(
                "SELECT r.pk.cocktail.id as cocktail_id, avg (r.stars) as avg_stars FROM Rate r group by r.pk.cocktail.id", Tuple.class
        );
        Map<String, Double> allAverageRates = new HashMap<>();

        for (Tuple tuple : q.getResultList()) {
            String cocktail_id = tuple.get("cocktail_id", String.class);
            Double avg_stars = tuple.get("avg_stars", Double.class);
            allAverageRates.put(cocktail_id, avg_stars);
        }
        return allAverageRates;
    }

    public Rate findBy(String userId, String cocktailId) {
        TypedQuery<Rate> q = em.createQuery("SELECT r FROM Rate r WHERE r.pk.user.id = :userId and r.pk.cocktail.id = :cocktailId", Rate.class);
        q.setParameter("userId", userId);
        q.setParameter("cocktailId", cocktailId);
        return q.getSingleResult();
    }

}