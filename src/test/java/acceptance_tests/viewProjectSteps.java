package acceptance_tests;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class viewProjectSteps {
    private ErrorMessageHolder errorMessage;
    private Project project;
    Map<String, Object> projectVariables = new HashMap<>();


    public viewProjectSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
        this.projectVariables = new HashMap<>(); // Initializes the map to be empty
    }

    @Given("a project with project ID {string} exists in the system")
    public void aProjectWithProjectIDExistsInTheSystem(String ProjectID) {
        ProjectHelper projectHelper = new ProjectHelper();
        this.project = projectHelper.exampleProject(ProjectID);
        TimeManager.addProject(this.project);
        assertNotNull(TimeManager.getProjectFromID(ProjectID));
    }

    @When("the user views the project with project ID {string}")
    public void theUserViewsTheProjectWithProjectID(String projectID) {
        try {
            this.projectVariables = TimeManager.viewProject(projectID); // If no exception is thrown, the option is available
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
            TimeManager.getProjectReport(projectID); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
}
