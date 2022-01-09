package ui;

import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static ui.TestFxHelper.waitForFxEvents;

public class LoginTest extends ApplicationTest {
  private final String validUsername = "valid-user";
  private final String validPassword = "valid-password";
  private Scene scene;
  private LoginController loginController;

  // Mock RemoteCognitionAccess in order to test the client application in isolation
  private final RemoteCognitionAccess mockRemoteCognitionAccess = Mockito.mock(RemoteCognitionAccess.class);

  private TestFxHelper helper = new TestFxHelper();

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = getLoader("Login");

    this.loginController = new LoginController(mockRemoteCognitionAccess);

    loader.setController(loginController);

    scene = new Scene(loader.load());
    stage.setScene(scene);
    stage.show();
  }

  private FXMLLoader getLoader(String fxml) {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("views/" + fxml + ".fxml"));
    return loader;
  }

  @Test
  @DisplayName("Controller is defined.")
  void controllerIsDefined() {
    Assertions.assertNotNull(loginController);
  }

  @Test
  @DisplayName("Accessor is defined.")
  void accessorIsDefined() {
    Assertions.assertNotNull(mockRemoteCognitionAccess);
  }

  /**
   * Tests if an existing user in local storage can log in. Note that this test is
   * currently, as it is not completely done.
   */
  @Test
  @DisplayName("Existing user can log in.")
  void existingUserCanLogIn() {
    // mock created users
    List<User> users = List.of(new User(validUsername, validPassword));

    try {
      Mockito.when(mockRemoteCognitionAccess.readUsers())
              .thenReturn(users);
      Mockito.when(mockRemoteCognitionAccess.read(validUsername))
              .thenReturn(users.get(0));
    } catch (InterruptedException | IOException e) {
      fail();
    }

    clickOn(helper.findTextField(node -> true, "#usernameInput", 0)).write(validUsername);

    clickOn(helper.findTextField(node -> true, "#passwordInput", 0)).write(validPassword);

    waitForFxEvents();

    clickOn("#loginButton");

    waitForFxEvents();

    // Check that we loaded Dashboard view
    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Dashboard"));
    waitForFxEvents();
  }

  @Test
  @DisplayName("Can go to register view.")
  void canGoToRegisterView() {
    // Check that the Login view was loaded correctly
    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Login"));
    waitForFxEvents();

    // Click to switch view
    clickOn("#goToRegisterButton");
    waitForFxEvents();

    // Check that the Register view has loaded correctly
    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Register"));
    waitForFxEvents();
  }

  @Test
  @DisplayName("Non-existing user cannot log in.")
  void nonExistingUserCannotLogIn() {
    // mock already created users
    List<User> users = new ArrayList<>();
    try {
      Mockito.when(mockRemoteCognitionAccess.readUsers())
          .thenReturn(users);
    } catch (InterruptedException | IOException e) {
      fail();
    }
    // This username and password is not in local storage, as users.json is cleared
    // after each test
    verifyInputData("non-existing-user", "non-existing-password", loginController.getFeedbackErrorMessage());
  }

  /**
   * A helper method for verifying input data.
   *
   * @param username is the provided username in the input form
   * @param password is the provided password in the input form
   * @param feedback is the provided feedback from the form
   */
  private void verifyInputData(String username, String password, String feedback) {
    // Input data into UI
    clickOn(helper.findTextField(node -> true, "#usernameInput", 0)).write(username);
    waitForFxEvents();
    clickOn(helper.findTextField(node -> true, "#passwordInput", 0)).write(password);

    waitForFxEvents();
    clickOn("#loginButton");
    waitForFxEvents();

    // Validate that user got correct feedback in UI
    FxAssert.verifyThat("#feedback", LabeledMatchers.hasText(feedback));
    waitForFxEvents();
  }
}
