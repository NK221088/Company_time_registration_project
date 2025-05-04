package acceptance_tests;

import dtu.time_manager.domain.Project;
import dtu.time_manager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class AddProjectSteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private String projectName;

    public AddProjectSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user is logged in")
    public void aUserIsLoggedIn() {
        timeManager.addUser(new User("huba"));
        timeManager.login("huba");
        assertNotNull(timeManager.getCurrentUser());
    }

    @Given("the current year is {int}")
    public void theCurrentYearIs(Integer year) {
        // This is now handled by the TimeManagerService internally
        assertEquals(2025, year);
    }

    @Given("the current project count is {int}")
    public void theCurrentProjectCountIs(Integer projectCount) {
        assertEquals(timeManager.getProjects().size(), projectCount);
    }

    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        this.projectName = projectName;
        try {
            timeManager.createProject(projectName);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(timeManager.getProjects().stream()
                .anyMatch(p -> p.getName().equals(projectName)));
    }

    @Then("the project is assigned a project id {string}")
    public void theProjectIsAssignedAProjectId(String projectId) {
        assertTrue(timeManager.getProjects().stream()
                .anyMatch(p -> p.getProjectId().equals(projectId)));
    }

    @Given("a project with name {string} exists in the system")
    public void aProjectWithNameExistsInTheSystem(String projectName) {
        try {
            timeManager.createProject(projectName);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project with name {string} is not created")
    public void theProjectIsNotCreated(String projectName) {
        assertFalse(timeManager.getProjects().stream()
                .anyMatch(p -> p.getName().equals(projectName)));
    }

    @Then("the project error message {string} is given")
    public void theProjectErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
