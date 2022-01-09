# Release 2

## Feedback from deliverable 1

We focus on the suggested improvements in this section. We also had a lot of positive feedback for deliverable 1.

### Validation in setters

In deliverable 1, we had _some_ validation in the setters in the Java models in the `core` module. However, it was not sufficient, neither for us nor our client (our student assistant).

We solved this by adding static validation methods to be used by the Java models and in the UI. This validation is naturally tested. The Java models throw an exception which is handling in the frontend.

### Assigning issues to developers

In deliverable 1, we got some feedback on remembering to assigning issues to developers.

We acknowledge that not all our issues are assigned to a developer. Internally in the group, we accept that a developer forgets to assign himself to an issue, **as long as the merge request is assigned to the developer**.

To back this up, we received positive feedback from our client that we use reasonable and easy-to-understand branching and merge requests.

> Since we received this feedback, we have been more strict on assigning each issue to an assignee.

## Changelog from deliverable 1 to deliverable 2

Below, you can view a short summary of the changelog from deliverable 1 to deliverable 2:

- Improve upon local persistent storage, such that users, quizzes and flashcards are stored persistently
- Implement _Dashboard_, _My Quizzes_, _Quiz_ and _View Quiz_ view with corresponding logic
  - A user can create, update and delete a quiz of flashcards
  - A user can go through a given quiz and answer the flashcards
- Add tests for `ui` module and `core` module
  - Both `ui` module and `core` module has test coverage of at least 70%
- Add initially planned extra feature: A user can now search their quizzes to filter the list of quizzes
- Style all views
- Add continuous integration
- Update documentation in the code base

## Persistent Storage

### Document metaphor versus implicit memory

The Cognition application uses **implicit memory for persistent storage**. This means that when the user interacts with the application, the user does not have to worry about explicitly stating that the user wants to store data persistently.

