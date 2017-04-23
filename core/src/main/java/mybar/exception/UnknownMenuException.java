package mybar.exception;

public class UnknownMenuException extends RuntimeException {

    private String name;

    public UnknownMenuException(String name) {
        this.name = name;
    }
     
    public String getName() {
        return name;
    }
 
}