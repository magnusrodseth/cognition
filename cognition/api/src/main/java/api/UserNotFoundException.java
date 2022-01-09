package api;

/**
 * Exception for when no user was found on the server.
 */
public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException() {
    super("No users was found.");
  }
}
