package acceptance_tests;

import dtu.time_manager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class AddProjectSteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private String projectName;
    private Project project;

    public AddProjectSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user with initials {string} is logged in")
    public void aUserWithInitialsIsLoggedIn(String userInitials) {
        try {
            timeManager.addUser(new User(userInitials));
            timeManager.login(userInitials);
            assertNotNull(timeManager.getCurrentUser(), "User should be logged in");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("the current year is {int}")
    public void theCurrentYearIs(Integer year) {
        // This is now handled by the TimeManagerService internally
        assertEquals(2025, year, "Current year should be 2025");
    }

    @Given("the current project count is {int}")
    public void theCurrentProjectCountIs(Integer projectCount) {
        assertEquals(projectCount, timeManager.getProjects().size(), 
            "Current project count should match expected count");
    }

    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        this.projectName = projectName;
        try {
            this.project = timeManager.createProject(projectName);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(timeManager.getProjects().stream()
                .anyMatch(p -> p.getName().equals(projectName)),
                "Project with name " + projectName + " should exist");
    }

    @Then("the project is assigned a project id {string}")
    public void theProjectIsAssignedAProjectId(String projectId) {
        assertTrue(timeManager.getProjects().stream()
                .anyMatch(p -> p.getProjectId().equals(projectId)),
                "Project with ID " + projectId + " should exist");
    }

    @Given("a project with name {string} exists in the system")
    public void aProjectWithNameExistsInTheSystem(String projectName) {
        try {
            this.project = timeManager.createProject(projectName);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project with name {string} is not created")
    public void theProjectIsNotCreated(String projectName) {
        assertFalse(timeManager.getProjects().stream()
                .anyMatch(p -> p.getName().equals(projectName)),
                "Project with name " + projectName + " should not exist");
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage(),
            "Error message should match expected message");
    }
}
