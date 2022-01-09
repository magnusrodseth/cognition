package ui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller is an abstract class with the common functionality
 * for all presentation layer controllers.
 */
public abstract class Controller {

  @FXML
  private Label feedback;

  private RemoteCognitionAccess remoteCognitionAccess;

  /**
   * Initializes an extensions of the Controller abstract class.
   * See {@link ui.Controller} for more information.
   *
   * @param remoteCognitionAccess is REST API accessor
   * @throws NullPointerException if remoteCognitionAccess is null
   */
  public Controller(RemoteCognitionAccess remoteCognitionAccess) throws NullPointerException {
    if (remoteCognitionAccess == null) {
      throw new NullPointerException("Remote access cannot be null.");
    }

    this.remoteCognitionAccess = remoteCognitionAccess;
  }

  private Stage getStage(ActionEvent event) {
    Node node = (Node) event.getSource();
    return (Stage) node.getScene().getWindow();
  }

  /**
   * Gets the loader for an FXML view.
   *
   * @param fxml is the String representation of the FXML filename.
   * @return an FXMLLoader with the location set to the provided FXML file.
   */
  private FXMLLoader getLoader(String fxml) {
    FXMLLoader loader = new FXMLLoader();
    // Using Controller.class, rather than this.getClass(),
    // SpotBugs does not find a compile-time bug.
    loader.setLocation(Controller.class.getResource("views/" + fxml + ".fxml"));
    return loader;
  }

  /**
   * Switches scene in the stage.
   *
   * @param stage is the stage to show
   * @param root  is the root of the loaded FXML
   * @throws IOException if an error occurs when switching scenes.
   */
  private void switchScene(Stage stage, Parent root) throws IOException {
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Changes from the current view to a provided view.
   * The method loads the FXML and sets the new controller,
   * then switches stage.
   *
   * @param event      is the ActionEvent on button click.
   * @param controller is the presentation logic Controller.
   * @param fxml       is the String representation of the FXML filename.
   */
  protected void changeToView(ActionEvent event,
                              Controller controller,
                              String fxml) {
    FXMLLoader loader = getLoader(fxml);

    loader.setController(controller);

    Stage stage = getStage(event);

    try {
      switchScene(stage, loader.load());
    } catch (IOException e) {
      setFeedbackText("An error occurred when trying to go to " + fxml + ".");
    }
  }

  public RemoteCognitionAccess getRemoteCognitionAccess() {
    return remoteCognitionAccess;
  }

  protected void setFeedbackText(String value) {
    feedback.setText(value);
  }

  protected void setFeedbackStyle(String value) {
    feedback.setStyle(value);
  }
}
