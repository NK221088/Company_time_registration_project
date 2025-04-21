package acceptance_tests;

import dtu.time_manager.app.Project;
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
        new User("huba");
        TimeManager.login("huba");
        assertNotEquals("", TimeManager.logged_in);
    }
    @Given("the current year is {int}")
    public void theCurrentYearIs(Integer year) {
       assertEquals(2025, year);
    }
    @Given("the current project count is {int}")
    public void theCurrentProjectCountIs(Integer projectCount) {
        assertEquals(TimeManager.getProjectCount(),projectCount);
    }
    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        Project project = new Project(projectName, projectName);

        try {
            TimeManager.createProject(projectName);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(TimeManager.projectExists(projectName));
    }

    @Then("the project is assigned a project id {string}")
    public void theProjectIsAssignedAProjectId(String ProjectID) {
        Project project = TimeManager.getProjectFromID(ProjectID);
        assertNotNull(project);
    }


    @Given("a project with name {string} exists in the system")
    public void aProjectWithNameExistsInTheSystem(String projectName) {
        try {
            TimeManager.createProject(projectName);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project with name {string} is not created")
    public void theProjectIsNotCreated(String projectName) {
        assertFalse(TimeManager.projectDuplicateExists(projectName));
    }

    @Then("the project error message {string} is given")
    public void theErrorMessageAProjectWithNameAlreadyExistsInTheSystemAndTwoProjectsCanTHaveTheSameNameIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
