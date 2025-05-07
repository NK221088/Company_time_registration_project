package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddActivitySteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;

    public AddActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
    }

    @When("adds an activity named {string} to the project")
    public void addsAnActivityNamedToTheProject(String activityName) {
        this.project = projectHolder.getProject();
        Activity activity = new Activity(activityName);
        try {
            project.addActivity(activity);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the activity named {string} should be added to the project")
    public void theActivityNamedShouldBeAddedToTheProject(String activityName) {
        List<Activity> activities = project.getActivities();
        assertTrue(activities.stream().map(Activity::getActivityName).anyMatch(name -> name.equals(activityName)));
    }
    @Then("the activity named {string} should be shown when the project named {string} is viewed")
    public void theActivityNamedShouldBeShownWhenTheProjectNamedIsViewed(String activityName, String projectName) {
        Project project = timeManager.getProjectFromName(projectName);
        String projectID = project.getProjectID();
        Map<String, Object> projectVariables = timeManager.viewProject(project);
        List<Activity> activities = (List<Activity>) projectVariables.get("Project activities");
        assertTrue(activities.stream().map(Activity::getActivityName).anyMatch(name -> name.equals(activityName)));

    }

    @Given("that the project with project ID {string} have a registered activity with name {string}")
    public void thatTheProjectWithProjectIDHaveARegisteredActivityWithName(String projectID, String activityName) {
        Project project = timeManager.getProjectFromID(projectID);
        Activity activity = new Activity(activityName);
        try {
            project.addActivity(activity);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the activity should not be added to the project")
    public void theActivityShouldNotBeAddedToTheProject() {
        assertFalse(errorMessage.getErrorMessage().isEmpty());
    }

    @Then("the activity error message {string} is given")
    public void theActivityErrorMessageIsGiven(String expectedErrorMessage) {
        assertEquals(expectedErrorMessage, errorMessage.getErrorMessage());
    }



}
