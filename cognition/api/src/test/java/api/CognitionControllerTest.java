package api;

import core.CompactQuiz;
import core.Quiz;
import core.User;
import json.CognitionStorage;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.NoSuchElementException;

import static core.tools.Tools.createUuid;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * CognitionControllerTest validates that the REST controller behaves like
 * expected. This class is treated almost as a unit test, given the manner the
 * controller is tested.
 */
public class CognitionControllerTest {
  private CognitionController cognitionController;
  private final String validUsername = "valid-username";
  private CognitionStorage cognitionStorage;

  @BeforeEach
  void setUp() {
    try {
      // Because we test the REST controller in isolation, a new instance is made and
      // its methods are tested like a unit test
      cognitionController = new CognitionController();
      cognitionStorage = new CognitionStorage("cognitionTest.json");
      cognitionController.setCognitionStorage(cognitionStorage);

      // Create sample user for tests
      User user = new User(validUsername, "test-password");
      cognitionController.createUser(user);
    } catch (IOException e) {
      fail();
    }
  }

  @AfterEach
  void tearDown() {
    clearStorage();
  }

  @Test
  @DisplayName("Can get users.")
  void canGetUsers() {
    try {
      List<User> users = cognitionController.getUsers();
      Assertions.assertNotNull(users);
    } catch (UserNotFoundException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Can get user by username.")
  void canGetUserByUsername() {
    try {
      User currentUser = cognitionController.getUserByUsername(validUsername);
      Assertions.assertNotNull(currentUser);
    } catch (UserNotFoundException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Can create user.")
  void canCreateUser() {
    String username = "created-user";
    String password = "created-password";

    User user = new User(username, password);

    try {
      cognitionController.createUser(user);
    } catch (StorageException | IdentifierAlreadyInUseException e) {
      fail();
    }

    // Assert that user was created
    User parsedUser = cognitionController.getUserByUsername(username);
    Assertions.assertEquals(username, parsedUser.getUsername());
  }

  @Test
  @DisplayName("If user already exists, then throw.")
  void ifUserAlreadyExistsThenThrow() {
    String username = "created-user";
    String password = "created-password";

    User user = new User(username, password);

    try {
      cognitionController.createUser(user);
    } catch (StorageException | IdentifierAlreadyInUseException e) {
      fail();
    }

    // Create duplicate user
    try {
      Assertions.assertThrows(IdentifierAlreadyInUseException.class, () -> cognitionController.createUser(user));
    } catch (StorageException | IdentifierAlreadyInUseException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Can get quiz titles by username.")
  void canGetQuizTitlesByUsername() {
    // Create sample quiz
    String uuid = createUuid();
    Quiz quiz = new Quiz(uuid, "quiz-name", "quiz-description");

    // Update state in persistent storage
    User user = cognitionController.getUserByUsername(validUsername);
    user.addQuiz(quiz);
    cognitionController.updateUser(user);

    // Read sample quiz
    List<CompactQuiz> quizTitles = cognitionController.getQuizTitlesByUsername(validUsername);
    CompactQuiz compactQuiz = quizTitles.get(0);

    Assertions.assertEquals(quiz.getName(), compactQuiz.getName());
  }

  @Test
  @DisplayName("Can update user.")
  void canUpdateUser() {
    try {
      // Get user
      User user = cognitionController.getUserByUsername(validUsername);

      // Update user
      Quiz quiz = new Quiz(createUuid(), "quiz-name", "quiz-description");
      user.addQuiz(quiz);

      // Update user
      cognitionController.updateUser(user);

      User parsedUser = cognitionController.getUserByUsername(validUsername);

      // Assert that we added quiz
      boolean addedQuiz = parsedUser.getQuizzes().size() == 1;
      Assertions.assertTrue(addedQuiz);
    } catch (UserNotFoundException e) {

      fail();
    }
  }

  @Test
  @DisplayName("When updating user: If no user exist, then throw.")
  void whenUpdatingUser_ifNoUserExist_thenThrow() {
    String nonExistingUuid = createUuid();
    User user = new User("nonUser", "hheloas");
    Assertions.assertThrows(
             UserNotFoundException.class,
            () -> cognitionController.updateUser(user)
    );
  }

  @Test
  @DisplayName("Can delete user.")
  void canDeleteUser() {
    try {
      // Get user
      User user = cognitionController.getUserByUsername(validUsername);
      Assertions.assertNotNull(user);

      // Delete user
      cognitionController.deleteUser(user.getUsername());

      Assertions.assertThrows(
          UserNotFoundException.class,
          () -> cognitionController.getUserByUsername(validUsername)
      );
    } catch (UserNotFoundException e) {
      fail();
    }
  }

  @Test
  @DisplayName("When deleting user: If no such user exist, then throw.")
  void whenDeletingUser_ifNoUserExists_thenThrow() {
    String invalidUsername = "thisUsernameDoesNotExist";

    Assertions.assertThrows(
            UserNotFoundException.class,
            () -> cognitionController.deleteUser(invalidUsername)
    );
  }

  @Test
  @DisplayName("Can get quiz by UUID.")
  void canGetQuizByUuid() {
    // Create sample quiz
    String uuid = createUuid();
    Quiz quiz = new Quiz(uuid, "quiz-name", "quiz-description");

    // Update state in persistent storage
    User user = cognitionController.getUserByUsername(validUsername);
    user.addQuiz(quiz);
    cognitionController.updateUser(user);

    // Read sample quiz
    Quiz parsedQuiz = cognitionController.getQuizByUuid(uuid);

    // Assert that we could read quiz
    Assertions.assertNotNull(parsedQuiz);
  }

  @Test
  @DisplayName("When getting quiz by UUID: If no quizzes exist, then throw.")
  void whenGettingQuizByUuid_ifNoQuizzesExist_thenThrow() {
    String nonExistingUuid = createUuid();

    Assertions.assertThrows(QuizNotFoundException.class, () -> cognitionController.getQuizByUuid(nonExistingUuid));
  }

  @Test
  @DisplayName("Can get quizzes by username.")
  void canGetQuizzesByUsername() {
    try {
      // Get user
      User user = cognitionController.getUserByUsername(validUsername);

      // Add quiz to user
      user.addQuiz(new Quiz(createUuid(), "quiz-name", "quiz-description"));

      // Update state
      cognitionController.updateUser(user);

      // Get user's quizzes
      List<Quiz> quizzes = cognitionController.getQuizzesByUsername(user.getUsername());

      // Assertion
      Assertions.assertNotNull(quizzes);
    } catch (UserNotFoundException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Can update quiz.")
  void canUpdateQuiz() {
    // Get user
    User user = cognitionController.getUserByUsername(validUsername);

    // Add quiz to user
    Quiz quiz = new Quiz(createUuid(), "quiz-name", "quiz-description");
    user.addQuiz(quiz);

    // Update state
    cognitionController.updateUser(user);

    // Update state of quiz
    String expectedName = "new-name";
    String expectedDescription = "new-description";
    quiz.setName(expectedName);
    quiz.setDescription(expectedDescription);

    // Update using controller
    cognitionController.updateQuizByUuid(quiz);

    // Inspect the user corresponding to the quiz, and verify that the quiz was
    // updated
    user = cognitionController.getUserByUsername(validUsername);
    Quiz updatedQuiz = user.getQuizzes().get(0);

    // Assertions
    Assertions.assertEquals(expectedName, updatedQuiz.getName());
    Assertions.assertEquals(expectedDescription, updatedQuiz.getDescription());
  }



  @Test
  @DisplayName("If user to update is null, then throw.")
  void ifUserToUpdateIsNullThenThrow() {
    Quiz quizNotBelongingToUser = new Quiz(createUuid(), "test-name", "test-description");

    Assertions.assertThrows(UserNotFoundException.class,
        () -> cognitionController.updateQuizByUuid(quizNotBelongingToUser));
  }



  @Test
  @DisplayName("Can delete quiz.")
  void canDeleteQuiz() {
    User user = cognitionController.getUserByUsername(validUsername);

    // Add quiz to user
    String quizUuid = createUuid();
    Quiz quiz = new Quiz(quizUuid, "quiz-name", "quiz-description");
    user.addQuiz(quiz);

    // Update state
    cognitionController.updateUser(user);

    // Delete quiz
    cognitionController.deleteQuizByUuid(quizUuid);

    // Verify state
    user = cognitionController.getUserByUsername(validUsername);
    boolean quizWasDeleted = user.getQuizzes().size() == 0;
    Assertions.assertTrue(quizWasDeleted);
  }

  @Test
  @DisplayName("If quiz to delete is null, then throw.")
  void ifQuizToDeleteIsNullThenThrow() {
    String nonExistingUuid = createUuid();

    Assertions.assertThrows(QuizNotFoundException.class, () -> cognitionController.deleteQuizByUuid(nonExistingUuid));
  }

  @Test
  @DisplayName("Can create quiz.")
  void canCreateQuiz() {
    cognitionController.createQuiz(new Quiz(createUuid(), "new-name", "new-description"), validUsername);

    User user = cognitionController.getUserByUsername(validUsername);

    boolean quizWasCreated = user.getQuizzes().size() == 1;
    Assertions.assertTrue(quizWasCreated);
  }

  @Test
  @DisplayName("Invalid user throws when creating quiz.")
  void invalidUserThrowsWhenCreatingQuiz() {
    Quiz quiz = new Quiz(createUuid(), "new-name", "new-description");
    Assertions.assertThrows(UserNotFoundException.class,
        () -> cognitionController.createQuiz(quiz, "non-existing-username"));
  }

  @Test
  @DisplayName("Existing quiz UUID throws when creating quiz.")
  void existingQuizUuidThrowsWhenCreatingQuiz() {
    // Create sample quiz
    Quiz quiz = new Quiz(createUuid(), "new-name", "new-description");

    // Nothing should be thrown the first time the quiz is created
    cognitionController.createQuiz(quiz, validUsername);

    // When we find a duplicate UUID for a quiz, we throw
    Assertions.assertThrows(IdentifierAlreadyInUseException.class,
        () -> cognitionController.createQuiz(quiz, validUsername));
  }

  /**
   * Empties the JSON data in file at the storage path. Used before validating the
   * return type when user storage is empty.
   * <p>
   * This method directly accesses the local storage, as it makes no sense to have
   * an API endpoint for deleting all persistent data.
   */
  private void clearStorage() {
    try (FileWriter writer = new FileWriter(String.valueOf(cognitionStorage.getStoragePath()))) {
      writer.write("");
    } catch (IOException e) {
      fail();
    }
  }
}
