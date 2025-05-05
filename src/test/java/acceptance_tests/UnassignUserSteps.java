package acceptance_tests;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class UnassignUserSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
//    private String testUserInitials;
//    private User testUser;
//    private Activity testActivity1;
//    private Activity testActivity2;

    public UnassignUserSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

//    @Given("that the user with initials: {string} is assigned the activity: {string}")
//    public void thatTheUserWithInitialsIsAssignedTheActivity(String userInitials, String activityName) {
//        testUser = timeManager.getUserFromInitials(userInitials);
//        testActivity1 = new Activity(activityName);
//        testActivity1.assignUser(testUser);
//    }

//    @Given("that the user with initials: {string} is not assigned the activity: {string}")
//    public void thatTheUserWithInitialsIsNotAssignedTheActivity(String userInitials, String activityName) {
//        testUser = timeManager.getUserFromInitials(userInitials);
//        testActivity2 = new Activity(activityName);
//    }

    @When("the user {string} is unassigned from the activity {string}")
    public void theUserIsUnassignedFromTheActivity(String userInitials, String activityName) {
        User user = timeManager.getUserFromInitials(userInitials);
        Project project = timeManager.getProjectFromName("Project 1");
        Activity activity = project.getActivityFromName(activityName);

        try {
            timeManager.unassignUser(activity, user);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
}
