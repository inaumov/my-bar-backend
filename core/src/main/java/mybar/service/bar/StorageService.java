package mybar.service.bar;

import mybar.api.bar.IProduct;
import mybar.domain.EntityFactory;
import mybar.repository.bar.StorageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StorageService {

    @Autowired
    private StorageDao storageDao;

    public IProduct findById(int id) {
        return storageDao.read(id);
    }

    public IProduct findByName(String name) {
        return null;
    }

    public void saveBottle(IProduct product) {
        storageDao.create(EntityFactory.from(product));
    }

    public void updateBottle(IProduct product) {
        storageDao.update(EntityFactory.from(product));
    }

    public void deleteBottleById(int id) {

    }

    public List<IProduct> findAllBottles() {
        return new ArrayList<IProduct>(storageDao.findAll());
    }

    public void deleteAllBottles() {

    }

    public boolean isBottleExist(IProduct product) {
        return false;
    }

}