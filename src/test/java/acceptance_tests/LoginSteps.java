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

    /* The only purpose of this constructor is to test
     * if Cucumber Dependency Injection using Picocontainer works.
     */
    public LoginSteps() {
    }

    private String user_initials;

    @Given("a users initials {string} is registered in the system")
    public void aUsersInitialsIsRegisteredInTheSystem(String user_initials) {
        this.user_initials = user_initials;
        User user = new User(user_initials);
    }
    @When("the user types in their initials {string}")
    public void theUserTypesInTheirInitials(String user_initials) {
        assertFalse(TimeManager.login(user_initials));
    }
    @Then("they are logged into the system")
    public void theyAreLoggedIntoTheSystem() {
    }
}