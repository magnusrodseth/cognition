package core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static core.tools.Tools.createUuid;

public class UserTest {
  private final String invalidUsername = "a";
  private final String invalidPassword = "b";
  private final String validUsername = "valid-username";
  private final String validPassword = "valid-password";
  private User user;

  @BeforeEach
  void setUp() {
    user = new User(validUsername, validPassword);
  }

  @Test
  @DisplayName("Can initialize user.")
  void canInitializeUser() {
    User emptyUser = new User();
    User user = new User("username", "password");

    Assertions.assertNotNull(emptyUser);
    Assertions.assertNotNull(user);
  }

  @Test
  @DisplayName("No duplicate quizzes.")
  void noDuplicateQuizzes() {
    Quiz quiz = new Quiz(createUuid(), "name", "description");

    // Add quiz for the first time
    user.addQuiz(quiz);

    // Add quiz for the second time. This time, it should not be added.
    user.addQuiz(quiz);

    // Flashcards should only be added once
    Assertions.assertEquals(1, user.getQuizzes().size());
  }

  @Test
  @DisplayName("Can add quiz.")
  void canAddQuiz() {
    Assertions.assertThrows(NullPointerException.class, () -> user.addQuiz(null));

    // Quiz should not have been added
    Assertions.assertEquals(0, user.getQuizzes().size());

    user.addQuiz(new Quiz(createUuid(), "name", "description"));

    // Quiz should have been added
    Assertions.assertEquals(1, user.getQuizzes().size());
  }

  @Test
  @DisplayName("Can remove quiz.")
  void canRemoveQuiz() {
    Quiz nonExistingQuiz = new Quiz(createUuid(), "non-existing-name", "non-existing-description");
    Quiz existingQuiz = new Quiz(createUuid(), "existing-name", "existing-description");

    // Add flashcard and verify number of flashcards
    user.addQuiz(existingQuiz);
    Assertions.assertEquals(1, user.getQuizzes().size());

    // Remove non-existing flashcard and verify that size is unchanged
    user.removeQuiz(nonExistingQuiz);
    Assertions.assertEquals(1, user.getQuizzes().size());

    // Remove existing flashcard, and verify that size decrease by 1
    user.removeQuiz(existingQuiz);
    Assertions.assertEquals(0, user.getQuizzes().size());
  }

  @Test
  @DisplayName("Can set username.")
  void canSetUsername() {
    String currentUsername = user.getUsername();

    Assertions.assertThrows(IllegalArgumentException.class, () -> user.setUsername(""));

    // Verify that the name is unchanged when provided with an empty string
    Assertions.assertEquals(currentUsername, user.getUsername());

    String newName = "new-name";
    user.setUsername(newName);

    // Verify that the name is changed when provided with a new name
    Assertions.assertEquals(user.getUsername(), newName);
  }

  @Test
  @DisplayName("Can set password.")
  void canSetPassword() {
    String currentPassword = user.getPassword();

    Assertions.assertThrows(IllegalArgumentException.class, () -> user.setPassword(""));

    // Verify that the password is unchanged when provided with an empty string
    Assertions.assertEquals(currentPassword, user.getPassword());

    String newPassword = "new-password";
    user.setPassword(newPassword);

    // Verify that the name is changed when provided with a new name
    Assertions.assertEquals(user.getPassword(), newPassword);
  }

  @Test
  @DisplayName("Illegal User throws.")
  void illegalUserThrows() {
    Assertions.assertThrows(IllegalArgumentException.class,
            () -> new User(invalidUsername, validPassword));

    Assertions.assertThrows(IllegalArgumentException.class,
            () -> new User(validUsername, invalidPassword));
  }

  @Test
  @DisplayName("Can compare with another User object.")
  void canCompareWithAnotherUserObject() {
    User firstUser = new User("first-username", "first-password");
    User secondUser = new User("second-username", "second-password");

    Assertions.assertEquals(firstUser, firstUser);
    Assertions.assertNotEquals(firstUser, secondUser);
    Assertions.assertNotEquals(firstUser, null);
  }

  @Test
  @DisplayName("Can update quiz.")
  void canUpdateQuiz() {
    String updatedName = "updated-name";

    User user = new User("username", "password");
    Quiz quizToUpdate = new Quiz(UUID.randomUUID().toString(), "name", "description");

    user.addQuiz(quizToUpdate);

    // Update quiz with new state
    quizToUpdate.setName(updatedName);

    user.updateQuiz(quizToUpdate);

    boolean quizWasUpdated = false;

    // Find quiz and validate that the same quiz was updated
    for (Quiz quiz : user.getQuizzes()) {
      if (quiz.getUuid().equals(quizToUpdate.getUuid()) && quiz.getName().equals(updatedName)
              && quiz.getDescription().equals(quizToUpdate.getDescription())) {
        quizWasUpdated = true;
        break;
      }
    }

    Assertions.assertTrue(quizWasUpdated);
  }
}
