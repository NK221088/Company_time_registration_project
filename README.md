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

To run the application in Eclipse, you can run the App: `/src/main/java/dtu/timemanager/gui/App.java` as a Java Application in the editor.

You can log in with the following predefined users:
`huba`, `isak`, `bria`

Although it might seem confusing, when unfamiliar, you have to double-click on the text label fields that represent that property to edit the properties of Projects or Activities. For example, to change the name, you have to double-click the text that says: "Project Name: xxx".

---
### Running JUnit tests (Whitebox) or Cucumber tests

The Whitebox tests are located at `/src/test/java/whitebox_tests`  
The Cucumber features are located at `/features`  
And the Cucumber Step implementations are located at `/src/test/java/acceptance_tests`  

If the project is opened in Eclipse, not much is different, importing as a Maven project and running the RunCucumberTest: `/src/test/java/acceptance_tests/RunCucumberTest.java` as a JUnit test will correctly run all the Cucumber tests.
