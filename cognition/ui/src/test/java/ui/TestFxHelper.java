package ui;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import json.CognitionStorage;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * TestFxHelper is a package-private class, as it is only used test in the ui module.
 * It serves as a helper class with static methods when running tests in the ui module.
 */
class TestFxHelper extends ApplicationTest {
  private final int TWO_SECONDS = 2000;

  /**
   * Waits for the event queue of the "JavaFX Application Thread" to be completed,
   * as well as any new events triggered in it
   */
  protected static void waitForFxEvents() {
    WaitForAsyncUtils.waitForFxEvents();
  }

  /**
   * Sleeps for a given amount of time.
   *
   * @param seconds is the amount of seconds to wait for.
   */
  protected static void sleep(int seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      fail();
    }
  }

  /**
   * Empties the JSON data in file at the storage path. Used before validating the
   * return type when user storage is empty.
   */
  protected static void clearTestStorage() {
    try (FileWriter writer = new FileWriter(String.valueOf(new CognitionStorage("cognitionTest.json").getStoragePath()))) {
      writer.write("");
    } catch (IOException e) {
      fail();
    }
  }

  /**
   * Helper method for clearing an input field with a given id. If the JavaFX Node
   * with the provided id cannot be found, the method simply returns.
   *
   * @param id is the ID of the JavaFX Node to find.
   */
  protected void clearInputField(String id) {
    TextField input = (TextField) findTextField(node -> true, id, 0);

    if (input == null) {
      return;
    }

    doubleClickOn(id).push(KeyCode.BACK_SPACE);
  }

  /**
   * Finds a TextField in the scene.
   * Inspired by the IT1901 staff.
   *
   * @param predicate limits the amount of nodes to search, if needed
   * @param selector  is identifier of the node to find
   * @param number    is the number of the node to find
   * @return a TextField node
   */
  protected Node findTextField(Predicate<TextField> predicate, String selector, int number) {
    Node textField = waitForNode(node -> node instanceof TextField tf
            && (selector == null || node.lookup(selector) != null)
            && predicate.test(tf), number);
    return textField.lookup(selector);
  }

  /**
   * Waits for a node in the scene.
   * Inspired by the IT1901 staff.
   *
   * @param predicate limits the amount of nodes to search, if needed
   * @param number    is the number of the node to find
   * @return a Node in the scene
   */
  private Node waitForNode(Predicate<Node> predicate, int number) {
    // Wait for FX events
    waitForFxEvents();

    // This array will later be populated with the target node
    Node[] nodes = new Node[1];

    try {
      WaitForAsyncUtils.waitFor(TWO_SECONDS, TimeUnit.MILLISECONDS,
              () -> {
                // Listen to view until node is found
                while (true) {
                  if ((nodes[0] = findNode(predicate, number)) != null) {
                    return true;
                  }
                  // Continuously wait until node is found
                  Thread.sleep(100);
                }
              }
      );
    } catch (TimeoutException e) {
      fail("No appropriate node available");
    }

    // We found target node
    return nodes[0];
  }

  /**
   * Finds a node in the scene.
   * Inspired by the IT1901 staff.
   *
   * @param predicate limits the amount of nodes to search, if needed
   * @param number    is the number of the node to find
   * @return a Node in the scene, or null if it does not exist
   */
  private Node findNode(Predicate<Node> predicate, int number) {
    // If number > 0, count is used to find the n-th node in the lookup
    int count = 0;
    for (Node node : lookup(predicate).queryAll()) {
      if (predicate.test(node) && count++ == number) {
        return node;
      }
    }
    return null;
  }
}
