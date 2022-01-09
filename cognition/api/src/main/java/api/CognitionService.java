package api;

import java.io.IOException;
import java.util.Objects;
import json.CognitionStorage;
import org.springframework.stereotype.Service;

/**
 * Handles the service layer of the REST API, with the business logic of the application.
 */
@Service
public class CognitionService {
  /**
   * CognitionStorage is the local storage used to implement persistent storage.
   */
  private CognitionStorage cognitionStorage;

  /**
   * Default constructor initializes the application persistent storage.
   *
   * @throws IOException if an error occurs when initializing persistent storage.
   */
  public CognitionService() throws IOException {
    // Determines if the server is running on a port used for testing
    boolean isTest = System.getProperty("webRequestTest") != null;

    /*
     * If system property webRequestTest (indicating API test) is set,
     * or the RestApplication's testMode flag is set, use the test
     * storage file: cognitionTest.json.
     */
    if (isTest || RestApplication.isTestMode()) {
      setCognitionStorage(new CognitionStorage("cognitionTest.json"));
    } else {
      setCognitionStorage(new CognitionStorage());
    }
  }

  public CognitionStorage getCognitionStorage() {
    return cognitionStorage;
  }

  /**
   * Sets a new active instance of the persistent storage.
   *
   * @param cognitionStorage is an instance of the CognitionStorage class.
   */
  public void setCognitionStorage(CognitionStorage cognitionStorage) {
    this.cognitionStorage = Objects.requireNonNull(cognitionStorage);
  }
}
