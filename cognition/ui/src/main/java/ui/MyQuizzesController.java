package ui;

import core.CompactQuiz;
import core.Quiz;
import core.User;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * MyQuizzesController is responsible for handling the
 * presentation logic in the "My Quizzes" view.
 */
public class MyQuizzesController extends LoggedInController {

  @FXML
  private ListView<CompactQuiz> quizzesListView;

  @FXML
  private TextField searchInput;

  /**
   * The currently selected compact quiz.
   * See documentation of {@link core.CompactQuiz} for more information.
   */
  private CompactQuiz selectedCompactQuiz;

  /**
   * The currently selected quiz.
   * See documentation {@link core.Quiz} for more information.
   */
  private Quiz selectedQuiz;

  private String feedbackErrorMessage;

  public MyQuizzesController(User user, RemoteCognitionAccess remoteCognitionAccess) {
    super(user, remoteCognitionAccess);
  }

  /**
   * Renders the initial view.
   */
  @FXML
  private void initialize() {
    // Initially render ListView
    quizzesListView.setItems(filterQuizzes(""));
    setupListView();

    // Add onChange event handler for search input
    searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
      quizzesListView.setItems(filterQuizzes(newValue));
    });

    // Update selected quiz by what quiz is selected in the ui
    quizzesListView.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> this.selectedCompactQuiz = newValue);
  }

  /**
   * Sets up list view cell factory so ListView properly works using CompactQuiz as type.
   */
  private void setupListView() {
    quizzesListView.setCellFactory(quizListView -> new ListCell<>() {
      @Override
      protected void updateItem(CompactQuiz item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setText(null);
          setGraphic(null);
        } else {
          setText(item.getName());
        }
      }
    });
  }

  /**
   * Compares the users quizzes with an input and returns the matching quizzes. A comparison
   * functions by checking if a name contains the given input.
   *
   * @param input the string quizzes are matched up against
   * @return an ObservableList with all the matching quizzes
   */
  private ObservableList<CompactQuiz> filterQuizzes(String input) {
    Stream<CompactQuiz> stream = null;

    // Fetch compact quizzes
    try {
      stream = getRemoteCognitionAccess()
              .getQuizTitlesByUsername(getUser().getUsername()).stream();
    } catch (IOException | InterruptedException e) {
      feedbackErrorMessage = "An error occurred when trying to get your quizzes.";
      setFeedbackText(feedbackErrorMessage);
      return null;
    }

    if (!input.equals("")) {
      stream = stream.filter(quiz -> quiz.getName().toLowerCase().contains(input.toLowerCase()));
    }

    return FXCollections.observableArrayList(stream.collect(Collectors.toList()));
  }

  /**
   * Triggered when user clicks to start quiz.
   *
   * @param event is the ActionEvent on button click.
   */
  @FXML
  private void handleStartQuiz(ActionEvent event) {
    if (quizIsNotSelected()) {
      return;
    }

    // Fetch selected quiz based on selected CompactQuiz
    // This is done in order to not fetch an unnecessary amount of data at once
    try {
      selectedQuiz = getRemoteCognitionAccess().getQuizByUuid(selectedCompactQuiz.getUuid());
    } catch (IOException | InterruptedException e) {
      feedbackErrorMessage = "An error occurred when trying to start the quiz.";
      setFeedbackText(feedbackErrorMessage);
      return;
    }

    // Set state in controller
    ViewQuizController viewQuizController = new ViewQuizController(
            getUser(),
            selectedQuiz,
            getRemoteCognitionAccess()
    );

    changeToView(event, viewQuizController, "ViewQuiz");
  }

  /**
   * Triggered when there is a quiz to be updated.
   *
   * @param event is the ActionEvent on button click.
   */
  @FXML
  private void handleUpdateQuiz(ActionEvent event) {
    if (quizIsNotSelected()) {
      return;
    }

    // Fetch selected quiz based on selected CompactQuiz
    // This is done in order to not fetch an unnecessary amount of data at once
    try {
      selectedQuiz = getRemoteCognitionAccess().getQuizByUuid(this.selectedCompactQuiz.getUuid());
    } catch (IOException | InterruptedException e) {
      feedbackErrorMessage = "An error occurred when trying to start the quiz.";
      setFeedbackText(feedbackErrorMessage);
      return;
    }

    // Set state in controller
    QuizController quizController = new QuizController(getUser(), getRemoteCognitionAccess());
    quizController.setQuizBeingUpdated(selectedQuiz);

    changeToView(event, quizController, "Quiz");
  }

  private boolean quizIsNotSelected() {
    if (this.selectedCompactQuiz == null) {
      feedbackErrorMessage = "No selected quiz";
      setFeedbackText(feedbackErrorMessage);
      return true;
    }
    return false;
  }

  /**
   * Gets triggered when a quiz is to be deleted.
   *
   * @param actionEvent is the ActionEvent on button click.
   */
  @FXML
  private void handleDeleteQuiz(ActionEvent actionEvent) {
    if (quizIsNotSelected()) {
      return;
    }

    // Fetch selected quiz based on selected CompactQuiz
    // This is done in order to not fetch an unnecessary amount of data at once
    try {
      selectedQuiz = getRemoteCognitionAccess().getQuizByUuid(this.selectedCompactQuiz.getUuid());
    } catch (IOException | InterruptedException e) {
      feedbackErrorMessage = "An error occurred when trying to start the quiz.";
      setFeedbackText(feedbackErrorMessage);
      return;
    }

    // Update state of models
    getUser().removeQuiz(selectedQuiz);

    // Update state in UI
    this.selectedCompactQuiz = null;
    this.selectedQuiz = null;
    quizzesListView.getItems().remove(quizzesListView.getSelectionModel().getSelectedItem());
    ObservableList<CompactQuiz> quizzes = quizzesListView.getItems();
    quizzesListView.setItems(quizzes);

    // Update state in persistent storage
    try {
      getRemoteCognitionAccess().update(getUser());
    } catch (IOException | InterruptedException e) {
      setFeedbackText("An error occurred when trying to delete selected quiz.");
    }
  }

  public String getFeedbackErrorMessage() {
    return feedbackErrorMessage;
  }
}
