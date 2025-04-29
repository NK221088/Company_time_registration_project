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

public class generateProjectReportSteps {
    private ErrorMessageHolder errorMessage;
    Map<String, Object> reportVariables = new HashMap<>();

    @Given("that the project with project ID {string} has an activity named {string}")
    public void thatTheProjectWithProjectIDHasAnActivityNamed(String projectID, String activityName) {
        Project project = TimeManager.getProjectFromID(projectID);
        // Returns null if activity doesn't exist in project
        assertNotNull(project.getActivityFromName(activityName));
    }

    @When("the user generates the project report for the project with project ID {string}")
    public void theUserGeneratesTheProjectReportForTheProjectWithProjectID(String projectID) {
        this.reportVariables = TimeManager.getProjectReport(projectID); // If no exception is thrown, the option is available
    }
    @Then("the project report is generated showing both the time spent for each activity in the project with project ID {string} and the total time spent on the project")
    public void theProjectReportIsGeneratedShowingBothTheTimeSpentForEachActivityInTheProjectWithProjectIDAndTheTotalTimeSpentOnTheProject(String projectID) {
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
