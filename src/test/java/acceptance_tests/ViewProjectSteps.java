package acceptance_tests;

import dtu.time_manager.app.domain.Activity;
import dtu.time_manager.app.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ViewProjectSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private Map<String, Object> projectVariables = new HashMap<>();

    public ViewProjectSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @Given("a project with project ID {string} and project name {string} and time interval {string} exists in the system")
    public void aProjectWithProjectIDAndProjectNameAndTimeIntervalExistsInTheSystem(String projectID, String projectName, String timeInterval) {
        timeManager.addProject(timeManager.createExampleProject(projectName, 1));
    }

    @When("the user views the project with project ID {string}")
    public void theUserViewsTheProjectWithProjectID(String projectID) {
        try {
            this.projectVariables = timeManager.viewProject(projectID); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project name {string} is shown")
    public void theProjectNameIsShown(String projectName) {
        assertEquals(projectName, this.projectVariables.get("Project name"));
    }

    @Then("the project ID {string} is shown")
    public void theProjectIDIsShown(String projectID) {
        assertEquals(projectID, this.projectVariables.get("Project ID"));
    }
    @Then("time interval {string} is shown")
    public void timeIntervalIsShown(String projectInterval) {
        assertEquals(projectInterval, this.projectVariables.get("Project interval"));
    }
    @Then("the activities in the project with project ID {string} is shown")
    public void theActivitiesInTheProjectWithProjectIDIsShown(String string) {
        Object activities = this.projectVariables.get("Project activities");
        assert activities instanceof List; // Check that it's a list that is returned
        List<?> activityList = (List<?>) activities; // Check that each element in the list is of type Activity
        for (Object activity : activityList) {
            assert activity instanceof Activity;}

    }
    @Then("the option for generating a project report for the project with project ID {string} is shown")
    public void theOptionForGeneratingAProjectReportForTheProjectWithProjectIDIsShown(String projectID) {
        try {
            timeManager.getProjectReport(projectID); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
}
