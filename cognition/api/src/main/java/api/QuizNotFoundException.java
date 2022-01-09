package api;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for when no quiz was found on the server.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Quiz")
public class QuizNotFoundException extends RuntimeException {
  public QuizNotFoundException(String message) {
    super(message);
  }

  public QuizNotFoundException() {
    super("No quiz was found.");
  }
}
