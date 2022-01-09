##################################################################
#          Project Convenience Makefile Wrapper for Maven        #
##################################################################

# This makefile is just a convenience wrapper for Maven.
# The actual building rules for this project may
# be found in the Maven "pom.xml" files in the project modules.

######################### DEFINITIONS ############################

# Define the command line invocation of Maven, if necessary:
ifeq ($(MVN),)
    MVN  := mvn
endif

.PHONY: app test install tests application client server help

# Install dependencies, start application server, and start client application.
app: install application

# Install dependencies, start test server, and run tests for all modules.
test: install tests

# Install dependencies without running tests.
install:
	@ $(MVN) $(MVNFLAGS) clean install -DskipTests

# Run tests for all Maven modules.
tests:
	@ $(MVN) $(MVNFLAGS) test

# Run the client application, and start Spring Boot server on port for application logic.
application:
	@ cd api && $(MVN) $(MVNFLAGS) spring-boot:run &\
  	cd ui && sleep 2 && $(MVN) $(MVNFLAGS) javafx:run

# Run the client application.
client:
	@ cd ui && $(MVN) $(MVNFLAGS) javafx:run

# Run the Spring Boot server.
server:
	@ cd api && $(MVN) $(MVNFLAGS) spring-boot:run

# Stop the Spring Boot server.
stop-server:
	@ lsof -t -i:8080 | xargs kill -9

# Print the available targets.
help:
	@ echo "Usage   :  make <target>"
	@ echo "Targets :"
	@ echo "   app : Install dependencies, start application server, and start client application."
	@ echo "   test : Install dependencies, start test server, and run tests for all modules."
	@ echo "   install : Install dependencies without running tests."
	@ echo "   tests : Run tests."
	@ echo "   application : Run the client application, and start Spring Boot server on port for application logic."
	@ echo "   client : Run the client application."
	@ echo "   server : Run the Spring Boot server."
	@ echo "   stop-server : Stop the Spring Boot server."
	@ echo "   help : Print the available targets."