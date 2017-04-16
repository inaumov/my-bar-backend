package mybar.service.bar;

import com.google.common.base.*;
import com.google.common.collect.*;
import mybar.api.bar.IBottle;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Bottle;
import mybar.dto.bar.BottleDto;
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
    private List<IBottle> bottleCache;

    public static final Function<Bottle, IBottle> toDtoFunction = new Function<Bottle, IBottle>() {
        @Override
        public IBottle apply(Bottle bottle) {
            return toDto(bottle);
        }
    };

    public IBottle findById(final int id) throws BottleNotFoundException {
        Predicate<IBottle> findBottlePredicate = new Predicate<IBottle>() {
            @Override
            public boolean apply(IBottle bottle) {
                return bottle.getId() == id;
            }
        };
        return Iterables.find(findAllBottles(), findBottlePredicate, toDto(bottleDao.read(id)));
    }

    public IBottle saveBottle(IBottle bottle) {
        try {
            Bottle entity = bottleDao.create(EntityFactory.from(bottle));
            BottleDto dto = toDto(entity);
            clearCache();
            return dto;
        } catch (EntityExistsException e) {
            return null;
        }
    }

    public IBottle updateBottle(IBottle bottle) throws BottleNotFoundException {
        Bottle entity = bottleDao.update(EntityFactory.from(bottle));
        clearCache();
        return toDto(entity);
    }

    public void deleteBottleById(final int id) throws BottleNotFoundException {
        bottleDao.delete(id);
        Iterables.removeIf(findAllBottles(), new Predicate<IBottle>() {
            @Override
            public boolean apply(IBottle bottle) {
                return bottle.getId() == id;
            }
        });
    }

    public List<IBottle> findAllBottles() {
        if (bottleCache == null || bottleCache.isEmpty()) {
            bottleCache = FluentIterable.from(bottleDao.findAll()).transform(toDtoFunction).toList();
        }
        return bottleCache;
    }

    public int deleteAllBottles() {
        clearCache();
        return bottleDao.destroyAll();
    }

    public boolean isBottleAvailable(final int ingredientId) {
        Optional<IBottle> bottleById = Iterables.tryFind(findAllBottles(), new Predicate<IBottle>() {
            @Override
            public boolean apply(IBottle bottle) {
                return bottle.getId() == ingredientId;
            }
        });
        return bottleById.isPresent() && bottleById.get().isInShelf();
    }

    private void clearCache() {
        bottleCache.clear();
    }

}