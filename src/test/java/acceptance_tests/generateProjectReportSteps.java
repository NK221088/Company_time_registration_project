package acceptance_tests;

import dtu.time_manager.domain.Project;
import dtu.time_manager.domain.Activity;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;

public class generateProjectReportSteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private Map<String, Object> reportVariables = new HashMap<>();

    public generateProjectReportSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("that the project with project ID {string} has an activity named {string}")
    public void thatTheProjectWithProjectIDHasAnActivityNamed(String projectId, String activityName) {
        try {
            Project project = timeManager.getProjects().stream()
                    .filter(p -> p.getProjectId().equals(projectId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            Activity activity = project.getActivities().stream()
                    .filter(a -> a.getName().equals(activityName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));
            
            assertNotNull(activity);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user generates the project report for the project with project ID {string}")
    public void theUserGeneratesTheProjectReportForTheProjectWithProjectID(String projectId) {
        try {
            this.reportVariables = timeManager.generateProjectReport(projectId);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the project report is generated showing both the time spent for each activity in the project with project ID {string} and the total time spent on the project")
    public void theProjectReportIsGeneratedShowingBothTheTimeSpentForEachActivityInTheProjectWithProjectIDAndTheTotalTimeSpentOnTheProject(String projectId) {
        assertTrue(reportVariables.containsKey("Project ID"), "Project ID should be in report");
        assertTrue(reportVariables.containsKey("Project Name"), "Project Name should be in report");
        assertTrue(reportVariables.containsKey("Project Activities"), "Project Activities should be in report");
        assertTrue(reportVariables.containsKey("Expected hours"), "Expected hours should be in report");
        assertTrue(reportVariables.containsKey("Worked hours"), "Worked hours should be in report");
    }

    @Then("the activities' time intervals")
    public void theActivitiesTimeIntervals() {
        assertTrue(reportVariables.containsKey("Activity intervals"), "Activity intervals should be in report");
    }

    @Then("the users assigned to activities in the project")
    public void theUsersAssignedToActivitiesInTheProject() {
        assertTrue(reportVariables.containsKey("Assigned employees"), "Assigned employees should be in report");
    }

    @Then("the users who have worked on activities in the project")
    public void theUsersWhoHaveWorkedOnActivitiesInTheProject() {
        assertTrue(reportVariables.containsKey("Contributing employees"), "Contributing employees should be in report");
    }

    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
