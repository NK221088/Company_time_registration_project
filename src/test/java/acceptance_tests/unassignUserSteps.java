package acceptance_tests;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class unassignUserSteps {

    private String testUserInitials;
    private User testUser;
    private Activity testActivity1;
    private Activity testActivity2;
    private String errorMessage;

    @Given("that the user with initials: {string} is assigned the activity: {string}")
    public void thatTheUserWithInitialsIsAssignedTheActivity(String userInitials, String activityName) {
        this.testUserInitials = userInitials;
        this.testUser = TimeManager.getUser(userInitials);
        this.testActivity1 = new Activity(activityName);
        this.testActivity1.assignUser(this.testUserInitials);
    }

    @When("the user tries is unassigned from the activity")
    public void theUserTriesIsUnassignedFromTheActivity() {
        this.testActivity1.unassignUser(this.testUserInitials);
    }

    @Then("the user is no longer assigned to the activity")
    public void theUserIsNoLongerAssignedToTheActivity() {
        assertFalse(this.testActivity1.getAssignedUsers().contains(this.testUser));
    }

    @Given("that the user with initials: {string} is not assigned the activity: {string}")
    public void thatTheUserWithInitialsIsNotAssignedTheActivity(String userInitials, String activityName) {
        this.testUserInitials = userInitials;
        this.testUser = TimeManager.getUser(userInitials);
        this.testActivity2 = new Activity(activityName);
    }

    @When("the user tried to be unassigned from the activity")
    public void theUserTriedToBeUnassignedFromTheActivity() {
        try {
            this.testActivity2.unassignUser(this.testUserInitials);
        } catch (Exception e) {
            this.errorMessage = e.getMessage();
        }
    }

    @Then("the error message {string} is displayed")
    public void theErrorMessage(String errorMessage) {
        assertEquals(this.errorMessage, errorMessage);
    }

}
