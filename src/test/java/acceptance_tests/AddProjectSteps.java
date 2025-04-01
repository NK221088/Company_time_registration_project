package acceptance_tests;

import dtu.example.ui.*;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import static org.junit.jupiter.api.Assertions.*;

public class AddProjectSteps {
    private ErrorMessageHolder errorMessage;

    public AddProjectSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user is logged in")
    public void aUserIsLoggedIn() {
//        CHANGE THIS
        new User("huba");
        TimeManager.login("huba");
//        ---------
        assertNotEquals(TimeManager.logged_in, "");
    }
    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        try {
        TimeManager.addProject(projectName);
    } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(TimeManager.projectExists(projectName));
    }

    @Given("a project with name {string} exists in the system")
    public void aProjectWithNameExistsInTheSystem(String projectName) {
        try {
            TimeManager.addProject(projectName);
        } catch (Exception e) {
                this.errorMessage.setErrorMessage(e.getMessage());
            }
    }
    @Then("the project is not created")
    public void theProjectIsNotCreated() {
    }
    @Then("the error message ”A project with name {string} already exists in the system and two projects can’t have the same name.” is given")
    public void theErrorMessageAProjectWithNameAlreadyExistsInTheSystemAndTwoProjectsCanTHaveTheSameNameIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
