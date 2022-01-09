# IT1901 - Group 2103

[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.stud.ntnu.no/#https://gitlab.stud.idi.ntnu.no/it1901/groups-2021/gr2103/gr2103)

## Description

Cognition is - plain and simple - a flashcard application. If you've used [Quizlet](https://quizlet.com/), Cognition
should feel quite familiar.

The Java project is located inside the `cognition` directory. [Click here](./cognition/README.md)
to read the project description.

## Deliverable Documentation

The documentation for each deliverable is located inside the `docs` directory. [Click here](./docs) to read the deliverable documentation.

## Developer Information

Developed by:

- Magnus Rødseth
- Julian Grande
- Thomas Hasvold
- Henrik Skog

## Tech Stack

- JavaFX (client application)
- Java
- Maven
- Spring Boot (web server application)

## Running the application

Make sure you have Maven installed and can successfully use `mvn` in your terminal. Launching the application using Gitpod should automatically solve this issue for you. If you are on Mac or Linux, simply open the terminal and write `brew install maven` if you do not have Maven installed locally already.

### Important to note

The `cognition` project supports using `make` as a wrapper for running `mvn` commands. This is done to improve the quality of life for the current developers and "future" developer, i.e. the person grading this project.

### Using `make` with `mvn` under the hood **(recommended for ease of use)**

```sh
# Navigate to the cognition directory.
cd cognition

# Install dependencies, start application server, and start client application.
# Alternatively, "make app" does the same.
make

# Install dependencies, and run tests for all modules.
make test

# Print the available targets.
make help
```

**`make test` and `make` are all that is needed to run tests and the Cognition application.**

Please see the [`Makefile`](./cognition/Makefile) for more information on the targets we have made.

### Using `mvn`

```shell
# Navigate to the cognition directory.
cd cognition

# Install dependencies and run tests in all modules.
mvn clean install

# Start Spring Boot server on port for application logic.
cd api && mvn spring-boot:run

# Run the client application.
cd ui && mvn javafx:run

# Stop Spring Boot server running on port for application logic
lsof -t -i:8080 | xargs kill -9
```

**As seen in the commands above, the `make` option is clearly quicker and more pleasant for the developer.**

## Packaging the application

[Click here](./cognition/README.md) for more information on packaging the application.
