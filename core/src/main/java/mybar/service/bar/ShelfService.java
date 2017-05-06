package mybar.service.bar;

import com.google.common.base.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import mybar.api.bar.IBottle;
import mybar.api.bar.ingredient.IBeverage;
import mybar.domain.EntityFactory;
import mybar.domain.bar.Bottle;
import mybar.domain.bar.ingredient.Beverage;
import mybar.dto.bar.BottleDto;
import mybar.exception.BottleNotFoundException;
import mybar.exception.UnknownBeverageException;
import mybar.repository.bar.BottleDao;
import mybar.repository.bar.IngredientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;

import static mybar.dto.DtoFactory.toDto;

@Service
@Transactional
public class ShelfService {

    @Autowired(required = false)
    private BottleDao bottleDao;
    @Autowired(required = false)
    private IngredientDao ingredientDao;

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
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bottle.getBrandName()), "Brand name is required.");
        Preconditions.checkArgument(bottle.getBeverage() != null && bottle.getBeverage().getId() >= 0, "Beverage ID is required.");
        checkBeverageExists(bottle.getBeverage());

        Bottle newEntity = EntityFactory.from(bottle);
        newEntity.setBeverage(getBeverageById(bottle.getBeverage()));

        try {
            Bottle entity = bottleDao.create(newEntity);
            BottleDto dto = toDto(entity);
            clearCache();
            return dto;
        } catch (EntityExistsException e) {
            return null;
        }
    }

    public IBottle updateBottle(IBottle bottle) throws BottleNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(bottle.getBrandName()), "Brand name is required.");
        Preconditions.checkArgument(bottle.getBeverage() != null && bottle.getBeverage().getId() >= 0, "Beverage ID is required.");
        checkBeverageExists(bottle.getBeverage());

        Bottle newEntity = EntityFactory.from(bottle);
        newEntity.setBeverage(getBeverageById(bottle.getBeverage()));
        Bottle entity = bottleDao.update(newEntity);
        clearCache();
        return toDto(entity);
    }

    private void checkBeverageExists(IBeverage beverage) throws UnknownBeverageException {
        if (getBeverageById(beverage) == null) {
            throw new UnknownBeverageException(beverage);
        }
    }

    private Beverage getBeverageById(IBeverage beverage) {
        return ingredientDao.findBeverageById(beverage.getId());
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
            bottleCache = Lists.newArrayList(Lists.transform(bottleDao.findAll(), toDtoFunction));
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
        if (!CollectionUtils.isEmpty(bottleCache)) {
            bottleCache.clear();
        }
    }

}