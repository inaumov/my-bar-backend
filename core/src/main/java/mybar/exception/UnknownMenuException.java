package mybar.exception;

public class UnknownMenuException extends RuntimeException {

    private String menuName;

    public UnknownMenuException(String menuName) {
        this.menuName = menuName;
    }
     
    public String getName() {
        return menuName;
    }
 
}