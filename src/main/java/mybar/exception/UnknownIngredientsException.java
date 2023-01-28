package mybar.exception;

import java.util.List;

public class UnknownIngredientsException extends RuntimeException {

    private List<Integer> ingredientIds;

    public UnknownIngredientsException(List<Integer> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public List<Integer> getIds() {
        return ingredientIds;
    }

}