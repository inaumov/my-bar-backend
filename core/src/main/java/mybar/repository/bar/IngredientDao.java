package mybar.repository.bar;

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
     * Find ingredients by group name.
     */
    public List<Ingredient> findByGroupName(String groupName) {
        List<Ingredient> ingredients = null;
        switch (groupName) {
            case "Beverage":
            case "Drink":
            case "Additive": {
                TypedQuery<Ingredient> q = em.createQuery("SELECT i FROM Ingredient i WHERE i.groupName = :groupName ORDER BY i.groupName, i.kind", Ingredient.class);
                q.setParameter("groupName", groupName);
                ingredients = q.getResultList();
                break;
            }
        }
        return ingredients;
    }

    /**
     * Find all ingredients order by group name, kind.
     */
    public List<Ingredient> findAll() {
        TypedQuery<Ingredient> q = em.createQuery("SELECT i FROM Ingredient i order by i.groupName, i.kind", Ingredient.class);
        List<Ingredient> resultList = q.getResultList();
        return resultList;
    }

}