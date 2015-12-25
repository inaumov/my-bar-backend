package mybar.api.bar;

import mybar.State;

import java.sql.Blob;
import java.util.Collection;
import java.util.Map;

public interface ICocktail {

    int getId();

    String getName();

    // TODO to follow better way
    Map<String, ? extends Collection<? extends IInside>> getInsides();
    //<T extends Collection<? extends IInside>>Map<String, T> getInsideList();

    int getMenuId();

    String getDescription();

    State getState();

    String getImageUrl();

}