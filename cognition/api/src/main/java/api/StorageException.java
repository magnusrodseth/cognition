package api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for when an unexpected error occurs
 * with the persistent storage.
 */
@ResponseStatus(
        value = HttpStatus.CONFLICT,
        reason = "An error occurred with the persistent storage"
)
public class StorageException extends RuntimeException {
  public StorageException(String message) {
    super(message);
  }

  public StorageException() {
    super("An error occurred with the persistent storage");
  }
}
