package acceptance_tests;


import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateProjectReportSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private Map<String, Object> reportVariables = new HashMap<>();
    private ProjectHolder projectHolder;
    private Project project;

    public GenerateProjectReportSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
    }

    @Given("the project has an activity named {string}")
    public void theProjectHasAnActivityNamed(String activityName) throws Exception {
        project.addActivity(new Activity(activityName));
    }
    @When("the user generates the project report for the project")
    public void theUserGeneratesTheProjectReportForTheProject() {
        this.reportVariables = timeManager.getProjectReport(project); // If no exception is thrown, the option is available
    }
    @Then("the project report is generated showing both the time spent for each activity in the project and the total time spent on the project")
    public void theProjectReportIsGeneratedShowingBothTheTimeSpentForEachActivityInTheProjectAndTheTotalTimeSpentOnTheProject() {
        boolean keysExist = this.reportVariables.containsKey("Project ID") && this.reportVariables.containsKey("Project Name") && this.reportVariables.containsKey("Project Activities") && this.reportVariables.containsKey("Expected hours") && this.reportVariables.containsKey("Worked hours");
        assertTrue(keysExist);
    }
    @Then("the activities' time intervals")
    public void theActivitiesTimeIntervals() {
        boolean keysExist = this.reportVariables.containsKey("Activity intervals");
        assertTrue(keysExist);
    }
    @Then("the users assigned to activities in the project")
    public void theUsersAssignedToActivitiesInTheProject() {
        boolean keysExist = this.reportVariables.containsKey("Assigned employees");
        assertTrue(keysExist);
    }
    @Then("the users who have worked on activities in the project")
    public void theUsersWhoHaveWorkedOnActivitiesInTheProject() {
        boolean keysExist = this.reportVariables.containsKey("Contributing employees");
        assertTrue(keysExist);
    }
}
