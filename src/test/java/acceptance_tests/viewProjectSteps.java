package acceptance_tests;

import dtu.time_manager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class viewProjectSteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private Project project;
    private Map<String, Object> projectVariables = new HashMap<>();

    public viewProjectSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a project with project ID {string} and project name {string} and time interval {string} exists in the system")
    public void aProjectWithProjectIDAndProjectNameAndTimeIntervalExistsInTheSystem(String projectId, String projectName, String timeInterval) {
        try {
            timeManager.createProject(projectName);
            this.project = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals(projectName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user views the project with project ID {string}")
    public void theUserViewsTheProjectWithProjectID(String projectId) {
        try {
            this.projectVariables = timeManager.getProjectInfo(projectId);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project name {string} is shown")
    public void theProjectNameIsShown(String projectName) {
        assertEquals(projectName, this.projectVariables.get("Project name"));
    }

    @Then("the project ID {string} is shown")
    public void theProjectIDIsShown(String projectId) {
        assertEquals(projectId, this.projectVariables.get("Project ID"));
    }

    @Then("time interval {string} is shown")
    public void timeIntervalIsShown(String projectInterval) {
        assertEquals(projectInterval, this.projectVariables.get("Project interval"));
    }

    @Then("the activities in the project with project ID {string} is shown")
    public void theActivitiesInTheProjectWithProjectIDIsShown(String projectId) {
        Object activities = this.projectVariables.get("Project activities");
        assertTrue(activities instanceof List, "Project activities should be a list");
        List<?> activityList = (List<?>) activities;
        for (Object activity : activityList) {
            assertTrue(activity instanceof Activity, "Each item in activities list should be an Activity");
        }
    }

    @Then("the option for generating a project report for the project with project ID {string} is shown")
    public void theOptionForGeneratingAProjectReportForTheProjectWithProjectIDIsShown(String projectId) {
        try {
            timeManager.generateProjectReport(projectId);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
