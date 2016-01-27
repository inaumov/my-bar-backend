package mybar.repository.bar;

import mybar.domain.bar.ingredient.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class IngredientDao {

    public static Logger log = LoggerFactory.getLogger(IngredientDao.class);

    @PersistenceContext
    protected EntityManager em;

    /**
     * Find ingredients by group name ordered by group name, kind.
     */
    public List<Ingredient> findByGroupName(String groupName) {
        switch (groupName) {
            case "Beverage":
            case "Drink":
            case "Additive": {
                try {
                    TypedQuery<Ingredient> q = em.createQuery("SELECT i FROM Ingredient i WHERE TYPE(i) = :type order by i.kind", Ingredient.class);
                    q.setParameter("type", Class.forName(groupName));
                    return q.getResultList();
                } catch (ClassNotFoundException e) {
                    log.error("Class for {} entity not found in classpath", groupName);
                }
            }
        }
        return null;
    }

    /**
     * Find all ingredients ordered by group name, kind.
     */
    public List<Ingredient> findAll() {
        TypedQuery<Ingredient> q = em.createQuery("SELECT i FROM Ingredient i order by i.class, i.kind", Ingredient.class);
        List<Ingredient> resultList = q.getResultList();
        return resultList;
    }

}