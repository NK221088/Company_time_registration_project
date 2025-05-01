package acceptance_tests;

import dtu.time_manager.app.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class viewActivitySteps {

    private Project test_project;
    private Activity activity;
    private User workingUser;
    private User assignedUser;
    private TimeRegistration test_time_registration;
    private double workedHours;
    private LocalDate startDate;
    private LocalDate endDate;

    @Given("the activity has the start date {string} and end date {string}")
    public void theActivityHasTheStartDateAndEndDate(String startDate, String endDate) {
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
    }

    @When("select an activity with name {string} from project ID {string}")
    public void selectAnActivityWithNameFromProjectID(String activityName, String projectID) {
        this.test_project = TimeManager.getProjectFromID(projectID);
        this.activity = new Activity(activityName);


    }


    @Then("the activity name of {string} is shown")
    public void theActivityNameOfIsShown(String activityName) {
        this.activity.setActivityName(activityName);
        Map<String, Object> info = this.activity.viewActivity();
        assertEquals(info.get("Name"), activityName);
    }

    @Then("the expected hours of {string} hours in {string} is shown")
    public void theExpectedHoursOfHoursInIsShown(String expectedHours, String activityName) {
        this.activity.setExpectedWorkHours(Double.parseDouble(expectedHours));
        Map<String, Object> info = this.activity.viewActivity();
        assertEquals(Double.parseDouble(expectedHours), info.get("ExpectedWorkHours"));
    }

    @Then("the number of work hours of {string} hours is spent on {string} is shown")
    public void theNumberOfWorkHoursOfHoursIsSpentOnIsShown(String assignedWorkHours, String activityName) {
        this.workingUser = new User("huba");
        this.test_time_registration = new TimeRegistration(this.workingUser, this.activity, Integer.parseInt(assignedWorkHours), LocalDate.now());
        Map <String, Object> info = this.activity.viewActivity();
        assertEquals(Double.parseDouble(assignedWorkHours), info.get("WorkedHours"));
    }
   @Then("the start date is shown")
    public void theStartDateIsShown() {
        this.activity.setActivityStartTime(this.startDate);
        Map<String, Object> info = this.activity.viewActivity();
        assertEquals(this.activity.getActivityStartTime(), info.get("StartTime"));


    }
    @Then("the end date is shown")
    public void theEndDateIsShown() {
        this.activity.setActivityEndTime(this.endDate);
        Map<String, Object> info = this.activity.viewActivity();
        assertEquals(this.activity.getActivityEndTime(), info.get("EndTime"));
    }

    @Then("the initials of the developer or developers {string} working on the {string} is shown")
    public void theInitialsOfTheDeveloperOrDevelopersWorkingOnTheIsShown(String user, String activityName) {
        this.activity.assignUser(user);
        this.activity.addWorkingUser(TimeManager.getUser(user));
        Map <String, Object> info = this.activity.viewActivity();
        ArrayList<User> userList = (ArrayList<User>) info.get("Assigned employees");
        assertTrue(userList.contains(TimeManager.getUser(user)));
    }

    @Then("the assigned users are shown")
    public void theAssignedUsersAreShown() {
        Map<String, Object> info = this.activity.viewActivity();
        assertNotNull(info.get("Assigned employees"));
    }

    @Then("the users who have worked on the project are shown")
    public void theUsersWhoHaveWorkedOnTheProjectAreShown() {
        Map<String, Object> info = this.activity.viewActivity();
        assertNotNull(info.get("Contributing employees"));
    }



}
