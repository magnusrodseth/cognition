package json;

import com.google.gson.JsonIOException;
import core.Flashcard;
import core.Quiz;
import core.User;
import org.junit.jupiter.api.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

public class CognitionStorageTest {
  private static final char[] characters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
          'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  private CognitionStorage cognitionStorage;

  @BeforeEach
  void setUp() {
    try {
      cognitionStorage = new CognitionStorage("cognitionTest.json");
    } catch (IOException e) {
      fail();
    }
  }

  @AfterEach
  void tearDown() {
    clearStorage();
  }

  /**
   * Test passes if a user can be created. Test fails if an exception is thrown.
   */
  @Test
  @DisplayName("Can create user.")
  void canCreateUser() {
    createUser(new User("created-username", "created-password"));
  }

  /**
   * Test passes if users can be created. Test fails if an exception is thrown.
   */
  @Test
  @DisplayName("Can create users.")
  void canCreateUsers() {
    int NUMBER_OF_USERS = 10;

    for (int i = 0; i < NUMBER_OF_USERS; i++) {
      // Try to create a sample user
      try {
        createUser(new User("created-username-" + i, "created-password-" + i));
      } catch (JsonIOException e) {
        fail();
      }
    }
  }

  @Test
  @DisplayName("Can serialize nested objects.")
  void canSerializeNestedObjects() {
    // expected is used to validate the nested object that gets serialized
    String expected = getCorrectNestedObject();

    // Create sample data that is nested
    int NUMBER_OF_QUIZZES = 1;
    int NUMBER_OF_FLASHCARDS_PER_QUIZ = 2;

    // Create UUID from deterministic seed
    String seed = "seed-used-for-testing";
    String id = UUID.nameUUIDFromBytes(seed.getBytes()).toString();

    // Create sample user
    User user = new User("username", "password");

    for (int i = 0; i < NUMBER_OF_QUIZZES; i++) {
      // Manipulate quizId to ensure that not all IDs are equal
      String quizId = id.replace(id.charAt(id.length() - 1), 'q');
      quizId = quizId.replace(quizId.charAt(quizId.length() - 2), characters[i % characters.length]);

      // Create sample quiz
      Quiz quiz = new Quiz(quizId, "quiz-" + i, "description-" + i);

      for (int j = 0; j < NUMBER_OF_FLASHCARDS_PER_QUIZ; j++) {
        // Manipulate flashcardId to ensure that not all IDs are equal
        String flashcardId = id.replace(id.charAt(id.length() - 1), 'f');
        flashcardId = flashcardId.replace(flashcardId.charAt(flashcardId.length() - 2),
                characters[j % characters.length]);

        // Create sample flashcard
        Flashcard flashcard = new Flashcard(flashcardId, "front-" + j, "answer-" + j);

        quiz.addFlashcard(flashcard);
      }

      user.addQuiz(quiz);
    }

    // Save current state of user
    createUser(user);

    // Read content of user storage as pure String
    String actual = "";
    try {
      actual = Files.readString(cognitionStorage.getStoragePath());
    } catch (IOException e) {
      fail();
    }

    Assertions.assertEquals(expected, actual);
  }

  /**
   * A helper method when testing {@link CognitionStorageTest#canSerializeNestedObjects()}.
   * The correct String is encapsulated in a method in order to hide
   * implementation detail and make the
   * developer workflow less cluttered. All values in the JSON data are
   * deterministic.
   *
   * @return a String representation of the correct nested objects, with all
   * whitespace removed.
   */
  private String getCorrectNestedObject() {
    return """
            [
                   {
                       "quizzes": [
                           {
                               "uuid": "4efd2ea4-a598-3ec5-bc09-09ffc0q625aq",
                               "name": "quiz-0",
                               "description": "description-0",
                               "flashcards": [
                                   {
                                       "uuid": "4efd2ea4-a598-3ec5-bc09-09ffc0f625af",
                                       "front": "front-0",
                                       "answer": "answer-0"
                                   },
                                   {
                                       "uuid": "4efd2ea4-b598-3ec5-bc09-09ffc0f625bf",
                                       "front": "front-1",
                                       "answer": "answer-1"
                                   }
                               ]
                           }
                       ],
                       "username": "username",
                       "password": "password"
                   }
               ]""".replaceAll("\\s+", "");
  }

