package mybar.api;

import java.util.Collection;

public interface ICategory {

    int getId();

    String getName();

    Collection<? extends IDish> getDishes();

}