package api;

import java.util.Arrays;
import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Starts the Spring Boot application.
 */
@SpringBootApplication
public class RestApplication {
  public static final int TEST_PORT = 3000;
  private static int PORT = 8080;
  private static boolean testMode = false;

  /**
   * Starts the Spring Boot application.
   *
   * @param args are the optional arguments passed in when starting the Spring Boot application.
   */
  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(RestApplication.class);

    // If Spring Boot arguments contain the word "testmode",
    // set test mode to true.
    if (Arrays.stream(args).toList().contains("testmode")) {
      setTestMode(true);
      PORT = 3000;
    }

    // Set the server port
    application.setDefaultProperties(
            Collections.singletonMap("server.port", String.valueOf(PORT))
    );

    application.run(args);
  }


  public static void setTestMode(boolean testMode) {
    RestApplication.testMode = testMode;
  }

  public static boolean isTestMode() {
    return testMode;
  }

}
