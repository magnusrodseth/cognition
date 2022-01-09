package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

public class AppTest extends ApplicationTest {
  private Scene scene;
  public static final int TEST_PORT = 3000;

  /**
   * Checks that the application can start. If the entire method runs without
   * failing, the test passes.
   */
  @Override
  public void start(Stage stage) throws Exception {
    // Load FXML view
    FXMLLoader loader = getLoader("Login");

    // Set state in controller
    LoginController loginController = new LoginController(new RemoteCognitionAccess(TEST_PORT));
    loader.setController(loginController);

    // Switch stage
    scene = new Scene(loader.load());
    stage.setScene(scene);
    stage.show();
  }

  private FXMLLoader getLoader(String fxml) {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("views/" + fxml + ".fxml"));
    return loader;
  }
}
