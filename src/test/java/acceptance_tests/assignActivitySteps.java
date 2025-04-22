package acceptance_tests;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class assignActivitySteps {
    private ErrorMessageHolder errorMessage;
    Integer projectCountBeforeAssign;

    @Given("a user with initials {string} and a user with initials {string} is registered in the system")
    public void aUserWithInitialsAndAUserWithInitialsIsRegisteredInTheSystem(String userInitials1, String userInitials2) {
        TimeManager.addUser(new User(userInitials2));
        List<User> users = TimeManager.getUsers();
        User user1 = TimeManager.getUser(userInitials1);
        User user2 = TimeManager.getUser(userInitials2);
        boolean contains = users.contains(user1) && users.contains(user2);
        assertTrue(contains);
    }
    @Given("the user with initials {string} is logged in")
    public void theUserWithInitialsIsLoggedIn(String userInitials) {
        User user = TimeManager.getUser(userInitials);
        assertEquals(TimeManager.getCurrentUser(), user);
    }
    @When("a the user with initials {string} assigns the user with initials {string} to an activity named {string} in the project named {string}")
    public void aTheUserWithInitialsAssignsTheUserWithInitialsToAnActivityNamedInTheProjectNamed(String userInitials1, String userInitials2, String activityName, String projectName) {
        User user = TimeManager.getUser(userInitials2);
        this.projectCountBeforeAssign = user.getActivityCount();
        Project project = TimeManager.getProjectFromName(projectName);
        Activity activity = project.getActivityFromName(activityName);
        try {
            activity.assignUser(userInitials2); // If no exception is thrown, the option is available
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("the user with initials {string} is assigned to the activity named {string} in the project named {string}")
    public void theUserWithInitialsIsAssignedToTheActivityNamedInTheProjectNamed(String userInitials, String activityName, String projectName) {
        Project project = TimeManager.getProjectFromName(projectName);
        Activity activity = project.getActivityFromName(activityName);
        List<User> users = activity.getUsers();
        User user = TimeManager.getUser(userInitials);
        assertTrue(users.contains(user));
    }
    @Then("the user with initials {string}'s count of assigned activities is incremented")
    public void theUserWithInitialsSCountOfAssignedActivitiesIsIncremented(String userInitials) {
        User user = TimeManager.getUser(userInitials);
        assertEquals(projectCountBeforeAssign+1, user.getActivityCount());
    }
}
