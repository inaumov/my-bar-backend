package mybar.repository.bar;

import mybar.domain.bar.Bottle;
import mybar.repository.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class BottleDao extends GenericDaoImpl<Bottle> {

    public List<Bottle> findAll() {
        TypedQuery<Bottle> q = em.createQuery("SELECT b FROM Bottle b", Bottle.class);
        List<Bottle> bottles = q.getResultList();
        return bottles;
    }

    public int destroyAll() {
        int deletedCount = em.createQuery("DELETE FROM Bottle").executeUpdate();
        return deletedCount;
    }

}