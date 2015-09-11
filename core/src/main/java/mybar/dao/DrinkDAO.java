package mybar.dao;

import mybar.entity.Menu;
import org.springframework.stereotype.Repository;
import mybar.entity.Drink;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class DrinkDAO extends GenericDaoImpl<Drink> {

    public List<Drink> findAllFor(Menu c) {
        TypedQuery<Drink> q = em.createQuery("SELECT m FROM Menu m WHERE m.menuId = :menuId", Drink.class);
        q.setParameter("menuId", c.getId());
        List<Drink> drinks = q.getResultList();
        return drinks;
    }

}