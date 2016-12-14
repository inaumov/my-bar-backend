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

    public boolean findCocktailByName(String cocktailName) {
        TypedQuery<String> q = em.createQuery("select c.name from Cocktail c where lower(c.name) like :cocktailName", String.class);
        q.setParameter("cocktailName", cocktailName.toLowerCase());
        List<String> resultList = q.getResultList();
        q.setMaxResults(1);
        return !resultList.isEmpty();
    }

}