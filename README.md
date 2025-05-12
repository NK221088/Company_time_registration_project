# TimeManager Application

## 📦 Project Overview
This is a Java 21-based time management application built using Maven and designed to run in IntelliJ IDEA. It includes:

- A graphical user interface
- Systematic white-box tests with JUnit
- TDD/BDD-style tests using Cucumber

---

## Running the Application

When the project is opened in IntelliJ, create a new run configuration, and set `dtu.timemanager.gui.App` as the main class.
Running this configuration should now open the application and greet you with the Login Menu.

If the project is opened in Eclipse, not much is different, importing as a Maven project and running the RunCucumberTest: `/src/test/java/acceptance_tests/RunCucumberTest.java` as a JUnit test will correctly run all the Cucumber tests. To run the application, you can run the App: `/src/main/java/dtu/timemanager/gui/App.java` as a Java Application in the editor.

You can log in with the following predefined users:
`huba`, `isak`, `bria`

---
### Running JUnit tests (Whitebox) or Cucumber tests

The Whitebox tests are located at `/src/test/java/whitebox_tests`  
The Cucumber features are located at `/features`  
And the Cucumber Step implementations are located at `/src/test/java/acceptance_tests`  
