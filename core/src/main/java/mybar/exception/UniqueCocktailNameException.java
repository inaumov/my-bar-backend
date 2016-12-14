package mybar.exception;

public class UniqueCocktailNameException extends RuntimeException {

    private final String name;

    public UniqueCocktailNameException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}