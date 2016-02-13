package mybar.repository.bar;

import mybar.domain.bar.Cocktail;
import mybar.domain.bar.Menu;
import mybar.repository.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CocktailDao extends GenericDaoImpl<Cocktail> {

    @Deprecated
    public List<Cocktail> findAllFor(Menu menu) {
        TypedQuery<Cocktail> q = em.createQuery("SELECT m FROM Menu m WHERE m.menuId = :menuId", Cocktail.class);
        q.setParameter("menuId", menu.getId());
        List<Cocktail> cocktails = q.getResultList();
        return cocktails;
    }

}