package mybar.dao;

import org.springframework.stereotype.Repository;
import mybar.entity.Category;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CategoryDao extends GenericDaoImpl<Category> {

    public List<Category> findAll() {
        TypedQuery<Category> q = em.createQuery("SELECT c FROM Category c", Category.class);
        List<Category> categories = q.getResultList();
        return categories;
    }

}