package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

// Nikolai Kuhl
public class UnassignUserSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;
    private ActivityHolder activityHolder;
    private Activity activity;
    private User user;

    public UnassignUserSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = this.projectHolder.getProject();
        this.activityHolder = activityHolder;
        this.activity = this.activityHolder.getActivity();
    }

    @When("the user {string} is unassigned from the activity")
    public void theUserIsUnassignedFromTheActivity(String userInitials) throws Exception {
        boolean initialsFound = timeManager.getUsers().stream()
                .anyMatch(user -> user.getUserInitials().equals(userInitials));
        if (!initialsFound){
            this.user = new User(userInitials);
            timeManager.addUser(user);
        }
        try {
            this.user = timeManager.getUserFromInitials("huba");
            timeManager.unassignUser(activity, user);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the user isn't assigned to the activity")
    public void theUserIsnTAssignedToTheActivity() {
        assertFalse(activity.getAssignedUsers().contains(user));
    }
    @Then("the user's count of currently assigned activities is {int}")
    public void theUserSCountOfCurrentlyAssignedActivitiesIs(Integer activityCount) {
        assertEquals(activityCount, user.getActivityCount());
    }
}
