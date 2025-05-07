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

    public AssignActivitySteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @Given("the user {string} has {int} assigned activities")
    public void theUserHasAssignedActivities(String userInitials, Integer activityCount) {
        User user = new User(userInitials);
        timeManager.addUser(user);
        Project projectWithActivities = timeManager.createExampleProject("Project With " + activityCount.toString() + " Activities", activityCount);
        timeManager.addProject(projectWithActivities);
        for (Activity activity : projectWithActivities.getActivities()) { timeManager.assignUser(activity, user); }

        assertEquals(activityCount, user.getActivityCount());
    }
    @When("the user {string} assigns the user {string} to {string} in {string}")
    public void theUserAssignsTheUserToIn(String userInitials1, String userInitials2, String activityName, String projectName) {
        Project project = timeManager.getProjectFromName(projectName);
        Activity activity = project.getActivityFromName(activityName);
        User user = timeManager.getUserFromInitials(userInitials2);

        try {
            timeManager.assignUser(activity, user); // If no exception is thrown, the option is available
        } catch (Exception e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the user {string} is assigned to {string} in {string}")
    public void theUserIsAssignedToIn(String userInitials, String activityName, String projectName) {
        Project project = timeManager.getProjectFromName(projectName);
        Activity activity = project.getActivityFromName(activityName);
        User user = timeManager.getUserFromInitials(userInitials);

        assertTrue(activity.getAssignedUsers().contains(user));
    }
    @Then("the user {string}'s count of currently assigned activities is {int}")
    public void theUserSCountOfCurrentlyAssignedActivitiesIs(String userInitials, Integer activityCount) {
        User user = timeManager.getUserFromInitials(userInitials);
        assertEquals(activityCount, user.getActivityCount());
    }

    @Given("the user {string} is already assigned to {string} in {string}")
    public void theUserIsAlreadyAssignedToIn(String userInitials, String activityName, String projectName) {
        Project project = timeManager.getProjectFromName(projectName);
        Activity activity = project.getActivityFromName(activityName);
        User user = timeManager.getUserFromInitials(userInitials);

        try {
            timeManager.assignUser(activity, user); // If no exception is thrown, the option is available
        } catch (Exception e) {
            errorMessage.setErrorMessage(e.getMessage());
        }
    }

    @Given("a project named {string} with an activity named {string} exists in the system")
    public void aProjectNamedWithAnActivityNamedExistsInTheSystem(String projectName, String activityName) throws Exception {
        Project project = timeManager.createProject(projectName);
        project.addActivity(new Activity(activityName));
        timeManager.addProject(project);
    }

    @Then("the user {string} isn't assigned to {string} in {string}")
    public void theUserIsnTAssignedToIn(String userInitials, String activityName, String projectName) {
        Project project = timeManager.getProjectFromName(projectName);
        Activity activity = project.getActivityFromName(activityName);
        User user = timeManager.getUserFromInitials(userInitials);

        assertFalse(activity.getAssignedUsers().contains(user));
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
