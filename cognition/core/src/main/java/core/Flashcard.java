package core;

import core.tools.Tools;
import java.util.Objects;

/**
 * A single Flashcard in a collection of flashcards, corresponding to a given Quiz.
 */
public class Flashcard {
  private String uuid;
  private String front;
  private String answer;

  /**
   * Initializes a Flashcard object with the provided parameters.
   *
   * @param uuid   is the identifier for the Flashcard.
   * @param front  is the front statement or question on the Flashcard.
   * @param answer is the flipped side of the Flashcard, and the answer to the
   *               front.
   */
  public Flashcard(String uuid, String front, String answer) {
    setUuid(uuid);
    setFront(front);
    setAnswer(answer);
  }

  /**
   * Initializes a Flashcard object with no values. This is used when
   * deserializing the object.
   */
  public Flashcard() {
  }

  public static boolean isValidFront(String front) {
    Boolean check = front.trim().equals("");
    return !check && front.length() >= 1;
  }

  public static boolean isValidAnswer(String answer) {
    Boolean check = answer.trim().equals("");
    return !check && answer.length() >= 1;
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

  public String getFront() {
    return front;
  }

  /**
   * Validates and sets front field.
   *
   * @param front new front value
   */
  private void setFront(String front) {
    if (!Flashcard.isValidFront(front)) {
      throw new IllegalArgumentException();
    }

    this.front = front;
  }

  public String getAnswer() {
    return answer;
  }

  /**
   * Validates and sets answer field.
   *
   * @param answer new answer value
   */
  private void setAnswer(String answer) {
    if (!Flashcard.isValidAnswer(answer)) {
      throw new IllegalArgumentException();
    }
    this.answer = answer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Flashcard flashcard = (Flashcard) o;
    return Objects.equals(uuid, flashcard.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    return "Flashcard{" + "UUID=" + uuid + ", front='" + front + '\''
        + ", answer='" + answer + '\'' + '}';
  }
}
