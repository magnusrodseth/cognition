package core.tools;

import java.util.UUID;

/**
 * Tools has static helper functions.
 */
public class Tools {
  public static String capitalize(String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }

  /**
   * Creates a random UUID string.
   * This string is in line with the UUID validation in isValidUuid.
   *
   * @return a String representation of a random UUID
   */
  public static String createUuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * Determines whether the provided UUID is a valid UUID.
   *
   * @param uuid is the String representation of the provided UUID.
   * @return a boolean determining if the UUID is valid.
   */
  public static boolean isValidUuid(String uuid) {
    return uuid != null && uuid.length() == 36 && uuid.split("-").length == 5;
  }
}
