package acceptance_tests;

import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        Project project1 = timeManager.createExampleProject("Project To Test Count 1", 1);
        Project project2 = timeManager.createExampleProject("Project To Test Count 2", 1);
        timeManager.addProject(project1);
        timeManager.addProject(project2);
        assertEquals(timeManager.getProjectCount(), projectCount);
    }
    @When("a new project with name {string} is added")
    public void aNewProjectWithNameIsAdded(String projectName) {
        try {
            this.project = timeManager.createProject(projectName);
            timeManager.addProject(project);

        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("a project named {string} should exist in the system")
    public void aProjectNamedShouldExistInTheSystem(String projectName) {
        assertTrue(timeManager.projectExists(project));
    }

    @Then("the project is assigned a project id {string}")
    public void theProjectIsAssignedAProjectId(String projectID) {
        assertEquals(project.getProjectID(), projectID);
    }

    @Then("the new project with name {string} is not created")
    public void theNewProjectWithNameIsNotCreated(String projectName) {
        List<Project> projects = timeManager.getProjects();
        long counted = projects.stream()
                .map(Project::getProjectName)
                .filter(name -> name.equals(projectName))
                .count();
        assertFalse(counted > 1);
    }


}
