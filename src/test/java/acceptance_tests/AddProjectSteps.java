package acceptance_tests;

import dtu.example.ui.*;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import static org.junit.jupiter.api.Assertions.*;

public class AddProjectSteps {

    @Given("a user is logged in")
    public void aUserIsLoggedIn() {
        assertNotEquals(TimeManager.logged_in, "");
    }
    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        TimeManager.addProject(projectName);
    }
    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(TimeManager.projectExists(projectName));
    }
}
