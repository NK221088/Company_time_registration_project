package acceptance_tests;

import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Nikolai Kuhl
public class AddProjectSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;

    public AddProjectSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
    }

    @Given("the current year is {int}")
    public void theCurrentYearIs(Integer year) {
       assertEquals(2025, year);
    }
    @Given("the current project count is {int}")
    public void theCurrentProjectCountIs(Integer projectCount) throws Exception {
        Project project1 = timeManager.addExampleProject("Project To Test Count 1", 1);
        Project project2 = timeManager.addExampleProject("Project To Test Count 2", 1);
        assertEquals(timeManager.getProjectCount(), projectCount);
    }
    @Given("no project with project name {string} exists in the system")
    public void noProjectWithProjectNameExistsInTheSystem(String projectName) {
        Project project = new Project(projectName);
        assertFalse(timeManager.projectExists(project));
    }
    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        try {
            this.project = timeManager.addProject(projectName);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(timeManager.projectExists(project));
        assertFalse(project.equals(null));
        assertEquals(projectName, project.toString());
    }

    @Then("the project is assigned a project id {string}")
    public void theProjectIsAssignedAProjectId(String projectID) {
        assertEquals(project.getProjectID(), projectID);
    }

    @Then("the new project with name {string} is not created")
    public void theNewProjectWithNameIsNotCreated(String projectName) {
        Integer count = 0;
        for (Project project : timeManager.getProjects()) {
            if (project.getProjectName().equals(projectName)) {
                count++;
            }
        }
        assertFalse(count > 1);
    }
}
