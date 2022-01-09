package ui;

import core.Flashcard;
import core.Quiz;
import core.User;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * ViewQuizController is responsible for the presentation logic in the ViewQuiz view.
 */
public class ViewQuizController extends LoggedInController {

  private Quiz quiz;
  private List<Flashcard> flashcards;
  private int flashcardIndex = 0;
  private int correctAnswerCount = 0;
  private boolean hasFailedFlashcard = false;

  @FXML
  private TextField answerInput;

  @FXML
  private Text heading;

  @FXML
  private Text flashcardText;

  @FXML
  private Text description;

  @FXML
  private Text answerText;

  @FXML
  private Button showAnswer;

  @FXML
  private Button submitAnswer;

  @FXML
  private Line answerInputLine;

  @FXML
  private Text answerInputText;

  @FXML
  private Label feedback;

  @FXML
  private Button restartButton;

  @FXML
  private Button goToMyQuizzesButton;

  public ViewQuizController(User user, Quiz quiz, RemoteCognitionAccess remoteCognitionAccess) {
    super(user, remoteCognitionAccess);
    this.quiz = quiz;
  }

  @FXML
  protected void initialize() {
    flashcards = this.quiz.getFlashcards();

    heading.setText(quiz.getName());
    description.setText(quiz.getDescription());

    // Initialize the first flashcard
    flashcardText.setText(flashcards.get(flashcardIndex).getFront());
  }

  /**
   * Updates the current flashcard when the user is doing a quiz.
   */
  private void nextFlashcard() {
    flashcardIndex++;
    flashcardText.setText(flashcards.get(flashcardIndex).getFront());
    setAnswerText("");
  }

  /**
   * Triggered when an answer for a flashcard is provided. Gives feedback based on if the answer
   * was right or not, and increments the number of right answers accordingly.
   */
  @FXML
  private void checkAnswer() {
    String userAnswer = answerInput.getText().toLowerCase();
    String correctAnswer = flashcards.get(flashcardIndex).getAnswer().toLowerCase();

    if (answerInput.getText().equals("")) {
      handleEmptyAnswer();
    } else if (userAnswer.equals(correctAnswer)) {
      handleCorrectAnswer();
    } else {
      handleWrongAnswer(correctAnswer);
    }
  }

  /**
   * Updates UI based on an empty answer.
   */
  private void handleEmptyAnswer() {
    setFeedbackErrorMode(true);
    setFeedbackText("Please provide an answer.");
  }

  /**
   * Updates UI based on an incorrect answer.
   *
   * @param answer is the answer provided by the user.
   */
  private void handleWrongAnswer(String answer) {
    setFeedbackErrorMode(true);
    setFeedbackText("Incorrect! \n The correct answer was: " + answer + ".");
    hasFailedFlashcard = true;
    answerInput.setText("");
  }

  /**
   * Updates UI based on a correct answer.
   */
  private void handleCorrectAnswer() {
    if (!hasFailedFlashcard) {
      correctAnswerCount++;
    }

    if (flashcardIndex == flashcards.size() - 1) {
      handleFinishQuiz();
    } else {
      setFeedbackErrorMode(false);
      setFeedbackText("Correct answer!");
      answerInput.setText("");

      hasFailedFlashcard = false;
      nextFlashcard();
    }
  }

  /**
   * Updates UI when a user finishes quiz.
   */
  private void handleFinishQuiz() {
    setRunningQuizElementsVisibility(false);
    setFinishedQuizElementsVisibility(true);

    flashcardText.setText(
            new StringBuilder()
                    .append("End of quiz! ")
                    .append("You got ")
                    .append(correctAnswerCount)
                    .append(" right out of ")
                    .append(flashcards.size())
                    .append(" possible.")
                    .toString());
    setAnswerText("");
  }

  /**
   * Change visibility on given UI elements based on boolean input.
   *
   * @param bool is the boolean representing if the UI element should be visible or not.
   */
  private void setRunningQuizElementsVisibility(boolean bool) {
    showAnswer.setVisible(bool);
    answerInput.setVisible(bool);
    submitAnswer.setVisible(bool);
    answerInputLine.setVisible(bool);
    answerInputText.setVisible(bool);
    feedback.setVisible(bool);
  }

  /**
   * Change visibility on given UI elements based on boolean input.
   *
   * @param bool is the boolean representing if the UI element should be visible or not.
   */
  private void setFinishedQuizElementsVisibility(boolean bool) {
    goToMyQuizzesButton.setVisible(bool);
    restartButton.setVisible(bool);
  }

  @FXML
  private void goToMyQuizzes(ActionEvent event) {
    changeToView(event,
            new MyQuizzesController(getUser(), getRemoteCognitionAccess()), "MyQuizzes"
    );
  }

  /**
   * Restarts the quiz.
   *
   * @param event is the event on button click
   */
  @FXML
  private void restart(ActionEvent event) {
    this.correctAnswerCount = 0;
    this.flashcardIndex = -1;
    nextFlashcard();

    setRunningQuizElementsVisibility(true);
    setFinishedQuizElementsVisibility(false);

    setFeedbackText("");
    answerInput.setText("");
  }


  /**
   * Sets the styling on feedback in the frontend.
   *
   * @param mode is a boolean representation of the feedback mode.
   */
  private void setFeedbackErrorMode(boolean mode) {
    if (mode) {
      setFeedbackStyle("-fx-text-fill: red");
    } else {
      setFeedbackStyle("-fx-text-fill: green");
    }
  }

  /**
   * Displays the answer in the UI.
   */
  @FXML
  private void showAnswer() {
    String invalidText = "Don't give up before trying!";
    if (hasFailedFlashcard) {
      String correctAnswer = flashcards.get(flashcardIndex).getAnswer();
      if (answerText.getText().equals("") || answerText.getText().equals(invalidText)) {
        setAnswerText("The correct answer is: " + correctAnswer);
      } else {
        setAnswerText("");
      }
    } else {
      setAnswerText(invalidText);
    }
  }
  
  private void setAnswerText(String text) {
    this.answerText.setText(text);
  }
}
