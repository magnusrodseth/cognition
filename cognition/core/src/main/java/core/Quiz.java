package core;


import core.tools.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Creates a new Quiz object that stores a name, a description and a list of flashcards.
 */
public class Quiz {
  public static final int MAX_DESCRIPTION_LENGTH = 260;
  public static final int MAX_TITLE_LENGTH = 60;
  private String uuid;
  private String name;
  private String description;
  private List<Flashcard> flashcards = new ArrayList<>();

  public Quiz() {
  }

  /**
   * Constructor that takes in all the required parameters of the Quiz and
   * validates them.
   *
   * @param uuid        is the Universal Unique ID of the Quiz
   * @param name        is the name of the Quiz
   * @param description is the description of the Quiz
   */
  public Quiz(String uuid, String name, String description) {
    setUuid(uuid);
    setName(name);
    setDescription(description);
  }

  public static boolean isValidName(String name) {
    return !name.trim().equals("") && name.length() >= 1;
  }

  public static boolean isValidDescription(String description) {
    return !description.trim().equals("") && description.length() <= MAX_DESCRIPTION_LENGTH;
  }

  /**
   * Loops through all provided flashcards and runs them through the singular
   * {@link core.Quiz#addFlashcard(Flashcard)} method.
   *
   * @param flashcards is the provided list of flashcards
   */
  public void addFlashcards(List<Flashcard> flashcards) {
    for (Flashcard flashcard : Objects.requireNonNull(flashcards)) {
      addFlashcard(flashcard);
    }
  }

  public String getUuid() {
    return uuid;
  }

  private void setUuid(String uuid) {
    if (!Tools.isValidUuid(uuid)) {
      throw new IllegalArgumentException();
    }

    this.uuid = uuid;
  }

  /**
   * Adds a flashcard to the list of flashcards, if the list does not already
   * contain it and the flashcard is not null.
   *
   * @param flashcard is a flashcard object
   */
  public void addFlashcard(Flashcard flashcard) {
    Objects.requireNonNull(flashcard);

    if (!flashcards.contains(flashcard)) {
      flashcards.add(flashcard);
    }
  }

  /**
   * Removes the given flashcard from the list of flashcards.
   *
   * @param flashcard the flashcard to remove
   */
  public void removeFlashcard(Flashcard flashcard) {
    Objects.requireNonNull(flashcard);
    // Removes only if it is present. Thus, not conditional is needed.
    flashcards.remove(flashcard);
  }

  public String getName() {
    return name;
  }

  /**
   * Checks if the name is a valid name, else throw IllegalArgumentException.
   *
   * @param name is the name of the Quiz
   */
  public void setName(String name) {
    if (!Quiz.isValidName(name)) {
      throw new IllegalArgumentException();
    }

    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Checks if the description provided is a valid description If not then throw
   * IllegalArgumentException.
   *
   * @param description is the description of the Quiz
   */
  public void setDescription(String description) {
    if (!Quiz.isValidDescription(description)) {
      throw new IllegalArgumentException();
    }
    this.description = description;
  }

  /**
   * Gets a copy of the flashcards.
   *
   * @return a copy of the flashcards.
   */
  public List<Flashcard> getFlashcards() {
    return new ArrayList<>(flashcards);
  }


  /**
   * Sets the list of flashcards to the provided flashcards if the list is not
   * null.
   */
  public void setFlashcards(List<Flashcard> flashcards) {
    this.flashcards = Objects.requireNonNull(flashcards);
  }
}
