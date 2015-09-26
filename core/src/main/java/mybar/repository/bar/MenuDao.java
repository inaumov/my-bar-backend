package mybar.repository.bar;

import mybar.domain.bar.Menu;
import mybar.repository.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class MenuDao extends GenericDaoImpl<Menu> {

    public List<Menu> findAll() {
        TypedQuery<Menu> q = em.createQuery("SELECT m FROM Menu m", Menu.class);
        List<Menu> menus = q.getResultList();
        return menus;
    }

}