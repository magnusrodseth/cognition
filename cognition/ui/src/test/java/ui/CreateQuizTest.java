package ui;

import core.Flashcard;
import core.Quiz;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static core.tools.Tools.createUuid;
import static org.junit.jupiter.api.Assertions.fail;
import static ui.TestFxHelper.waitForFxEvents;

public class CreateQuizTest extends ApplicationTest {
  private final String validUsername = "valid-username";
  private final String validPassword = "valid-password";
  private Scene scene;
  private QuizController quizController;

  // Mock RemoteCognitionAccess in order to test the client application in isolation
  private final RemoteCognitionAccess mockRemoteCognitionAccess = Mockito.mock(RemoteCognitionAccess.class);

  private TestFxHelper helper = new TestFxHelper();

  private User loggedInUser;

  @AfterEach
  void tearDown() {
    TestFxHelper.clearTestStorage();
  }

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = getLoader("Quiz");

    // in the app there is no logical way for Create Quiz to be accessed without a
    // logged-in user. Thus, we create a fake user here to emulate it
    loggedInUser = new User(validUsername, validPassword);

    // create the controller with the mocked version of cognition access
    quizController = new QuizController(loggedInUser, mockRemoteCognitionAccess);

    // IMPORTANT: We do not set a quiz object here. Thus, we render the view like we
    // create a quiz.
    loader.setController(quizController);

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
  @DisplayName("Can create quiz.")
  void canCreateQuiz() {
    // Define input values
    String name = "name";
    String description = "description";
    String front = "front";
    String answer = "answer";

    // Add flashcard
    clickOn(helper.findTextField(node -> true, "#front-input", 0))
            .write(front);

    clickOn(helper.findTextField(node -> true, "#answer-input", 0))
            .write(answer);

    // Create quiz
    verifyInputData(name, description, false);

    try {
      // Mock response
      User loggedInUserWithQuizzes = loggedInUser;
      Quiz quiz = new Quiz(createUuid(), name, description);
      quiz.addFlashcard(new Flashcard(createUuid(), front, answer));

      loggedInUserWithQuizzes.addQuiz(quiz);

      Mockito.when(mockRemoteCognitionAccess.read(validUsername))
              .thenReturn(loggedInUserWithQuizzes);
    } catch (InterruptedException | IOException e) {
      fail();
    }

    List<Quiz> quizzes = new ArrayList<>();

    try {
      quizzes = mockRemoteCognitionAccess.read(validUsername).getQuizzes();
    } catch (IOException | InterruptedException e) {
      fail();
    }

    boolean quizWasStored = false;

    // Note that we do not compare UUID in this test,
    // as a new UUID is generated on button click in the UI regardless.
    for (Quiz quiz : quizzes) {
      boolean quizMatches = quiz.getName().equals(name) && quiz.getDescription().equals(description);

      if (quizMatches) {
        for (Flashcard flashcard : quiz.getFlashcards()) {
          boolean flashcardMatches = flashcard.getFront().equals(front) && flashcard.getAnswer().equals(answer);

          if (flashcardMatches) {
            quizWasStored = true;
            break;
          }
        }
      }
    }
    
    Assertions.assertTrue(quizWasStored);
  }

  @Test
  @DisplayName("Cannot create empty quiz.")
  void cannotCreateEmpty() {
    String name = "name";
    String description = "description";

    clickOn(helper.findTextField(node -> true, "#name", 0)).write(name);
    clickOn(helper.findTextField(node -> true, "#description", 0)).write(description);

    waitForFxEvents();
    clickOn("#remove-flashcard-button");

    waitForFxEvents();
    clickOn("#storeQuizButton");

    waitForFxEvents();
    FxAssert.verifyThat("#feedback", LabeledMatchers.hasText("There has to be at least one flashcard"));
  }

  /**
   * A helper method for verifying input data.
   *
   * @param name           is the provided name in the input form
   * @param description    is the provided description in the input form
   * @param isErrorMessage determines if the feedback is an error message or not.
   */
  private void verifyInputData(String name, String description, boolean isErrorMessage) {
    // Input data into UI
    clickOn(helper.findTextField(node -> true, "#name", 0)).write(name);
    clickOn(helper.findTextField(node -> true, "#description", 0)).write(description);

    waitForFxEvents();

    clickOn("#storeQuizButton");

    waitForFxEvents();
    String feedback = isErrorMessage ? quizController.getFeedbackErrorMessage()
            : quizController.getFeedbackSuccessMessage();

    // Validate that user got correct feedback in UI
    FxAssert.verifyThat("#feedback", LabeledMatchers.hasText(feedback));
    waitForFxEvents();
  }
}
