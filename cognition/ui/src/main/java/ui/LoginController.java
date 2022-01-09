package ui;

import core.User;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * LoginController is responsible for the presentation logic in the Login view.
 */
public class LoginController extends Controller {
  @FXML
  private TextField usernameInput;

  @FXML
  private PasswordField passwordInput;

  @FXML
  private Button loginButton;

  private String feedbackErrorMessage = "No user with that username and password could be found.";

  public LoginController(RemoteCognitionAccess remoteCognitionAccess) {
    super(remoteCognitionAccess);
  }

  /**
   * Gets triggered when the user clicks the Login button.
   *
   * @param event is the ActionEvent on button click.
   */
  @FXML
  private void handleLogin(ActionEvent event) {
    String username = usernameInput.getText().toLowerCase();
    String password = passwordInput.getText();

    if (isValidLogin(username, password)) {
      goToDashboard(event, username);
    } else {
      setFeedbackText(feedbackErrorMessage);
    }
  }

  /**
   * Gets triggered when the user clicks to go to Dashboard view.
   *
   * @param event    is the ActionEvent on button click.
   * @param username is the username of the current user.
   */
  private void goToDashboard(ActionEvent event, String username) {
    User user;
    try {
      user = getRemoteCognitionAccess().read(username);
    } catch (IOException | InterruptedException e) {
      feedbackErrorMessage = "An error occurred when reading the user from file";
      setFeedbackText(feedbackErrorMessage);
      return;
    }

    changeToView(event, new DashboardController(user, getRemoteCognitionAccess()),
            "Dashboard");
  }

  /**
   * Switches to the Register view.
   *
   * @param event is the event on button click
   */
  @FXML
  private void goToRegister(ActionEvent event) {
    changeToView(event, new RegisterController(getRemoteCognitionAccess()),
            "Register");
  }

  /**
   * Validates the provided input.
   *
   * @param username is the String representation of the provided username
   * @param password is the String representation of the provided password
   * @return a boolean indicating whether the input is valid.
   */
  private boolean isValidLogin(String username, String password) {
    List<User> users;

    // Read users in local storage
    try {
      users = getRemoteCognitionAccess().readUsers();
    } catch (IOException | InterruptedException e) {
      feedbackErrorMessage = "An error occurred when loading local storage.";
      return false;
    }

    // Find user based on parameters
    if (users != null) {
      for (User user : users) {
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
          return true;
        }
      }
    }

    feedbackErrorMessage = "No user with that username and password could be found.";
    return false;
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
}
