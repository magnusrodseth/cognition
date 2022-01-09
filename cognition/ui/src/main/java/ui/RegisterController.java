package ui;

import core.Flashcard;
import core.Quiz;
import core.User;
import core.UserValidation;
import core.tools.Tools;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * RegisterController handles the presentation logic when registering a user.
 */
public class RegisterController extends Controller {
  /**
   * The default success message for feedback in the RegisterController. Will not
   * be updated, and is thus static. This field is used when testing. Hence, a
   * getter is needed.
   */
  private static final String feedbackSuccessMessage = "User created successfully!";

  @FXML
  private TextField usernameInput;
  @FXML
  private PasswordField passwordInput;
  @FXML
  private PasswordField passwordRepeatInput;
  @FXML
  private Labeled feedback;

  private String feedbackErrorMessage;

  public RegisterController(RemoteCognitionAccess remoteCognitionAccess) {
    super(remoteCognitionAccess);
  }

  /**
   * This method gets triggered when the user clicks on the button for registering a user.
   * Validates input, and registers user on valid input.
   * Gives the user a fitting feedback message if there's invalid input.
   */
  @FXML
  public void handleRegister() {
    String username = usernameInput.getText().toLowerCase();
    String password = passwordInput.getText();
    String passwordRepeat = passwordRepeatInput.getText();

    if (isValidRegister(username, password, passwordRepeat)) {
      registerUser(username, password);
    } else {
      setFeedbackErrorMode(true);
      feedback.setText(feedbackErrorMessage);
    }
  }

  /**
   * Validates the provided input.
   *
   * @param username       is the String representation of the provided username.
   * @param password       is the String representation of the provided password.
   * @param passwordRepeat is the String representation of the provided repeated
   *                       password.
   * @return a boolean indicating whether the input is valid.
   */
  private boolean isValidRegister(String username, String password, String passwordRepeat) {
    if (User.isValidUsername(username) == UserValidation.ILLEGAL_INPUT_LENGTH) {
      feedbackErrorMessage = "Username has to be at least 3 characters long";
      return false;
    }

    if (User.isValidUsername(username) == UserValidation.ILLEGAL_INPUT) {
      feedbackErrorMessage = "User can only contain letters and numbers";
      return false;
    }

    if (User.isValidPassword(password) == UserValidation.ILLEGAL_INPUT_LENGTH) {
      feedbackErrorMessage = "Password must be at least 6 characters long";
      return false;
    }

    if (User.isValidPassword(password) == UserValidation.ILLEGAL_INPUT) {
      feedbackErrorMessage = "Password cannot contain only whitespaces";
      return false;
    }

    if (!password.equals(passwordRepeat)) {
      feedbackErrorMessage = "Passwords do not match";
      return false;
    }

    // Get users from local storage
    List<User> users = null;
    try {
      users = getRemoteCognitionAccess().readUsers();
    } catch (IOException | InterruptedException e) {
      feedbackErrorMessage = "An error occurred when reading from local storage.";
      feedback.setText(feedbackErrorMessage);
    }

    // Find user
    if (users != null) {
      for (User user : users) {
        if (user.getUsername().equals(username)) {
          feedbackErrorMessage = username + " is already in use.";
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Registers a new user. Also assigns some default quizzes to the new user.
   *
   * @param username is the users username
   * @param password is the users password
   */
  private void registerUser(String username, String password) {
    try {
      User user = new User(username, password);

      // Every new user gets assigned a default quiz,
      // in order to quickly use all features in the application
      Quiz introductionQuiz = createDefaultQuiz();
      user.addQuiz(introductionQuiz);

      getRemoteCognitionAccess().create(user);

      setFeedbackErrorMode(false);
      feedback.setText(feedbackSuccessMessage);
    } catch (IOException | InterruptedException e) {
      feedback.setText("An error occurred when writing to local storage.");
    }

  }

  /**
   * Triggered when the view switches to "Login" view.
   *
   * @param event is the ActionEvent on button click.
   */
  @FXML
  public void goToLogin(ActionEvent event) {
    changeToView(event, new LoginController(getRemoteCognitionAccess()),
            "Login");
  }

  /**
   * Gets the feedback error message. This method is useful during testing to
   * validate that the feedback the user receives is the correct feedback to
   * display.
   *
   * @return a String representation of the feedback error message.
   */
  public String getFeedbackErrorMessage() {
    return feedbackErrorMessage;
  }

  /**
   * Gets the feedback success message. This method is useful during testing to
   * validate that the feedback the user receives is the correct feedback to
   * display.
   *
   * @return a String representation of the feedback success message.
   */
  public String getFeedbackSuccessMessage() {
    return feedbackSuccessMessage;
  }

  private void setFeedbackErrorMode(boolean mode) {
    if (mode) {
      feedback.setStyle("-fx-text-fill: red");
    } else {
      feedback.setStyle("-fx-text-fill: green");
    }
  }

  /**
   * Creates a default quiz.
   * This is added to each new registered user, in order for them to quickly use
   * all functionality in the application.
   *
   * @return the default quiz.
   */
  public static Quiz createDefaultQuiz() {
    final Quiz introductionQuiz = new Quiz(Tools.createUuid(), "Introduction to Cognition",
            "A quiz to introduce you to the functionality of Cognition");
    List<Flashcard> flashcards = new ArrayList<>();
    flashcards.add(new Flashcard(Tools.createUuid(), "What is the 3rd letter of the alphabet?",
            "c"));
    flashcards.add(new Flashcard(Tools.createUuid(),
            "What is the chemical formula for water? ", "H2O"));
    flashcards.add(new Flashcard(Tools.createUuid(), "How tall is Mount Everest? (m)", "8849"));
    introductionQuiz.addFlashcards(flashcards);

    return introductionQuiz;
  }
}
