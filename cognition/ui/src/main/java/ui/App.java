package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Launches the JavaFX application.
 */
public class App extends Application {

  private Scene scene;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    // Load FXML view
    FXMLLoader loader = getLoader("Login");

    // Set state in controller.
    // The RemoteCognitionAccess that gets initialized here is common for the entire
    // application.
    // This UserStorage is set in all other controllers, in order to maintain
    // continuity.
    // This logic also, most importantly,
    // separates persistent storage between application logic and unit and
    // end-to-end tests.
    loader.setController(
            new LoginController(new RemoteCognitionAccess())
    );

    Parent root = loader.load();

    // Switch stage
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Gets the loader for a given FXML file.
   *
   * @param fxml is the String representation of the FXML filename.
   * @return an instance of the FXMLLoader with the set location based on provided FXML filename.
   */
  private FXMLLoader getLoader(String fxml) {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("views/" + fxml + ".fxml"));
    return loader;
  }
}
