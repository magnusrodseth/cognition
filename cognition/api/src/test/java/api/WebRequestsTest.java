package api;

import com.google.gson.Gson;
import core.Quiz;
import core.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WebRequestsTest tests querying the actual API and the REST controller logic
 * behind the API. This class is treated more like an integration test, given
 * the nature of the test methods.
 */
@SpringBootTest(classes = CognitionController.class)
@AutoConfigureMockMvc
@EnableWebMvc
@AutoConfigureRestDocs(outputDir = "src/main/asciidoc")
public class WebRequestsTest {

    @Autowired
    private MockMvc mvc;

    private final Gson gson = new Gson();
    private final String username = "test-username";
    private final String password = "test-password";
    private final String quizUuid = "9f6c96cc-6a70-46bc-8f69-31b2ebd661cd";
    private final String invalidQuizUuid = "thisIsNotAUuid";

    /*
     * Set state to indicate to CognitionController that we are in test mode, and
     * therefore should use the appropriate storage file used for testing.
     */
    static {
        System.setProperty("webRequestTest", "true");
    }

    @AfterEach
    void deleteUser() {
        // Clean up after each test
        try {
            this.mvc.perform(delete("/users/" + username)).andExpect(status().isOk());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when getting users.")
    void expect200WhenGettingUsers() {
        try {
            initializeUser();

            this.mvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(
                    document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when getting user by username.")
    void expect200WhenGettingUserByUsername() {
        try {
            initializeUser();

            this.mvc.perform(get("/users/" + username).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when getting quiz titles by username.")
    void expect200WhenGettingQuizTitlesByUsername() {
        try {
            initializeUser();
            initializeQuiz();

            this.mvc.perform(get("/quizzes/" + username + "/titles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when posting user.")
    void expect200WhenPostingUser() {
        User user = new User(username, "valid-password");
        String serialized = gson.toJson(user);

        try {
            this.mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(serialized))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when deleting user.")
    void expect200WhenDeletingUser() {
        User user = new User(username, "valid-password");
        String serialized = gson.toJson(user);

        // Create sample user to later be deleted
        try {
            this.mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(serialized))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 409 when posting user with conflicting identifier")
    void expect409WhenPostingUserWithConflictingIdentifier() {
        User conflictingUser = new User(username, "valid-password");
        String serialized = gson.toJson(conflictingUser);

        try {
            initializeUser();

            this.mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(serialized))
                    .andExpect(status().is4xxClientError()).andDo(document("{methodName}",
                            preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when updating user.")
    void expect200WhenUpdatingUser() {
        User user = new User(username, "new-password");
        String serialized = gson.toJson(user);

        try {
            initializeUser();

            this.mvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).content(serialized)).andDo(print())
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    @DisplayName("Expect 200 when getting quizzes by username")
    void expect200WhenGettingQuizzesByUsername() {
        try {
            // Setup
            initializeUser();
            initializeQuiz();

            this.mvc.perform(get("/quizzes/" + username).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when creating quiz by username")
    void expect200WhenCreatingQuizByUsername() {

        Quiz quiz = new Quiz(quizUuid, "Test quiz", "Test description for test quiz");
        String serializedQuiz = gson.toJson(quiz);

        try {
            initializeUser();

            this.mvc.perform(post("/quiz/" + username).contentType(MediaType.APPLICATION_JSON).content(serializedQuiz))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
            ;
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 409 when creating quiz with conflicting UUID")
    void expect409WhenCreatingQuizWithConflictingUuid() {
        Quiz quiz = new Quiz(quizUuid, "Test quiz", "Test description for test quiz");
        String serializedQuiz = gson.toJson(quiz);

        try {
            initializeUser();
            initializeQuiz();

            this.mvc.perform(post("/quiz/" + username).contentType(MediaType.APPLICATION_JSON).content(serializedQuiz))
                    .andExpect(status().is4xxClientError()).andDo(document("{methodName}",
                            preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
            ;
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when getting quiz by UUID")
    void expect200WhenGettingQuizByUuid() {
        try {
            initializeUser();
            initializeQuiz();

            this.mvc.perform(get("/quiz/" + quizUuid).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 404 when getting quiz by invalid UUID")
    void expect404WhenGettingQuizByInvalidUuid() {
        try {
            initializeUser();
            initializeQuiz();

            this.mvc.perform(get("/quiz/" + invalidQuizUuid).contentType(MediaType.APPLICATION_JSON)).andDo(print())
                    .andExpect(status().isNotFound()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when updating quiz by UUID")
    void expect200WhenUpdatingQuizByUuid() {
        Quiz quiz = new Quiz(quizUuid, "Updated quiz", "Updated description");
        String serializedQuiz = gson.toJson(quiz);

        try {
            initializeUser();
            initializeQuiz();

            this.mvc.perform(put("/quiz").contentType(MediaType.APPLICATION_JSON).content(serializedQuiz))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
            ;
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Expect 200 when deleting quiz by UUID")
    void expect200WhenDeletingQuizByUuid() {
        try {
            initializeUser();
            initializeQuiz();

            this.mvc.perform(delete("/quiz/" + quizUuid).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(document("{methodName}", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
            ;
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Initializes a test user to be used when testing endpoints.
     *
     * @throws Exception if an error occurs when initializing user.
     */
    private void initializeUser() throws Exception {
        User user = new User(username, password);
        String serialized = gson.toJson(user);

        this.mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(serialized))
                .andExpect(status().isOk());
    }

    private void initializeQuiz() throws Exception {
        Quiz quiz = new Quiz(quizUuid, "Test quiz", "Test description for test quiz");
        String serializedQuiz = gson.toJson(quiz);

        this.mvc.perform(post("/quiz/" + username).contentType(MediaType.APPLICATION_JSON).content(serializedQuiz))
                .andExpect(status().isOk());
    }
}
