package acceptance_tests;

import dtu.timemanager.domain.Activity;
import dtu.timemanager.domain.Project;
import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class AssignActivitySteps {
    private Integer projectCountBeforeAssign;
    private TimeManager timeManager;
    private ErrorMessageHolder errorMessage;
    private ProjectHolder projectHolder;
    private Project project;
    private User user1;
    private User user2;

    public AssignActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.project = projectHolder.getProject();
        this.user1 = timeManager.getCurrentUser();
    }

    @Given("the user {string} has {int} assigned activities")
    public void theUserHasAssignedActivities(String userInitials, Integer activityCount) {
        this.user2 = new User(userInitials);
        Project projectWithActivities = timeManager.createExampleProject("Project With " + activityCount.toString() + " Activities", activityCount);
        timeManager.addProject(projectWithActivities);
        for (Activity activity : projectWithActivities.getActivities()) { timeManager.assignUser(activity, user2); }
        assertEquals(activityCount, user2.getActivityCount());
    }
    @When("the user {string} assigns the user {string} to {string} in {string}")
    public void theUserAssignsTheUserToIn(String userInitials1, String userInitials2, String activityName, String projectName) {
        Activity activity = project.getActivityFromName(activityName);
        try {
            timeManager.assignUser(activity, user2); // If no exception is thrown, the option is available
        } catch (Exception e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the user {string} is assigned to {string} in {string}")
    public void theUserIsAssignedToIn(String userInitials, String activityName, String projectName) {
        Activity activity = project.getActivityFromName(activityName);
        assertTrue(activity.getAssignedUsers().contains(user2));
    }
    @Then("the user {string}'s count of currently assigned activities is {int}")
    public void theUserSCountOfCurrentlyAssignedActivitiesIs(String userInitials, Integer activityCount) {
        assertEquals(activityCount, user2.getActivityCount());
    }

    @Given("the user {string} is already assigned to {string} in {string}")
    public void theUserIsAlreadyAssignedToIn(String userInitials, String activityName, String projectName) {
        Project project = timeManager.getProjectFromName(projectName);
        Activity activity = project.getActivityFromName(activityName);
        try {
            timeManager.assignUser(activity, user2); // If no exception is thrown, the option is available
        } catch (Exception e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Then("the user {string} isn't assigned to {string} in {string}")
    public void theUserIsnTAssignedToIn(String userInitials, String activityName, String projectName) {
        Activity activity = project.getActivityFromName(activityName);
        assertFalse(activity.getAssignedUsers().contains(user2));
    }

//    @Then("the user with initials {string}'s count of assigned activities is incremented")
//    public void theUserWithInitialsSCountOfAssignedActivitiesIsIncremented(String userInitials) {
//        User user = timeManager.getUserFromInitials(userInitials);
//        assertEquals(projectCountBeforeAssign+1, user.getActivityCount());
//    }
//    @Then("the user with initials {string} is only assigned to the activity named {string} in the project named {string} once")
//    public void theUserWithInitialsIsOnlyAssignedToTheActivityNamedInTheProjectNamedOnce(String userInitials, String activityName, String projectName) {
//        Project project = timeManager.getProjectFromName(projectName);
//        Activity activity = project.getActivityFromName(activityName);
//        List<User> assignedusers = activity.getAssignedUsers();
//        User user = timeManager.getUserFromInitials(userInitials);
//        long countOfUser = assignedusers.stream()
//                .map(User::getUserInitials)
//                .filter(name -> name.equals(userInitials))
//                .count();
//        assertEquals(1, countOfUser);
//    }
}
