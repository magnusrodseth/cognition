package api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for when the username/UUID is already in use.
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Identifier already in use")
public class IdentifierAlreadyInUseException extends RuntimeException {

  public IdentifierAlreadyInUseException(String identifier) {
    super("The identifier: " + identifier + " is already in use.");
  }

  public IdentifierAlreadyInUseException() {
    super("Identifier already in use.");
  }
}
