package ui;

import core.CompactQuiz;
import core.Flashcard;
import core.Quiz;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.util.stream.Collectors;

import static core.tools.Tools.createUuid;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static ui.TestFxHelper.waitForFxEvents;

public class MyQuizzesTest extends ApplicationTest {
  private Scene scene;
  private MyQuizzesController myQuizzesController;
  private final RemoteCognitionAccess mockRemoteCognitionAccess = Mockito.mock(RemoteCognitionAccess.class);
  private User loggedInUser;

  private final String validUsername = "valid-username";
  private final String validPassword = "valid-password";

  @AfterEach
  void tearDown() {
    TestFxHelper.clearTestStorage();
  }

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = getLoader("MyQuizzes");

    this.loggedInUser = new User(validUsername, validPassword);

    // create some test data. 10 quizzes
    for (int i = 0; i < 10; i++) {
      Quiz quiz = new Quiz(createUuid(), "Test quiz "
          + i, "Test description " + i);
      for (int j = 0; j < 10; j++) {
        Flashcard fc = new Flashcard(createUuid(), "Front"
            + j, "Back" + j);
        quiz.addFlashcard(fc);
      }
      loggedInUser.addQuiz(quiz);
    }

    Mockito.when(mockRemoteCognitionAccess.getQuizTitlesByUsername(loggedInUser.getUsername()))
        .thenReturn(loggedInUser
                .getQuizzes()
                .stream()
                .map(quiz -> new CompactQuiz(quiz.getUuid(), quiz.getName()))
                .collect(Collectors.toList()
                )
        );

    myQuizzesController = new MyQuizzesController(loggedInUser, mockRemoteCognitionAccess);

    loader.setController(myQuizzesController);

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
    Assertions.assertNotNull(myQuizzesController);
  }

  @Test
  @DisplayName("Can display quizzes")
  public void canDisplayQuizzes() {
    ListView<CompactQuiz> listView = findListView();

    if (listView.getItems().size() == 0) {
      fail("No items in list");
    }

    waitForFxEvents();

    // assert listview has all quizzes that user object has
    for (int i = 0; i < listView.getItems().size(); i++) {
      Assertions.assertEquals(
          loggedInUser.getQuizzes().get(i).getUuid(),
          listView.getItems().get(i).getUuid()
      );
    }
  }

  @Test
  @DisplayName("Can delete quiz")
  void canDeleteQuiz() {
    //try to delete quiz without selecting one, should not work and give feedback to user
    clickOn("#deleteQuizButton");
    waitForFxEvents();

    // Validate that user got correct feedback in UI
    Assertions.assertEquals(
        "No selected quiz",
        myQuizzesController.getFeedbackErrorMessage()
    );

    // click on item in list -> click on delete button
    clickOn("#quizzesListView");
    waitForFxEvents();


    // mock getting back a quiz from local storage
    try {
      Mockito.when(mockRemoteCognitionAccess.getQuizByUuid(notNull()))
          .thenReturn(loggedInUser.getQuizzes().get(0));
    } catch (IOException | InterruptedException e) {
      fail();
    }

    // delete selected quiz
    clickOn("#deleteQuizButton");
    waitForFxEvents();

    ListView<CompactQuiz> listView = findListView();

    //  -> make sure number of items are 10 - 1 = 9
    Assertions.assertEquals(9, listView.getItems().size());
    waitForFxEvents();
  }

  @Test
  @DisplayName("Can start quiz")
  public void canStartQuiz() {
    // click on start quiz without selecting quiz first
    clickOn("#startQuizButton");
    waitForFxEvents();

    // cannot start quiz without selecting one first
    Assertions.assertEquals(
        "No selected quiz",
        myQuizzesController.getFeedbackErrorMessage()
    );
    waitForFxEvents();

    // select a quiz
    clickOn("#quizzesListView");
    waitForFxEvents();

    // mock finding the selected () from local storage
    try {
      Mockito.when(mockRemoteCognitionAccess.getQuizByUuid(notNull()))
          .thenReturn(loggedInUser.getQuizzes().get(0));
    } catch (IOException | InterruptedException e) {
      fail();
    }

    // start quiz
    clickOn("#startQuizButton");
    waitForFxEvents();

    // make sure right view is loaded
    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("ViewQuiz"));
    waitForFxEvents();
  }

  @Test
  @DisplayName("Can update quiz")
  public void canUpdateQuiz() {
    // click on update without selecting quiz
    clickOn("#updateQuizButton");
    waitForFxEvents();

    // cannot update quiz without selecting one first
    Assertions.assertEquals(
        "No selected quiz",
        myQuizzesController.getFeedbackErrorMessage()
    );
    waitForFxEvents();

    // click on a quiz
    clickOn("#quizzesListView");
    waitForFxEvents();

    // try to update quiz
    clickOn("#updateQuizButton");
    waitForFxEvents();

    // assert that right view is loaded
    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Quiz"));
    waitForFxEvents();
  }

  @Test
  @DisplayName("Can navigate to Dashboard")
  void canNavigateToDashboard() {
    clickOn("#goToDashboardButton");
    waitForFxEvents();

    FxAssert.verifyThat("#signOutButton", LabeledMatchers.hasText("Sign out"));
    waitForFxEvents();
  }

  @Test
  @DisplayName("Can navigate to Create Quiz")
  void canNavigateToCreateQuiz() {
    clickOn("#createQuizButton");
    waitForFxEvents();

    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Quiz"));
    waitForFxEvents();
  }

  @Test
  @DisplayName("Can log out")
  void canLogOut() {
    clickOn("#signOutButton");
    waitForFxEvents();

    FxAssert.verifyThat("#pageId", LabeledMatchers.hasText("Login"));
    waitForFxEvents();
  }

  private ListView<CompactQuiz> findListView() {
    return lookup("#quizzesListView").query();
  }
}