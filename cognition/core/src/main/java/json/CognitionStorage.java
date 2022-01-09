package json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import core.User;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * CRUD (Create, Read, Update and Delete) operations for all local storage.
 */
public class CognitionStorage {

  private final Gson gson = new Gson();
  private Path storagePath;

  /**
   * Creates a file if it does not already exist with the filename given.
   *
   * @param filename is the name of the file to be created.
   * @throws IOException if an error occurs while directories for local storage. A
   *                     potential exception is handled in the frontend.
   */
  public CognitionStorage(String filename) throws IOException {
    Objects.requireNonNull(filename);

    setStoragePath(filename);

    // A potential exception is handled in the frontend.
    createDirectoryIfNotExists();
  }

  public CognitionStorage() throws IOException {
    this("cognition.json");
  }

  /**
   * Reads all users from storage and deserializes the JSON array to a list of
   * User objects.
   *
   * @return a List of User objects
   * @throws IOException if an error occurred when trying to read from the storage
   *                     file
   */
  public List<User> readUsers() throws IOException {
    if (isEmpty()) {
      return new ArrayList<>();
    }

    try {
      return getGson().fromJson(
          /* Initialize JsonReader */
          new JsonReader(new StringReader(
              /* StringReader parses the String data loaded from file. */
              Files.readString(getStoragePath(), StandardCharsets.UTF_8)
          )),
          new TypeToken<List<User>>() {
          }.getType());
    } catch (IOException e) {
      throw new IOException(
          getStoragePath()
              + " is present, but an error occurred when reading users from user storage.");
    }
  }

  /**
   * Writes a list of User objects to local storage.
   *
   * @param users is a list of User objects.
   * @throws IOException     if an error occurred when trying to write to local
   *                         storage.
   * @throws JsonIOException if an error occurred when serializing the JSON
   *                         content.
   */
  private void writeToJson(List<User> users) throws JsonIOException, IOException {
    Objects.requireNonNull(users);

    try (FileWriter writer = new FileWriter(
        String.valueOf(getStoragePath()), StandardCharsets.UTF_8)) {
      try {
        getGson().toJson(users, writer);
      } catch (JsonIOException e) {
        writer.flush();
        writer.close();
        throw new JsonIOException("An error occurred when serializing the JSON content.");
      }

      writer.flush();
    }
  }

  /**
   * Reads all users from file, appends the provided user, and then overwrites
   * local storage file. Users are stored in an array.
   *
   * @param instance is the user that should be written to file
   * @throws IOException if there is an error reading from local storage
   */
  public void create(User instance) throws IOException {
    Objects.requireNonNull(instance);

    List<User> users = readUsers();

    if (users.size() == 0) {
      // To maintain consistency in the dataset, a single user is also stored in a list
      writeToJson(List.of(instance));
    } else {
      users.add(instance);
      writeToJson(users);
    }
  }

  /**
   * Takes in a username as a parameter and returns the corresponding user
   * from local storage, if there is a match.
   *
   * @param username is the identifier of the corresponding User object in storage
   * @return the corresponding User object
   * @throws IOException            if the file path in readUsers() is invalid
   * @throws NoSuchElementException if there are no users in storage
   */
  public User read(String username) throws IOException, NoSuchElementException {
    Objects.requireNonNull(username);

    List<User> users = readUsers();

    if (users.size() == 0) {
      throw new NoSuchElementException();
    }

    // Filters based on id and returns null if no match was found
    return users.stream().filter(user -> user.getUsername().equals(username))
        .findFirst().orElseThrow(NoSuchElementException::new);
  }

  /**
   * Finds a user based on a username and lets a consumer modify the user list
   * based on it. Writes the new user list to file.
   *
   * @param username user that should be taken action upon
   * @param action   consumer that specifies the action
   * @throws IOException            if an error occurred when trying to read the
   *                                User from local storage
   * @throws NoSuchElementException if no user with the given username was
   *                                found.
   */
  private void updateUser(String username, BiConsumer<List<User>, Integer> action)
      throws IOException, NoSuchElementException {
    Objects.requireNonNull(username);
    Objects.requireNonNull(action);

    List<User> users = readUsers();

    if (users.size() == 0) {
      throw new NoSuchElementException();
    }

    for (int i = 0; i < users.size(); i++) {
      User user = users.get(i);
      if (user.getUsername().equals(username)) {
        // Accept the provided action, passed in as parameter
        action.accept(users, i);

        writeToJson(users);
        return;
      }
    }

    // If loop is finished, no user with the given username was found
    throw new NoSuchElementException();
  }

  /**
   * Updates a user.
   *
   * @param username is the users username
   * @throws IOException            if an error occurred when trying to read the
   *                                User from local storage
   * @throws NoSuchElementException if no user with the given username was
   *                                found.
   */
  public void update(String username, User instance) throws NoSuchElementException, IOException {
    updateUser(username, (users, index) -> {
      // Remove old user
      users.remove((int) index);

      // Add new user
      users.add(index, instance);
    });
  }

  /**
   * Deletes a user.
   *
   * @param username is the users username
   * @throws IOException if an error occurred when trying to read the
   *                     User from local storage
   */
  public void delete(String username) throws IOException, NullPointerException {
    updateUser(username, ((users, integer) -> {
      // Remove old user
      users.remove(integer.intValue());
    }));
  }

  public Path getStoragePath() {
    return storagePath;
  }

  /**
   * Sets the storage path of the JSON data.
   *
   * @param filename is the filename of the JSON data
   */
  public void setStoragePath(String filename) {
    storagePath = Paths.get(System.getProperty("user.home"),
        "it1901-gr2103", "cognition", filename);
  }

  public Gson getGson() {
    return gson;
  }

  /**
   * Checks if the storage is empty.
   *
   * @return a boolean indicating if the storage is empty.
   */
  public boolean isEmpty() {
    File file = new File(String.valueOf(getStoragePath()));

    // file.length is 0 if file does not exist or has no content
    return file.length() == 0;
  }


  /**
   * Creates the directories required if they do not exist.
   *
   * @throws IOException if an error occurred when trying to create the
   *                     directories
   */
  private void createDirectoryIfNotExists() throws IOException {
    try {
      String stringPath = String.valueOf(getStoragePath());

      int lastDividerIndex = System.getProperty("os.name").contains("Windows")
          ? stringPath.lastIndexOf("\\")
          : stringPath.lastIndexOf("/");

      String dirPath = stringPath.substring(0, lastDividerIndex);

      Files.createDirectories(Path.of(dirPath));
    } catch (IOException e) {
      throw new IOException("An error occurred when trying to create directory: " + storagePath);
    }
  }
}