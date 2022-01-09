package ui;

import core.User;
import core.tools.Tools;
import javafx.fxml.FXML;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * DashboardController handles the presentation logic for the Dashboard view.
 */
public class DashboardController extends LoggedInController {

  @FXML
  private Labeled heading;

  @FXML
  private Text cognitionDescription;

  public DashboardController(User user, RemoteCognitionAccess remoteCognitionAccess) {
    super(user, remoteCognitionAccess);
  }


  /**
   * Adds application description to dashboard.
   */
  @FXML
  public void initialize() {
    heading.setText("Welcome, " + Tools.capitalize(getUser().getUsername()));

    cognitionDescription.setText("Cognition is an interactive game-based "
            + "learning tool used to study information. This tool uses interactive "
            + "digital flashcards. Teachers and Students can easily upload information "
            + "to create study sets by adding questions and definitions. This tool is "
            + "easy to navigate and provides a fun and interactive playground to the "
            + "learners as they engage with the application.");
    
    cognitionDescription.setFont(Font.font("Avenir book", 24));

  }
}