Take [Quizlet](https://quizlet.com/) as an example. As a user, I create a quiz consisting of flashcards, and click _"Create"_. As a user, I do not care how the data is persistently stored. I only care about the data being there the next time I launch the application. Thus, we have no explicit _"Store current state of the application"_ button. This would conflict with our design choice for persistence.

To compare, take _Microsoft Word_ - without automatic backup functionality to a cloud service - as a contrary example. When a user writes in a local Microsoft Word instance, the data is usually not automatically saved without the user's influence. If I want to keep my current progress, I go to `File -> Save As...`, and then persistently save my progress. This is an example of a document metaphor (desktop) for persistent storage.

### Local Storage

Please see the [Core module documentation](../../cognition/core/README.md) for a thorough documentation of its classes, including local persistent storage. This documentation also includes the definition of the file format used.

## Continuous Integration

The repository uses continuous integration (hereby referred to as CI) pipeline before merging new features to the main branch. This pipeline runs the following Maven commands using a Maven image:

The configuration for the pipeline can be found in [`.gitlab-ci.yml`](../../.gitlab-ci.yml). It uses settings found in [`.m2/settings.xml`](../../.m2/settings.xml).

The pipeline for continuous integration must succeed before merging new functionality.

The code must compile, pass all code quality checks and pass all tests.

Please note that we do not run tests in the `ui` module in GitLab, as it is not supported. This is confirmed by the IT1901 staff. Our solution to this problem is to test the `api` and `core` module in the CI pipeline, and then test the `ui` module locally before merging. That way, all tests are validated before merging.

> There may be a way to achieve headless UI tests running in GitLab using a particular Maven configuration. However, we deem this a low priority compared to other work. **We always test the client application locally before merging.**

## Architecture Documentation

[Click here](./ARCHITECTURE.md) to read the architecture documentation, with illustrating diagrams.

## Work habits, workflow and code quality

We use automated tests in order to decrease developer effort of manually running and re-running tests. See the `## Continuous Integration` section above for more information.

The following tools is used to ensure code quality:

- [Checkstyle](https://checkstyle.sourceforge.io). Validates design, checks code layout and checks code formatting. Please read the note below regarding Checkstyle warnings.

- [Spotbugs](https://spotbugs.github.io/). Highlights potential bugs in Java code. There are some cases in our code base which Spotbugs do not understand. These are suppressed using the custom `@SuppressFBWarnings` annotation.

- [Jacoco](https://www.jacoco.org/jacoco/). Gathers test information and displays code coverage.

### Important to note

The `ui` module has 1 Checkstyle warning we have chosen not to handle, due to the use of our custom `@SupressFBWarnings` annotation. This annotation is inspired by the IT1901 staff's example Java project. The Checkstyle warning as follows:

```sh
[WARN] .../gr2103/cognition/ui/src/main/java/ui/controllers/annotations/SuppressFBWarnings.java:11:19: Abbreviation in name 'SuppressFBWarnings' must contain no more than '1' consecutive capital letters. [AbbreviationAsWordInName]
```

**However, `SuppressFBWarnings` must have this specific name to be considered by SpotBugs. This is confirmed by the IT1901 staff.** Thus, we intentionally ignore this Checkstyle warning.

> As of release 3, SuppressFBWarnings is no longer in use and therefores does not trigger a Checkstyle warning.

## Test coverage

The focus for group deliverable 2 was robust local, persistent storage, MVP frontend functionality and some extra features. To enforce this, we have written tests for the `ui` module and `core` module.

We aim to have at least collectively 70% or more test coverage per module. Please note that some classes are not easy to test in isolation (e.g. [`ui/App`](../../cognition/ui/src/main/java/ui/App.java)). This is confirmed by the IT1901 staff.

### Business logic and persistence (`core` module)

These tests can be found [here](../../cognition/core/src/test/java).

The tests in [`test/java/core`](../../cognition/core/src/test/java/core) validate that all models work as intended.

The tests in [`test/java/json`](../../cognition/core/src/test/java/json) validate that you can create, read, update and delete the respective Java models. Furthermore, the tests check some edge cases. For more information, see the Javadoc in the respective classes.

### Frontend (`ui`)

These tests can be found [here](../../cognition/ui/src/test/java/ui). We validate that presentation logic works as expected. We are confident that our test method naming convention is readable. Implementation detail is documented with additional comments. For more information, see the Javadoc in the respective classes.

## Modularization

### User Interface

Please see the [UI module documentation](../../cognition/ui) for a thorough documentation of its classes.

### Core and persistent local storage

Please see the [Core module documentation](../../cognition/core) for a thorough documentation of its classes.

### REST API

Please see the [API module documentation](../../cognition/api) for a thorough documentation of its classes.

> Please note that we have added a module called [`integration_tests`](../../cognition/integration_tests).

## Reflection

### Storage format

Currently, all data is written to one file: `~/it1901-gr2103/cognition/cognition.json`. In this file, the users are stored in an array with their corresponding quizzes. The advantage of this solution is that it is very comfortable to work with, as our file format and JSON representation directly corresponds to how our Java models are structured. On the contrary, we recognize that in an upscaled version of the app, this would be very inefficient. All users are read and written every time one single flashcard is updated or added. However, this is only a small scale project. As such, we stick to the [file format definition found in the core module documentation (`### Storage Format`)](../../cognition/core/README.md).

### Enforcing characters limits on user input

When the user creates a quiz, the user adds a name, description and one or more flashcards to the current quiz. We have chosen not to enforce an upper limit to the length of the flashcard's front and back side.

There are both advantages and disadvantages to this approach. One advantage of enforcing such a character limit is that we as developers and maintainers gain more control over the visual representation. As an example, if a user inputs 50'000 characters on the front of the flashcard, the view of the flashcard may appear disfigured and incorrectly formatted. However, we as developers do not want to affect the user's formulation on a flashcard. As an example, a user wants to write a some sentences and a question with 1'000 characters on the front of the flashcard. If we enforce an upper limit to the length of the flashcard's front and back, the user will have to trim down the formulation, potentially impeding them.

However, we do enforce an upper limit to the length of the a quiz' description. This is because the purpose of a quiz description is to quickly describe the essence of a quiz, rather than writing multiple paragraphs. We find that an upper limit of 500 characters fits.

> Please note that this was changed for the final deliverable. We added character limits in order for the UI elements to not overflow.

### Spotbugs and resource leaks

During the group deliverable 2 milestones, we expanded upon the local, persistent storage. This, of course, included writing to file using the Java `FileWriter`. Spotbugs helped us expose a potential resource leak: We had an edge case where the `FileWriter` would not be flushed and closed correctly. This is a prime example of why one should utilize such workflow tools to improve the robustness of the application.

### Pair Programming

The group has practiced regular pair programming sessions during development. This helps drive the progress forward faster. Additionally, all group members stay up to date on the different parts of our application.

When pair programming, we usually split into two groups of 2 developers. One developer takes the role of a **pilot**; the one sitting at the keyboard and writing the code. The pilot's responsibility is not only to write the code, but also to argue for why we write the code the way we do. The other developer takes the role of a **co-pilot**, sitting next to the pilot and verifying that we're heading in the same direction. When committing code, we seek to add who the code was co-authored by in order to maintain a tidy and clear Git commit history.

When we're not pair programming physically, we seek to give suggested changes on GitLab during code reviews for merge requests.

During parts of the milestone for group deliverable 2, a group member was physically absent during usual pair programming hours. We solved this by using JetBrains IntelliJ's integrated code session tool([_Code with Me_](https://www.jetbrains.com/help/idea/code-with-me.html)), because all developers use this IDE. This way, we could cooperate as we usually would during pair programming, without worrying about the obstacle of not being physically close to each other. This solution worked great with no problems regarding latency, bugs, etc... We will continue to use this tool in the future if other members are physically absent.

### Problems and solutions

#### TestFX and persistent storage

As previously stated, our primary focus for this deadline was a robust solution for the `core` and `ui` modules. Testing the `core` module worked fine; no significant challenges occurred there. However, testing the `ui` module using [TestFX](https://github.com/TestFX/TestFX) proved to be quite challenging, specifically testing the process of switching a view. For example, when a valid user logs in, the user gets taken to the Dashboard view, i.e. the view switches.

Part of the problem was that each UI controller initialized a new instance of the local storage, e.g. `new CognitionStorage()`. We solved this by having the initially loaded controller, that is the `LoginController`, set an instance of the local storage, e.g. `new CognitionStorage()`. That instance would then be passed around to a new controller using a setter, e.g. `setCognitionStorage(cognitionStorage)`, whenever the view switches, ensuring continuity in the local storage persistence.

Furthermore, when writing the tests for the `ui` module, we discovered that we manipulated the application's local storage by mistake. For example, testing that one can create a valid user should not add that test user to the actual application's local storage. Rather, the test user should be added to a test file (e.g. `cognitionTest.json`), and then cleared upon test completion.

As an extension of the solution, using setters to inject one instance of the `Storage` as explained above, we were now able to separate the application's local storage and the tests' local storage by simply initializing a new instance of the local storage, e.g. `new CognitionStorage("cognitionTest.json")`;

Please see the [start method in the App class](../../cognition/ui/src/main/java/ui/App.java) and the [UI tests](../../cognition/ui/src/test/java/ui) for a better understanding of the solution.

**In summary**, we find this to be a very clean solution to what proved to be a rather complex problem with many moving parts.

> For deliverable 3, the UI has no direct ties to the local storage. The UI interacts with local storage using the REST API.
