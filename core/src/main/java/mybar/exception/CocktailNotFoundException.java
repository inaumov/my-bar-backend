package mybar.exception;

public class CocktailNotFoundException extends RuntimeException {

    private final int cocktailId;

    public CocktailNotFoundException(int id) {
        cocktailId = id;
    }

    public int getCocktailId() {
        return cocktailId;
    }
 
}