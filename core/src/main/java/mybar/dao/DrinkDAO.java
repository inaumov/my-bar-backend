package mybar.dao;

import org.springframework.stereotype.Repository;
import mybar.entity.Category;
import mybar.entity.Drink;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class DrinkDao extends GenericDaoImpl<Drink> {

    public List<Drink> findAllFor(Category c) {
        TypedQuery<Drink> q = em.createQuery("SELECT m FROM Menu m WHERE m.categoryId = :categoryId", Drink.class);
        q.setParameter("categoryId", c.getId());
        List<Drink> drinks = q.getResultList();
        return drinks;
    }

}