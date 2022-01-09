package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = {RestApplication.class})
public class ContextTest {
  private CognitionController cognitionController;

  @BeforeEach
  void setUp() {
    try {
      cognitionController = new CognitionController();
    } catch (IOException e) {
      fail();
    }
  }

  @Test
  @DisplayName("Context loads.")
  void contextLoads() {
    Assertions.assertNotNull(cognitionController);
  }
}
