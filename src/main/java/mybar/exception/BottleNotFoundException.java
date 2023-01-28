package mybar.exception;

public class BottleNotFoundException extends RuntimeException {
 
    private final String bottleId;
     
    public BottleNotFoundException(String id) {
        bottleId = id;
    }
     
    public String getBottleId() {
        return bottleId;
    }
 
}