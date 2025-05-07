package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import io.cucumber.java.en.When;

public class UnassignUserSteps {
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;

    public UnassignUserSteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = this.projectHolder.getProject();
    }

    @When("the user {string} is unassigned from the activity {string}")
    public void theUserIsUnassignedFromTheActivity(String userInitials, String activityName) {
        User user = timeManager.getUserFromInitials(userInitials);
        Activity activity = project.getActivityFromName(activityName);

        try {
            timeManager.unassignUser(activity, user);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
}
