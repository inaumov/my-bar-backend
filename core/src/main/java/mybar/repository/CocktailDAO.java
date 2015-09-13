package mybar.repository;

import mybar.domain.Cocktail;
import mybar.domain.Menu;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CocktailDao extends GenericDaoImpl<Cocktail> {

    public List<Cocktail> findAllFor(Menu c) {
        TypedQuery<Cocktail> q = em.createQuery("SELECT m FROM Menu m WHERE m.menuId = :menuId", Cocktail.class);
        q.setParameter("menuId", c.getId());
        List<Cocktail> cocktails = q.getResultList();
        return cocktails;
    }

}