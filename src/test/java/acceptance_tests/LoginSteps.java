package acceptance_tests;

import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import static org.junit.jupiter.api.Assertions.*;


public class LoginSteps {
    private final TimeManager timeManager;
    private final ErrorMessageHolder errorMessage;
    private String user_initials;

    public LoginSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @Given("the user {string} is registered")
    public void theUserIsRegistered(String user_initials) {
        timeManager.addUser(new User(user_initials));
    }
    @When("the user types in their initials {string}")
    public void theUserTypesInTheirInitials(String user_initials) {
        try {
            User user = timeManager.getUserFromInitials(user_initials);
            timeManager.setCurrentUser(user);
        } catch (Exception e) {
            this.errorMessage.setErrorMessage(e.getMessage());
        }
    }
    @Then("they are logged into the system")
    public void theyAreLoggedIntoTheSystem() {
        assertNotNull(timeManager.getCurrentUser());
    }

    @Then("they aren't logged into the system")
    public void theyArenTLoggedIntoTheSystem() {
        assertNull(timeManager.getCurrentUser());
    }
    @Then("the error message {string} is given")
    public void theErrorMessageIsGiven(String errorMessage) {
        this.errorMessage.setErrorMessage(errorMessage);
        assertEquals(errorMessage, this.errorMessage.getErrorMessage());
    }

    @Given("the user {string} is logged in")
    public void theUserIsLoggedIn(String user_initials) {
        User user = new User(user_initials);
        timeManager.addUser(user);
        timeManager.setCurrentUser(user);
        assertNotNull(timeManager.getCurrentUser());
    }
}
