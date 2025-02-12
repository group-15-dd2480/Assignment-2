# Assignment 2 - CI Server - Group 15

The purpose of the project is creating a small continous integration (CI) server for github repositories. The CI server will only contain the core features of CI integration, that being building, testing, and reporting results.

A webhook is configured to send push event notifications to our server, which triggers a build of the associated repository. The code is checked using `mvn test` and `mvn package`, the results are reported using the Github Status API.

## How to Build and Test

The code is written in Java and uses Maven for building, for testing we use JUnit5 and Mockito.

To build the code, run `mvn clean install` in the root directory of the project. This will compile the code, run the tests, and package the code into a jar file inside of the `target` directory.

To just run the tests, run `mvn test` in the root directory of the project.

Maven version: 3.9.9

Java version: 21.0.2

## Folder structure overview

The code can be found in `/src/main/java/org/dd2480`, with resources in `/src/main/resources`, and tests in `/src/test/java`.

## Statement of Contributions

#### Francis

- Implemented HTTP server and static file host.
- Implemented webhook endpoint.
- Implemented github actions to test code.

#### David

- Implemented handlers for listing all builds and listing individual builds."
- Implemented fetching of project files.

#### Markus

- Implemented Github Status API.
- Wrote README file.

#### Ebrar

- Implemented Build project and save build status.
- Implemented Build results.
