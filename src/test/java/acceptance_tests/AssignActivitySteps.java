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
    private ActivityHolder activityHolder;
    private Activity activity;

    public AssignActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage, ProjectHolder projectHolder, ActivityHolder activityHolder) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
        this.projectHolder = projectHolder;
        this.user1 = timeManager.getCurrentUser();
        this.project = projectHolder.getProject();
        this.activityHolder = activityHolder;
        this.activity = activityHolder.getActivity();

    }

    @Given("the user {string} has {int} assigned activities")
    public void theUserHasAssignedActivities(String userInitials, Integer activityCount) throws Exception {
        boolean initialsFound = timeManager.getUsers().stream()
                .anyMatch(user -> user.getUserInitials().equals(userInitials));
        if (!initialsFound){
            this.user1 = new User(userInitials);
            timeManager.addUser(user1);
        }
        else {this.user1 = timeManager.getUserFromInitials(userInitials);}
        Project projectWithActivities = timeManager.createExampleProject("Project With " + activityCount.toString() + " Activities", activityCount);
        timeManager.addProject(projectWithActivities);
        for (Activity activity : projectWithActivities.getActivities()) { timeManager.assignUser(activity, user1); }
        assertEquals(activityCount, user1.getActivityCount());
    }
    @Given("the user {string} has {int} assigned activity, {string} in {string}")
    public void theUserHasAssignedActivityIn(String userInitials, Integer int1, String string2, String string3) throws Exception {
        boolean initialsFound = timeManager.getUsers().stream()
                .anyMatch(user -> user.getUserInitials().equals(userInitials));
        if (!initialsFound){
            this.user1 = new User(userInitials);
            timeManager.addUser(user1);
        }
        this.activity.assignUser(user1);
    }
    @When("the user {string} assigns the user {string} to the activity {string} in {string}")
    public void theUserAssignsTheUserToTheActivityIn(String string, String string2, String string3, String string4) {
        try {
            activity.assignUser(user1);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the user {string} is assigned to {string} in {string}")
    public void theUserIsAssignedToIn(String userInitials, String activityName, String projectName) {
        assertTrue(activity.getAssignedUsers().contains(user1));
    }
    @Then("the user {string}'s count of currently assigned activities is {int}")
    public void theUserSCountOfCurrentlyAssignedActivitiesIs(String userInitials, Integer activityCount) {
        assertEquals(activityCount, user1.getActivityCount());
    }

    @Then("the user {string} isn't assigned to {string} in {string}")
    public void theUserIsnTAssignedToIn(String userInitials, String activityName, String projectName) {
        Activity activity = project.getActivityFromName(activityName);
        assertFalse(activity.getAssignedUsers().contains(user1));
    }
    @Then("the user {string} isn't assigned to {string} in {string} again")
    public void theUserIsnTAssignedToInAgain(String userInitials, String string2, String string3) {
        long count = activity.getAssignedUsers().stream()
                .filter(user -> user.getUserInitials().equals(userInitials))
                .count();
        assertTrue(count == 1);
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
