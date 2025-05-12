package acceptance_tests;


import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.ProjectReport;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Nikolai Kuhl
public class GenerateProjectReportSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectReport projectReport;
    private ProjectHolder projectHolder;
    private Project project;
    private ActivityHolder activityHolder;

    public GenerateProjectReportSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
        this.activityHolder = activityHolder;
    }

    @Given("the project has an activity named {string}")
    public void theProjectHasAnActivityNamed(String activityName) throws Exception {
        Activity activity = new Activity(activityName);
        project.addActivity(activity);
        activityHolder.setActivity(activity);
    }
    @Given("the project has a project lead {string}")
    public void theProjectHasAProjectLead(String string) {
        project.setProjectLead(timeManager.getCurrentUser());
    }
    @When("the user generates the project report for the project")
    public void theUserGeneratesTheProjectReportForTheProject() {
        this.projectReport = timeManager.getProjectReport(project); // If no exception is thrown, the option is available
    }
    @Then("the project report is generated showing both the time spent for each activity in the project and the total time spent on the project")
    public void theProjectReportIsGeneratedShowingBothTheTimeSpentForEachActivityInTheProjectAndTheTotalTimeSpentOnTheProject() {
        boolean gettersExist =
            this.projectReport.getProjectID() != null &&
            this.projectReport.getProjectName() != null &&
            this.projectReport.getActivities() != null &&
            this.projectReport.getExpectedHours() != null &&
            this.projectReport.getWorkedHours() != null;
        assertTrue(gettersExist);
    }
    @Then("the activities' time intervals")
    public void theActivitiesTimeIntervals() {
        assertTrue(this.projectReport.getActivityIntervals() != null);
    }
    @Then("the users assigned to activities in the project")
    public void theUsersAssignedToActivitiesInTheProject() {
        assertTrue(this.projectReport.getAssignedUsers() != null);
    }
    @Then("the project lead")
    public void theProjectLead() {
        assertTrue(this.projectReport.getProjectLead() != null);
    }
    @Then("the users who have worked on activities in the project")
    public void theUsersWhoHaveWorkedOnActivitiesInTheProject() {
        assertTrue(this.projectReport.getContributingEmployees() != null);
    }
}
