package acceptance_tests;

import dtu.timemanager.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ViewActivitySteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;

    private Activity activity;
    private User workingUser;
    private TimeRegistration test_time_registration;
    private LocalDate startDate;
    private LocalDate endDate;
    private double expectedHours;
    private ProjectHolder projectHolder;
    private Project project;
    private ActivityHolder activityHolder;
    private Map<String, Object> info;

    public ViewActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
        this.activityHolder = activityHolder;
        this.activity = activityHolder.getActivity();
    }

    @Given("the activity has the start date {string} and end date {string}")
    public void theActivityHasTheStartDateAndEndDate(String startDate, String endDate) throws Exception {
        this.activity.setActivityStartTime(LocalDate.parse(startDate));
        this.activity.setActivityEndTime(LocalDate.parse(endDate));
    }
    @Given("the activity has the expected hours {string}")
    public void theActivityHasTheExpectedHours(String expectedHours) {
        this.activity.setExpectedWorkHours(Double.parseDouble(expectedHours));
    }
    @Given("the activity has no defined time interval")
    public void theActivityHasNoDefinedTimeInterval() {
        assertEquals(activity.getTimeInterval(), " - ");
    }
    @When("select the activity with name {string}")
    public void selectTheActivityWithName(String activityName) {
        assertEquals(activityName, activity.getActivityName());
    }
    @Then("the name of the activity is shown")
    public void theNameOfTheActivityIsShown() {
        this.info = this.activity.viewActivity();
        assertNotNull(info.get("Name"));
    }
    @Then("the expected hours is shown")
    public void theExpectedHoursIsShown() {
        assertNotNull(info.get("ExpectedWorkHours"));
    }
    @Then("the number of work hours of {string} hours is spent on {string} is shown")
    public void theNumberOfWorkHoursOfHoursIsSpentOnIsShown(String assignedWorkHours, String activityName) throws Exception {
        this.workingUser = new User("huba");
        this.test_time_registration = new TimeRegistration(this.workingUser, this.activity, Integer.parseInt(assignedWorkHours), LocalDate.now());
        Map <String, Object> info = this.activity.viewActivity();
        assertEquals(Double.parseDouble(assignedWorkHours), info.get("WorkedHours"));
    }

    @Then("the assigned users are shown")
    public void theAssignedUsersAreShown() {
        assertNotNull(this.activity.viewActivity().get("Assigned employees"));
    }
    @Then("the users who have worked on the activity are shown")
    public void theUsersWhoHaveWorkedOnTheActivityAreShown() {
        assertNotNull(this.activity.viewActivity().get("Contributing employees"));
    }
    @Then("time interval {string} is shown for the activity")
    public void timeIntervalIsShownForTheActivity(String timeInterval) {
        assertEquals(this.activity.viewActivity().get("Time interval"), timeInterval);
    }
}
