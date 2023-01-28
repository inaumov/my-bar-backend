package mybar.exception;

public class CocktailNotFoundException extends RuntimeException {

    private final String cocktailId;

    public CocktailNotFoundException(String id) {
        cocktailId = id;
    }

    public String getCocktailId() {
        return cocktailId;
    }
 
}