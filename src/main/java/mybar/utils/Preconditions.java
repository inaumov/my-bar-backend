package mybar.utils;

public final class Preconditions {
    private Preconditions() {
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean isValid, String errorMessage) {
        if (!isValid) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}