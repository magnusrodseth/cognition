package core.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ToolsTest {
  @ParameterizedTest
  @CsvSource({"test,Test", "tEst,TEst", "javA,JavA"})
  @DisplayName("Test capitalization.")
  void testCapitalize(String input, String expected) {
    Assertions.assertEquals(expected, Tools.capitalize(input));
  }
}
