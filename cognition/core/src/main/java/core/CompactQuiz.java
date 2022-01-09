package core;

import core.tools.Tools;

/**
 * CompactQuiz is a class that acts like a struct used as a "lighter" version of a Quiz object.
 * When fetching all quizzes for the current user,
 * we only need to display the quiz titles
 * and keep track of the corresponding identifier,
 * in order to not fetch an unnecessary amount of data at once.
 * Thus, we first fetch a CompactQuiz, and then later fetch a complete Quiz on-demand.
 */
public class CompactQuiz {
  /**
   * This UUID corresponds to the UUID in {@link core.Quiz}.
   */
  private String uuid;

  /**
   * This name corresponds to the name in {@link core.Quiz}.
   */
  private String name;

  /**
   * Initializes a "lighter" version of a Quiz object.
   * See {@link core.CompactQuiz} for more information.
   *
   * @param uuid corresponds to the UUID for a quiz in {@link core.Quiz}
   * @param name corresponds to the name for a quiz in {@link core.Quiz}
   */
  public CompactQuiz(String uuid, String name) {
    setUuid(uuid);
    setName(name);
  }

  private void setUuid(String uuid) {
    if (!Tools.isValidUuid(uuid)) {
      throw new IllegalArgumentException();
    }

    this.uuid = uuid;
  }

  private void setName(String name) {
    if (!Quiz.isValidName(name)) {
      throw new IllegalArgumentException();
    }

    this.name = name;
  }

  public String getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }
}
