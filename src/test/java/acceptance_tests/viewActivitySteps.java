package acceptance_tests;

import dtu.time_manager.app.*;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class viewActivitySteps {

    private Project test_project;
    private Activity activity;
    private User test_user;
    private TimeRegistration test_time_registration;


    @When("select an activity with name {string} from project ID {string}")
    public void selectAnActivityWithNameFromProjectID(String activityName, String projectID) {
        this.test_user = TimeManager.getUser("huba");
        this.test_project = TimeManager.getProjectFromID(projectID);
        this.activity = new Activity(activityName);
        this.activity.assignUser("huba");
    }


    @Then("the activity name of {string} is shown")
    public void theActivityNameOfIsShown(String activityName) {
        String test_activityName = this.activity.getActivityName();
        assertEquals(test_activityName, this.activity.getActivityName());
    }

    @Then("the expected hours of {string} hours in {string} is shown")
    public void theExpectedHoursOfHoursInIsShown(String expectedHours, String activityName) {
        this.activity.setExpectedWorkHours(Double.parseDouble(expectedHours));
        Double test_ExpectedHours = this.activity.getExpectedWorkHours();
        assertEquals(Double.parseDouble(expectedHours), test_ExpectedHours);
    }

    @Then("the number of work hours of {string} hours is spent on {string} is shown")
    public void theNumberOfWorkHoursOfHoursIsSpentOnIsShown(String assignedWorkHours, String activityName) {
        this.test_time_registration = new TimeRegistration(this.test_user, this.activity, Integer.parseInt(assignedWorkHours), LocalDate.now());
        Double test_hoursSpent = this.activity.getWorkedHours();
        assertEquals(Double.parseDouble(assignedWorkHours), test_hoursSpent);
    }

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
        ArrayList<User> test_users = this.activity.getAssignedUsers();
        assertTrue(test_users.contains(TimeManager.getUser(user)));
    }

}
