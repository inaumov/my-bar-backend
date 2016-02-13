package mybar.api.bar;

import mybar.State;

import java.util.Collection;
import java.util.Map;

public interface ICocktail {

    int getId();

    String getName();

    Map<String, ? extends Collection<? extends IInside>> getInsideItems();

    int getMenuId();

    String getDescription();

    State getState();

    String getImageUrl();

}