package mybar.repository.bar;

import mybar.domain.bar.Product;
import mybar.repository.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class StorageDao extends GenericDaoImpl<Product> {

    public List<Product> findAll() {
        TypedQuery<Product> q = em.createQuery("SELECT p FROM Product p", Product.class);
        List<Product> products = q.getResultList();
        return products;
    }

}