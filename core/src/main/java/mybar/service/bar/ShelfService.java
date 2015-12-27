package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import mybar.api.bar.IBottle;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Bottle;
import mybar.repository.bar.BottleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.List;

@Service
@Transactional
public class ShelfService {

    @Autowired
    private BottleDao bottleDao;

    // kind of caching :)
    private List<Bottle> all;
    private boolean shouldUpdate;

    public IBottle findById(int id) {
        if (all != null) {
            for (Bottle p : all) {
                if (p.getId() == id) {
                    return p.toDto();
                }
            }
        }
        return bottleDao.read(id).toDto();
    }

    public boolean saveBottle(IBottle bottle) {
        Bottle entity = null;
        try {
            entity = bottleDao.create(EntityFactory.from(bottle));
        } catch (EntityExistsException e) {
            return false;
        }
        all.add(entity);
        return true;
    }

    public void updateBottle(IBottle bottle) {
        Bottle entity = bottleDao.update(EntityFactory.from(bottle));
        if (entity.getId() != 0) {
            shouldUpdate = true;
        }
    }

    public void deleteBottleById(int id) {
        bottleDao.delete(id);
        for (Bottle p : all) {
            if (p.getId() == id) {
                all.remove(p);
                break;
            }
        }
    }

    public List<IBottle> findAllBottles() {
        if (all == null || shouldUpdate) {
            all = bottleDao.findAll();
        }
        return Lists.transform(all, new Function<Bottle, IBottle>() {
            @Override
            public IBottle apply(Bottle bottle) {
                return bottle.toDto();
            }
        });
    }

    public int deleteAllBottles() {
        return bottleDao.destroyAll();
    }

}