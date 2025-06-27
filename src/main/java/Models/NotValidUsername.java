package Models;

public class NotValidUsername extends RuntimeException {
    public NotValidUsername(String message) {
      super(message);
    }
}
