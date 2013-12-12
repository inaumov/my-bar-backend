package mybar.dao;

import org.springframework.stereotype.Repository;
import mybar.entity.Category;
import mybar.entity.Dish;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class DishDAO extends GenericDaoImpl<Dish> {

    public List<Dish> findAllFor(Category c) {
        TypedQuery<Dish> q = em.createQuery("SELECT m FROM Menu m WHERE m.categoryId = :categoryId", Dish.class);
        q.setParameter("categoryId", c.getId());
        List<Dish> dishes = q.getResultList();
        return dishes;
    }

}