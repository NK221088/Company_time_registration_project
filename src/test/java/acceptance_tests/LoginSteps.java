package acceptance_tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dtu.example.ui.*;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class LoginSteps {
    private User user;
    private String user_initials;
    private TimeManager time_manager;

    public LoginSteps() {
        this.time_manager = new TimeManager();
    }

    @Given("a users initials {string} is registered in the system")
    public void aUsersInitialsIsRegisteredInTheSystem(String user_initials) {
        this.user_initials = user_initials;
        user = new User(user_initials);
    }
    @When("the user types in their initials {string}")
    public void theUserTypesInTheirInitials(String user_initials) {
        assertTrue(TimeManager.login(user_initials));
    }
    @Then("they are logged into the system")
    public void theyAreLoggedIntoTheSystem() {
    }
}