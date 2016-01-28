package mybar.repository.bar;

import mybar.domain.bar.ingredient.Additive;
import mybar.domain.bar.ingredient.Beverage;
import mybar.domain.bar.ingredient.Drink;
import mybar.domain.bar.ingredient.Ingredient;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class IngredientDao {

    @PersistenceContext
    protected EntityManager em;

    /**
     * Find ingredients by one group name ordered by group name, kind.
     */
    public List<Ingredient> findByGroupName(String groupName) {
        Class<? extends Ingredient> clazz;
        switch (groupName) {
            case "Beverage": {
                clazz = Beverage.class;
                break;
            }
            case "Drink": {
                clazz = Drink.class;
                break;
            }
            case "Additive": {
                clazz = Additive.class;
                break;
            }
            default: {
                return null;
            }
        }
        TypedQuery<Ingredient> q = em.createNamedQuery("findByGroupName", Ingredient.class);
        q.setParameter("type", clazz);
        return q.getResultList();
    }

    /**
     * Find all ingredients ordered by group name, kind.
     */
    public List<Ingredient> findAll() {
        TypedQuery<Ingredient> q = em.createNamedQuery("findAll", Ingredient.class);
        return q.getResultList();
    }

}