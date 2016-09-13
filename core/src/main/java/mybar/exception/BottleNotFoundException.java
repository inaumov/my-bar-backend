package mybar.exception;

public class BottleNotFoundException extends RuntimeException {
 
    private final int bottleId;
     
    public BottleNotFoundException(int id) {
        bottleId = id;
    }
     
    public int getBottleId() {
        return bottleId;
    }
 
}