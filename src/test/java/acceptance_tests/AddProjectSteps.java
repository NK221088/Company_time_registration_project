package acceptance_tests;

import dtu.time_manager.app.domain.Project;
import dtu.time_manager.app.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import static org.junit.jupiter.api.Assertions.*;

public class AddProjectSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;

    public AddProjectSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @Given("the current year is {int}")
    public void theCurrentYearIs(Integer year) {
       assertEquals(2025, year);
    }
    @Given("the current project count is {int}")
    public void theCurrentProjectCountIs(Integer projectCount) {
        Project project1 = timeManager.createExampleProject("Project To Test Count 1", 1);
        Project project2 = timeManager.createExampleProject("Project To Test Count 2", 1);
        timeManager.addProject(project1);
        timeManager.addProject(project2);
        assertEquals(timeManager.getProjectCount(), projectCount);
    }
    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        try {
            timeManager.addProject(timeManager.createProject(projectName));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(timeManager.projectExists(projectName));
    }

    @Then("the project is assigned a project id {string}")
    public void theProjectIsAssignedAProjectId(String ProjectID) {
        assertNotNull(timeManager.getProjectFromID(ProjectID));
    }


    @Given("a project with name {string} exists in the system")
    public void aProjectWithNameExistsInTheSystem(String projectName) {
        try {
            timeManager.addProject(timeManager.createProject(projectName));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project with name {string} is not created")
    public void theProjectIsNotCreated(String projectName) {
        assertFalse(timeManager.projectDuplicateExists(projectName));
    }

    @Then("the project error message {string} is given")
    public void theErrorMessageAProjectWithNameAlreadyExistsInTheSystemAndTwoProjectsCanTHaveTheSameNameIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
