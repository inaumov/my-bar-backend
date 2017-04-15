package mybar.service.bar;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import mybar.api.bar.IBottle;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Bottle;
import mybar.exception.BottleNotFoundException;
import mybar.repository.bar.BottleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.List;

import static mybar.dto.DtoFactory.*;

@Service
@Transactional
public class ShelfService {

    @Autowired(required = false)
    private BottleDao bottleDao;

    // kind of caching :)
    private List<Bottle> all;
    private boolean shouldUpdate;

    public static final Function<Bottle, IBottle> toDtoFunction = new Function<Bottle, IBottle>() {
        @Override
        public IBottle apply(Bottle bottle) {
            return toDto(bottle);
        }
    };

    public IBottle findById(int id) throws BottleNotFoundException {
        if (all != null) {
            for (Bottle bottle : all) {
                if (bottle.getId() == id) {
                    return toDto(bottle);
                }
            }
        }
        return toDto(bottleDao.read(id));
    }

    public IBottle saveBottle(IBottle bottle) {
        Bottle entity = null;
        try {
            entity = bottleDao.create(EntityFactory.from(bottle));
        } catch (EntityExistsException e) {
            return toDto(entity);
        }
        all.add(entity); // TODO NPE
        return toDto(entity);
    }

    public IBottle updateBottle(IBottle bottle) throws BottleNotFoundException {
        Bottle entity = bottleDao.update(EntityFactory.from(bottle));
        if (entity.getId() != 0) {
            shouldUpdate = true;
        }
        return toDto(entity);
    }

    public void deleteBottleById(int id) throws BottleNotFoundException {
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
        return ImmutableList.copyOf(Lists.transform(all, toDtoFunction));
    }

    public int deleteAllBottles() {
        return bottleDao.destroyAll();
    }

    public boolean isBottleAvailable(int ingredientId) {
        return ingredientId % 2 == 1; // todo
    }

}