  @Test
  @DisplayName("Can read user by username.")
  void canReadUserByUsername() {
    String username = "test-username";
    String password = "test-password";
    User user = new User(username, password);

    createUser(user);

    try {
      User parsedUser = cognitionStorage.read(username);
      Assertions.assertEquals(user, parsedUser);
    } catch (IOException e) {
      fail();
    }
  }

  /**
   * An internal method used during testing due to many tests requiring the
   * creation of a test user.
   *
   * @param user is the user to create
   */
  private void createUser(User user) {
    // Try to create a sample user
    try {
      cognitionStorage.create(user);
    } catch (JsonIOException | IOException e) {
      fail();
    }
  }

  /**
   * Test passes if a sample user can be read from local storage. Test fails if an
   * exception is thrown.
   */
  @Test
  @DisplayName("Can read user.")
  void canReadUser() {
    // The identifier is used to determine if the read user is the correct one
    String identifier = "read-username";

    // Create sample user with given identifier
    createUser(new User("read-username", "read-password"));

    User user = new User();

    try {
      user = cognitionStorage.read(identifier);
    } catch (IOException e) {
      fail();
    }

    Assertions.assertNotNull(user);
  }

  /**
   * Test passes if a sample user can be updated based on identifier. Test fails
   * if an exception is thrown.
   */
  @Test
  @DisplayName("Can update user.")
  void canUpdateUser() {
    // The identifier is used to determine if the updated user is the correct one
    String identifier = "username";

    // Create sample user with given identifier
    createUser(new User(identifier, "base-password"));

    try {
      cognitionStorage.update(identifier, new User("new-username", "new-password"));
    } catch (IOException e) {
      fail();
    }
  }

  @Test
  @DisplayName("When updating user: If local storage is empty, then throw.")
  void whenUpdatingUser_ifLocalStorageIsEmpty_thenThrow() {
    String username = "valid-username";
    User user = new User(username, "valid-password");

    Assertions.assertThrows(
            NoSuchElementException.class,
            () -> cognitionStorage.update(username, user)
    );
  }

  @Test
  @DisplayName("When updating user: If user does not exist, then throw.")
  void whenUpdatingUser_ifUserDoesNotExist_thenThrow() {

    User userToCreate = new User("user-to-store", "user-to-store-password");
    String username = "non-existing-username";

    // Setup test
    try {
      cognitionStorage.create(userToCreate);
    } catch (IOException e) {
      fail();
    }

    // Assertions
    Assertions.assertThrows(
            NoSuchElementException.class,
            () -> cognitionStorage.update(username, new User(username, "non-existing-password"))
    );
  }

  @Test
  @DisplayName("Can delete user.")
  void canDeleteUser() {
    // The identifier is used to determine if the updated user is the correct one
    String username = "delete-username";

    // Create sample user with given identifier
    createUser(new User(username, "delete-password"));

    try {
      cognitionStorage.delete(username);
    } catch (IOException e) {
      fail();
    }

    List<User> users = new ArrayList<>();

    try {
      users = cognitionStorage.readUsers();
    } catch (IOException e) {
      fail();
    }

    // Check if User object with given identifier really was deleted
    for (User user : users) {
      // There still exists a User object with the randomly generated identifier.
      if (user.getUsername().equals(username)) {
        fail();
      }
    }
  }

  @Test
  @DisplayName("No users returns null.")
  void noUsersStoredReturnsEmptyList() {
    List<User> users = new ArrayList<>();

    // Clear user storage before validating the return type when user storage is
    // empty.
    clearStorage();

    try {
      users = cognitionStorage.readUsers();
    } catch (IOException e) {
      fail();
    }

    // Because we have not added any users to the local storage, the returned value
    // of
    // userStorage.readUsers() should be an empty list.
    Assertions.assertEquals(users.size(), 0);
  }

  /**
   * Empties the JSON data in file at the storage path. Used before validating the
   * return type when user storage is empty.
   */
  private void clearStorage() {
    try (FileWriter writer = new FileWriter(String.valueOf(cognitionStorage.getStoragePath()))) {
      writer.write("");
    } catch (IOException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Has correct storage path.")
  void hasCorrectStoragePath() {
    Assertions.assertEquals(
            Paths.get(System.getProperty("user.home"), "it1901-gr2103", "cognition", "cognitionTest.json"),
            cognitionStorage.getStoragePath());
  }
}
