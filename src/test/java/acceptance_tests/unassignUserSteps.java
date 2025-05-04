package acceptance_tests;

import dtu.time_manager.domain.*;
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
    private Project project;

    public unassignUserSteps(ErrorMessageHolder errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Given("a user with initials {string} is logged in")
    public void aUserWithInitialsIsLoggedIn(String userInitials) {
        try {
            timeManager.addUser(new User(userInitials));
            timeManager.login(userInitials);
            assertNotNull(timeManager.getCurrentUser(), "User should be logged in");
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
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
            this.project = timeManager.createProject("Test Project");
            this.testActivity1 = timeManager.createActivity(activityName);
            timeManager.assignActivityToProject(this.testActivity1, this.project);
            timeManager.assignUserToActivity(this.testActivity1, this.testUser);

            assertTrue(this.testActivity1.getAssignedUsers().contains(this.testUser), 
                "User should be assigned to activity");
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
        assertFalse(this.testActivity1.getAssignedUsers().contains(this.testUser), 
            "User should not be assigned to activity");
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
            this.project = timeManager.createProject("Test Project 2");
            this.testActivity2 = timeManager.createActivity(activityName);
            timeManager.assignActivityToProject(this.testActivity2, this.project);

            assertFalse(this.testActivity2.getAssignedUsers().contains(this.testUser), 
                "User should not be assigned to activity");
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
        assertEquals(errorMessage, this.errorMessage.getErrorMessage(), 
            "Error message should match expected message");
    }
}
