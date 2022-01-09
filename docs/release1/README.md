# Release 1

## Build using Maven

The `cognition` Java project uses Maven to build and run.

```sh
# Navigate to the cognition directory
cd cognition

# Install dependencies and run all tests
mvn install

# Run frontend (after running mvn install)
mvn javafx:run -f ui/pom.xml
```

> Please note that this is now updated. Please see the [root `README.md`](../../README.md) for updated information on how to build and run the application.

## Gitpod

The application can be developed and executed using Gitpod. Use the badge in the root [`REAMDE.md`](../../README.md) to
launch the application using Gitpod.

Each developer is encouraged to use their IDE of choice, as long as all functionality also supports using Gitpod.

## Contributing to the code base

[Click here](../../CONTRIBUTING.md) to read more about contributing to the code base.

## User Interface

Please see the [UI module documentation](../../cognition/ui/README.md) for thorough documentation of its logic and
classes.

## Core and persistent local storage

Please see the [Core module documentation](../../cognition/core/README.md) for thorough documentation of its logic and
classes.

## REST API

Please see the [API module documentation](../../cognition/api/README.md) for thorough documentation of its logic and
classes.

## Continuous Integration (CI) and code quality

The pipeline for continuous integration must succeed before merging new functionality.

The code must compile, pass all code quality checks and pass all tests.

The following tools will be used to ensure code quality:

- [Checkstyle](https://checkstyle.sourceforge.io). Validates design, checks code layout and checks code formatting.

- [Spotbugs](https://spotbugs.github.io/). Highlights potential bugs and resource flaws in the Java code.

- [Jacoco](https://www.jacoco.org/jacoco/). Gathers test information and displays code coverage.

## Testing

New functionality should - if appropriate (which it almost always is) - be tested before it is merged.

## Reflection

### Setting up the multi-module Maven project

In short, deliverable 1 went quite smoothly. Setting up the multi-module Maven project was fine in itself. Specifying
each module's dependencies and opening up packages to other modules was a bit challenging, but the group worked it out
during pair programming with some trial and errors. In this case, Maven was quite strict with naming and directory
structure.

### Issue tracking and branching

Working with issue tracking and branches is something all 4 group members are comfortable with from before. Thus, the
real challenge lies in scoping the issues correctly, i.e. not creating too large issues. For group deliverable 1, the
group feels that this went very well. As mentioned in the deliverable documentation, we aim to scope each issue to take
a maximum of 12 hours. For more information, see the [`CONTRIBUTING.md`](../../CONTRIBUTING.md) documentation,
specifically under `### Scope of an issue`. In general, we feel that we accomplished this throughout the entire
milestone of group deliverable 1.

> Please note that some issues have lived for several days. Internally in the group, we figured that some long-lived branches connected to a given issue works well. As an example, a documentation branch for each release makes sense to keep long-lived. Thus, the connected issue will also be long-lived.

### Issue templates

Working with GitLab issue templates and GitLab merge request templates also helped us narrow down and specify each
problem to solve. This was beneficial both for the developer and the code reviewer.

### Summary

To summarize, group deliverable 1 mainly focused on laying down the groundwork for a pleasant Git workflow. There were
some hiccups whilst setting up the multi-module Maven project, but that was quickly solved due to good teamwork. The
group is happy with the current groundwork, and agrees that this will be easier to build upon in later stages.

## Design

[Click here](../design/README.md) to read about and view the design of the client application. 