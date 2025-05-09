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
    private User user;

    public LoginSteps(TimeManager timeManager, ErrorMessageHolder errorMessage) {
        this.timeManager = timeManager;
        this.errorMessage = errorMessage;
    }

    @Given("the user {string} is registered")
    public void theUserIsRegistered(String user_initials) throws Exception {
        try {
            timeManager.addUser(new User(user_initials));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public void theUserIsLoggedIn(String userInitials) throws Exception {
        boolean initialsFound = timeManager.getUsers().stream()
                .anyMatch(user -> user.getUserInitials().equals(userInitials));
        if (!initialsFound){
            this.user = new User(userInitials);
            timeManager.addUser(user);
        }
        else {this.user = timeManager.getUserFromInitials(userInitials);}
        timeManager.setCurrentUser(user);
        assertNotNull(timeManager.getCurrentUser());
    }
}
