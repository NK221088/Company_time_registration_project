package acceptance_tests;

import dtu.time_manager.domain.Activity;
import dtu.time_manager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class unassignUserSteps extends TestBase {
    private ErrorMessageHolder errorMessage;
    private String testUserInitials;
    private User testUser;
    private Activity testActivity1;
    private Activity testActivity2;

    public unassignUserSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("that the user with initials: {string} is assigned the activity: {string}")
    public void thatTheUserWithInitialsIsAssignedTheActivity(String userInitials, String activityName) {
        try {
            this.testUserInitials = userInitials;
            timeManager.addUser(new User(userInitials));
            this.testUser = timeManager.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals(userInitials))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create a project and activity
            timeManager.createProject("Test Project");
            Project project = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals("Test Project"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            timeManager.createActivity(project, activityName);
            this.testActivity1 = project.getActivities().stream()
                    .filter(a -> a.getName().equals(activityName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));

            timeManager.assignUserToActivity(this.testActivity1, this.testUser);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user tries is unassigned from the activity")
    public void theUserTriesIsUnassignedFromTheActivity() {
        try {
            timeManager.unassignUserFromActivity(this.testActivity1, this.testUser);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the user is no longer assigned to the activity")
    public void theUserIsNoLongerAssignedToTheActivity() {
        assertFalse(this.testActivity1.getAssignedUsers().contains(this.testUser));
    }

    @Given("that the user with initials: {string} is not assigned the activity: {string}")
    public void thatTheUserWithInitialsIsNotAssignedTheActivity(String userInitials, String activityName) {
        try {
            this.testUserInitials = userInitials;
            timeManager.addUser(new User(userInitials));
            this.testUser = timeManager.getUsers().stream()
                    .filter(u -> u.getUserInitials().equals(userInitials))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create a project and activity
            timeManager.createProject("Test Project 2");
            Project project = timeManager.getProjects().stream()
                    .filter(p -> p.getName().equals("Test Project 2"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            timeManager.createActivity(project, activityName);
            this.testActivity2 = project.getActivities().stream()
                    .filter(a -> a.getName().equals(activityName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Activity not found"));
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @When("the user tried to be unassigned from the activity")
    public void theUserTriedToBeUnassignedFromTheActivity() {
        try {
            timeManager.unassignUserFromActivity(this.testActivity2, this.testUser);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the error message {string} is displayed")
    public void theErrorMessageIsDisplayed(String errorMessage) {
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }
}
