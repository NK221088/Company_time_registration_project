package acceptance_tests;

import dtu.time_manager.app.*;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class viewActivitySteps {

    private Project test_project;
    private Activity activity;
    private User workingUser;
    private User assignedUser;
    private TimeRegistration test_time_registration;
    private double workedHours;


    @When("select an activity with name {string} from project ID {string}")
    public void selectAnActivityWithNameFromProjectID(String activityName, String projectID) {
        this.test_project = TimeManager.getProjectFromID(projectID);
        this.activity = new Activity(activityName);
        this.activity.addWorkingUser(this.workingUser);



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
        this.test_time_registration = new TimeRegistration(this.workingUser, this.activity, Integer.parseInt(assignedWorkHours), LocalDate.now());
        Map <String, Object> info = this.activity.viewActivity();
        assertEquals(Double.parseDouble(assignedWorkHours), info.get("WorkedHours"));
    }
//    @Then("the start date is shown")
//    public void theStartDateIsShown() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }
//    @Then("the end date is shown")
//    public void theEndDateIsShown() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    @Then("the assigned users are shown")
//    public void theAssignedUsersAreShown() {
//        assertNotNull(this.projectVariables.get("Assigned employees"));
//    }
//
//    @Then("the users who have worked on the project are shown")
//    public void theUsersWhoHaveWorkedOnTheProjectAreShown() {
//        assertNotNull(this.projectVariables.get("Contributing users"));
//    }

    @Then("the initials of the developer or developers {string} working on the {string} is shown")
    public void theInitialsOfTheDeveloperOrDevelopersWorkingOnTheIsShown(String user, String activityName) {
        this.activity.assignUser(user);
        Map <String, Object> info = this.activity.viewActivity();
        ArrayList<User> userList = (ArrayList<User>) info.get("Assigned employees");
        assertTrue(userList.contains(TimeManager.getUser(user)));
    }

}
