package mybar.service.bar;

import mybar.api.bar.IProduct;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Product;
import mybar.repository.bar.StorageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StorageService {

    @Autowired
    private StorageDao storageDao;

    // kind of caching :)
    private List<Product> all;
    private boolean shouldUpdate;

    public IProduct findById(int id) {
        if (all != null) {
            for (Product p : all) {
                if (p.getId() == id) {
                    return p;
                }
            }
        }
        return storageDao.read(id);
    }

    public boolean saveBottle(IProduct product) {
        Product entity = null;
        try {
            entity = storageDao.create(EntityFactory.from(product));
        } catch (EntityExistsException e) {
            return false;
        }
        all.add(entity);
        return true;
    }

    public void updateBottle(IProduct product) {
        Product entity = storageDao.update(EntityFactory.from(product));
        if (entity.getId() != 0) {
            shouldUpdate = true;
        }
    }

    public void deleteBottleById(int id) {
        storageDao.delete(id);
        for (Product p : all) {
            if (p.getId() == id) {
                all.remove(p);
                break;
            }
        }
}

    public List<IProduct> findAllBottles() {
        if (all == null || shouldUpdate) {
            all = storageDao.findAll();
        }
        return new ArrayList<IProduct>(all);
    }

    public int deleteAllBottles() {
        return storageDao.destroyAll();
    }

}