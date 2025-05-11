# TimeManager Application

## 📦 Project Overview
This is a Java-based time management application built using Maven and designed to run in IntelliJ IDEA. It includes:

- A graphical user interface (`dtu.timemanager.gui.App`)
- Systematic white-box tests with JUnit
- BDD-style tests using Cucumber
- Predefined users for login functionality

---

## 🚀 How to Run the Application

### Steps

1. **Clone the repository or unzip the project ZIP folder**
   ```bash
   git clone <your-repository-url>
   cd your-project-folder


2. **Open IntelliJ IDEA**

   * Choose **File > Open...**
   * Navigate to the unzipped/cloned folder and select the root `pom.xml` file.
   * IntelliJ will auto-import the Maven project.

3. **Run the Application**

   * Locate the class `dtu.timemanager.gui.App`
   * Right-click and choose **Run 'App.main()'**


## 🔐 Login Information

Upon launching the application, you can log in with the following predefined users:

| Username | Password |
| -------- | -------- |
| `huba`   | `huba`   |
| `isak`   | `isak`   |
| `bria`   | `bria`   |

## ✅ Running the Tests

### JUnit Tests (Systematic Tests)

1. In IntelliJ, open the `src/test/java` directory.
2. Locate the test classes under appropriate packages.
3. Right-click the test class or folder and select **Run Tests**.

### Cucumber Tests (BDD)

* Located in the same `src/test/java` structure, typically under a `cucumber` or `bdd` package.
* Feature files are in `src/test/resources`.
* You can run the test runner class (e.g., `RunCucumberTest`) directly.

---

## 📁 Project Structure

```
/src
  /main
    /java/dtu/timemanager/gui/App.java
    ...
  /test
    /java
      /...JUnit and Cucumber tests
    /resources
      *.feature
pom.xml
README.md
```