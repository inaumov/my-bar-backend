package mybar.exception;

import mybar.api.bar.ingredient.IBeverage;

public class UnknownBeverageException extends RuntimeException {

    private IBeverage beverage;

    public UnknownBeverageException(IBeverage beverage) {
        this.beverage = beverage;
    }

    public int getId() {
        return beverage.getId();
    }

    public String getKind() {
        return beverage.getKind();
    }

}