package mybar.service;

import mybar.api.IProduct;
import mybar.domain.EntityFactory;
import mybar.repository.StorageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {

    @Autowired
    private StorageDao storageDao;

    @Transactional
    public List<IProduct> getAllBottles() {
        return new ArrayList<IProduct>(storageDao.findAll());
    }

    @Transactional
    public void addBottle(IProduct product) {
        storageDao.create(EntityFactory.from(product));
    }

    @Transactional
    public void updateBottle(IProduct product) {
        storageDao.update(EntityFactory.from(product));
    }

}
