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
        this.project = projectHolder.getProject();
    }

    @When("adds an activity named {string} to the project")
    public void addsAnActivityNamedToTheProject(String activityName) {
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

    @Then("the activity should not be added to the project")
    public void theActivityShouldNotBeAddedToTheProject() {
        assertFalse(errorMessage.getErrorMessage().isEmpty());
    }

}
