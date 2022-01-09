# Integration tests

## Description

The `integration_tests` module tests and verifies that the client application can successfully communicate with a
Spring Boot web server running on port for testing (`3000`) from the [`api`](../api) module.

This is tested by using the presentation layer
controller's [`RemoteCognitionAccess`](../ui/src/main/java/ui/RemoteCognitionAccess.java) in order to communicate with a
running Spring Boot web server.

The group reflected upon adding more test, i.e. one integration test per view. We deemed this redundant, compared to
simply testing with one view due to the following reasons:

- The integration tests should not **primarily** test actual module-specific functionality. This is handled by the
  respective modules.
- The same instance of our [`RemoteCognitionAccess`](../ui/src/main/java/ui/RemoteCognitionAccess.java) - found in
  the `ui` module - is passed around when switching views and consequently presentation layer controllers.

## Reflection

For more reflection on the `integration_tests` module, please read
the [deliverable 3 documentation](../../docs/release3/README.md), specifically under the heading `### Integration tests and deployment tests`.
