package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ViewProjectSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;
    private Map<String, Object> projectVariables = new HashMap<>();

    public ViewProjectSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
    }

    @Given("a project, {string}, exists in the system")
    public void aProjectExistsInTheSystem(String projectName) throws Exception {
        this.project = timeManager.createExampleProject(projectName, 0);
        this.projectHolder.setProject(project);
        timeManager.addProject(this.project);
    }
    @Given("the project has the start date {string} and end date {string}")
    public void theProjectHasTheStartDateAndEndDate(String startDate, String endDate) throws Exception {
        try {
            this.project.setProjectStartDate(LocalDate.parse(startDate));
            this.project.setProjectEndDate(LocalDate.parse(endDate));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }

    }
    @Given("project ID {string}")
    public void projectID(String projectID) {
        assertEquals(this.project.getProjectID(), projectID);
    }
    @Then("the activities in the project are shown")
    public void theActivitiesInTheProjectAreShown() {
        Object activities = this.projectVariables.get("Project activities");
        assert activities instanceof List; // Check that it's a list that is returned
        List<?> activityList = (List<?>) activities; // Check that each element in the list is of type Activity
        for (Object activity : activityList) {
            assert activity instanceof Activity;}
    }

    @When("the user views the project")
    public void theUserViewsTheProject() {
        try {
            this.projectVariables = timeManager.viewProject(project); // If no exception is thrown, the option is available
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
    @Then("the option for generating a project report for the project is shown")
    public void theOptionForGeneratingAProjectReportForTheProjectIsShown() {
        try {
            timeManager.getProjectReport(project); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
}
